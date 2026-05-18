# EaxonFix260519 待完善事项清单
负责人：@Eaxon
记录日期：2026-05-19

负责范围：

- `dismai-server/dismai-order-service`
- `dismai-server/dismai-pay-service`
- `dismai-redis-tool-framework`
- `dismai-redisson-framework`

## P0 必须优先修复

### 1. 修复支付金额字段精度

涉及位置：

- `sql/cloud/damai_pay_0.sql`
- `sql/cloud/damai_pay_1.sql`

当前问题：

- `d_pay_bill_*`.`pay_amount` 使用 `decimal(10,0)`。
- `d_refund_bill_*`.`refund_amount` 使用 `decimal(10,0)`。
- 没有小数位，票价或退款金额存在小数时会导致金额被截断或四舍五入。

影响：

- 支付金额入库失真。
- 支付回调金额校验可能失败。
- 主动查单对账可能误判。

待完善：

- 将支付金额和退款金额字段调整为支持小数的金额类型，例如 `decimal(10,2)` 或按业务金额上限重新设计精度。
- 同步检查实体、DTO、支付参数、测试数据是否一致。

### 2. 修复异步建单 Kafka 自动提交导致的丢单风险

涉及位置：

- `dismai-server/dismai-order-service/src/main/resources/application.yml`
- `dismai-server/dismai-order-service/src/main/java/com/dismai/service/kafka/CreateOrderConsumer.java`
- `dismai-server/dismai-program-service/src/main/java/com/dismai/service/ProgramOrderService.java`

当前问题：

- Kafka consumer 配置 `enable-auto-commit: true`。
- 消息消费失败时虽然抛出异常，但 offset 可能已经自动提交。
- Program 服务已经完成锁座和扣减余票后，如果订单服务消费失败且 offset 已提交，会出现库存锁定但订单未落库。

影响：

- 秒杀链路可能出现“扣库存成功、订单不存在”。
- 后续延迟取消无法正确释放订单关联资源。

待完善：

- 改为手动提交 offset。
- 只有订单落库成功、相关缓存标记成功后再提交。
- 消费失败要支持重试、死信或补偿释放锁座。
- 明确异步下单返回订单号后的查询策略，避免前端误认为订单已落库。

### 3. 修复身份证防刷 Hash 取消后不释放的问题

涉及位置：

- `dismai-server/dismai-program-service/src/main/resources/lua/programDataCreateOrderResolution.lua`
- `dismai-server/dismai-order-service/src/main/resources/lua/OrderProgramDataResolution.lua`
- `dismai-server/dismai-order-service/src/main/java/com/dismai/service/OrderService.java`

当前问题：

- 下单 Lua 会写入 `PROGRAM_ID_CARD:{programId}`。
- 订单取消、建单失败、MQ 消费超时释放库存时，没有删除对应身份证占用。

影响：

- 用户未支付取消后，同一身份证仍可能被判定为重复购票。
- 秒杀失败补偿不完整。

待完善：

- 订单创建时保存订单和购票人身份证之间的可追溯关系。
- 取消订单和建单失败补偿时同步释放身份证占用。
- Lua 反向恢复脚本增加身份证释放逻辑，或设计单独的身份证占用释放脚本。

## P1 高优先级修复

### 4. 修复 PayService.tradeCheck 账单不存在时可能误导订单状态

涉及位置：

- `dismai-server/dismai-pay-service/src/main/java/com/dismai/service/PayService.java`
- `dismai-server/dismai-order-service/src/main/java/com/dismai/service/OrderService.java`

当前问题：

- `tradeCheck()` 查询三方成功后复制结果到 `TradeCheckVo`。
- 如果本地 `PayBill` 不存在，只记录日志并返回。
- 订单服务只判断 `tradeCheckVo.isSuccess()`，可能在本地账单不存在时继续处理订单状态。

影响：

- 订单状态可能被错误更新。
- 支付账单和订单状态可能不一致。

待完善：

- 本地账单不存在时明确返回失败状态或业务错误码。
- 订单服务在查单结果中同时校验账单存在、金额一致、状态可信。

### 5. 完善支付回调并发幂等控制

涉及位置：

- `dismai-server/dismai-pay-service/src/main/java/com/dismai/service/PayService.java`
- `dismai-server/dismai-order-service/src/main/java/com/dismai/service/OrderService.java`

当前问题：

- `PayService.notify()` 没有按订单号加锁。
- 更新支付账单时只按 `out_order_no` 更新，没有限制当前状态必须为未支付。
- 并发回调、主动查单、退款流程可能交叉更新状态。

影响：

- 已取消或已退款账单可能被回调重新改为已支付。
- 支付账单状态流转不严格。

待完善：

- 给支付回调增加订单号维度的分布式锁。
- 更新条件增加当前状态约束，例如只允许 `NO_PAY -> PAY`。
- 对重复回调直接幂等返回成功。
- 梳理 `NO_PAY / PAY / CANCEL / REFUND` 的合法状态流转。

### 6. 修复退款本地落库事务边界

涉及位置：

- `dismai-server/dismai-pay-service/src/main/java/com/dismai/service/PayService.java`

