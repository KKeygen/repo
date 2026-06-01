# 测试骨架索引

> 本目录只收录已补齐但尚未实际执行的集成测试与故障注入骨架。

## 集成测试骨架

| 编号 | 场景 | 骨架位置 | 说明 |
| --- | --- | --- | --- |
| IT-001 | 登录与鉴权 | [UserAuthIntegrationSkeletonTest.java](../../dismai-server/dismai-user-service/src/test/java/com/dismai/integration/UserAuthIntegrationSkeletonTest.java) | 预留 `POST /user/login` 和鉴权校验入口 |
| IT-002 | 节目查询 | [ProgramQueryIntegrationSkeletonTest.java](../../dismai-server/dismai-program-service/src/test/java/com/dismai/integration/ProgramQueryIntegrationSkeletonTest.java) | 预留节目列表、详情、票档和座位查询入口 |
| IT-003 | 锁座下单 | [OrderFlowIntegrationSkeletonTest.java](../../dismai-server/dismai-order-service/src/test/java/com/dismai/integration/OrderFlowIntegrationSkeletonTest.java) | 预留下单、锁座和消息发送入口 |
| IT-004 | 订单超时取消 | [OrderTimeoutIntegrationSkeletonTest.java](../../dismai-server/dismai-order-service/src/test/java/com/dismai/integration/OrderTimeoutIntegrationSkeletonTest.java) | 预留超时释放座位与库存入口 |
| IT-005 | 支付回调 | [PayCallbackIntegrationSkeletonTest.java](../../dismai-server/dismai-pay-service/src/test/java/com/dismai/integration/PayCallbackIntegrationSkeletonTest.java) | 预留支付通知与订单状态更新入口 |

## 故障注入脚本

| 编号 | 场景 | 脚本位置 | 说明 |
| --- | --- | --- | --- |
| IT-006 | Kafka 异常 | [it-006-kafka-fault-injection.sh](scripts/it-006-kafka-fault-injection.sh) | 预留 Kafka 停止、恢复和重试验证步骤 |
| IT-007 | Redis 异常 | [it-007-redis-fault-injection.sh](scripts/it-007-redis-fault-injection.sh) | 预留 Redis 停止、恢复和防重复售卖验证步骤 |
| IT-008 | MySQL 异常 | [it-008-mysql-fault-injection.sh](scripts/it-008-mysql-fault-injection.sh) | 预留 MySQL 停止、恢复和对账验证步骤 |

## 约束

1. 这些骨架只描述测试入口和校验方向，不代表已完成端到端联调。
2. 当前仓库中未实际执行这些集成测试和故障注入脚本。
3. 后续若补齐中间件和测试数据，可直接在这些文件上继续完善断言与执行逻辑。