当前问题：

- `refund()` 没有事务注解。
- `refund()` 内部调用同类方法 `updateRefundBill()`，`@Transactional` 不会通过 Spring 代理生效。

影响：

- 三方退款成功后，本地支付账单更新或退款单插入失败时，数据可能不一致。

待完善：

- 将退款本地落库放入明确事务边界。
- 避免同类自调用事务失效。
- 退款成功后的本地更新和退款单插入必须原子化。
- 增加重复退款幂等判断。

### 7. 修复 RedisCacheImpl 列表缓存命中返回 null

涉及位置：

- `dismai-redis-tool-framework/dismai-redis-framework/src/main/java/com/dismai/redis/RedisCacheImpl.java`

当前问题：

- `getValueIsList(redisKeyBuild, clazz, supplier, ttl, timeUnit)` 在缓存命中时没有反序列化赋值。
- 方法最后返回的 `tList` 仍可能是 `null`。

影响：

- 使用该方法的缓存旁路逻辑在缓存命中时反而拿不到数据。
- 可能造成重复查库或空数据异常。

待完善：

- 缓存命中时解析 `valueStr` 并返回列表。
- 缓存未命中时才调用 supplier。
- 为该方法补充单元测试。

### 8. 完善 Redis Stream 消费失败恢复机制

涉及位置：

- `dismai-redis-tool-framework/dismai-redis-stream-framework/src/main/java/com/dismai/RedisStreamListener.java`
- `dismai-redis-tool-framework/dismai-redis-stream-framework/src/main/java/com/dismai/config/RedisStreamAutoConfig.java`

当前问题：

- 消费异常时只记录日志，不 ack。
- 没有 pending list 重试、claim、死信队列或告警机制。
- 使用 `ReadOffset.lastConsumed()`，异常消息可能长期停留在 pending。

影响：

- 消息卡住后业务不再推进。
- 服务重启也不一定自动恢复异常消息。

待完善：

- 增加 pending 消息扫描和重试机制。
- 超过重试次数进入死信队列。
- 补充消费失败告警日志和监控指标。

## P2 中优先级完善

### 9. 为内部接口增加服务间调用约束

涉及位置：

- `dismai-server/dismai-order-service/src/main/java/com/dismai/controller/OrderController.java`
- `dismai-server/dismai-pay-service/src/main/java/com/dismai/controller/PayController.java`

当前问题：

- 部分接口注释说明“不提供给前端调用”，但 Controller 层没有来源校验。
- 服务自身没有内部调用鉴权兜底。

影响：

- 如果网关路由或白名单配置不严，内部接口可能被外部调用。

待完善：

- 明确内部接口调用标识或服务间鉴权方案。
- 网关路由层和服务层都应有约束。
- 至少对创建订单、支付、回调、退款等关键接口增加调用来源校验。

### 10. 清理敏感配置与环境配置

涉及位置：

- `dismai-server/dismai-pay-service/src/main/resources/application.yml`
- `dismai-server/dismai-pay-service/src/main/resources/shardingsphere-pay-local.yaml`
- `dismai-server/dismai-pay-service/src/main/resources/shardingsphere-pay-pro.yaml`
- `dismai-server/dismai-order-service/src/main/resources/shardingsphere-order-local.yaml`
- `dismai-server/dismai-order-service/src/main/resources/shardingsphere-order-pro.yaml`

当前问题：

- 支付宝 appId、sellerId、商户私钥、公钥直接放在配置文件。
- 数据库用户名密码直接写入配置。
- `PayService` 中硬编码 `payScene = "生产"`。

影响：

- 不利于环境隔离。
- 不利于密钥管理。
- 本地、测试、生产配置容易混用。

待完善：

- 敏感配置改为环境变量、Nacos 配置中心或密钥管理服务注入。
- `payScene` 改为配置项。
- 区分 local、dev、test、prod 的支付配置。

### 11. 补充订单、支付、高并发框架测试

涉及范围：

- 订单服务建单、取消、支付状态流转。
- 支付服务支付、回调、查单、退款。
- Redis Lua 扣库存、锁座、反向恢复。
- Redisson 锁和重复执行限制。
- Redis Stream 消费异常恢复。

当前问题：

- 当前负责模块下未发现 `src/test` 测试文件。

影响：

- 后续改动容易引入秒杀链路回归。
- 高并发场景缺少可重复验证手段。

待完善：

- 补充核心单元测试。
- 补充集成测试。
- 补充并发压测脚本或最小可复现测试。

## 当前完成度判断

订单服务：

- 已具备建单、异步建单、延迟取消、支付、支付回调、订单查询、缓存反向恢复等主流程。
- 当前属于“功能链路基本可跑，但可靠性和补偿闭环不足”。

支付服务：

- 已具备支付宝支付、验签回调、主动查单、退款、账单详情等能力。
- 当前核心风险集中在金额精度、状态幂等、退款事务、敏感配置管理。

高并发支撑框架：

- 已有 Redis Lua、Redisson 分布式锁、重复执行限制、延迟队列、Redis Stream 等基础能力。
- 当前还需要完善异常恢复、补偿机制、测试覆盖和高并发验证。

