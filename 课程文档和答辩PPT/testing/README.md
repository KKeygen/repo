# 测试索引

> 本目录收录已完成的集成测试与故障注入脚本，用于课程大作业测试代码提交和测试报告索引。

## 集成测试入口

| 编号 | 场景 | 测试位置 | 说明 |
| --- | --- | --- | --- |
| IT-001 | 登录与鉴权 | [UserAuthIntegrationTest.java](../../dismai-server/dismai-user-service/src/test/java/com/dismai/integration/UserAuthIntegrationTest.java) | 登录后检查 token/cookie/mobile 等鉴权返回字段 |
| IT-002 | 节目查询 | [ProgramQueryIntegrationTest.java](../../dismai-server/dismai-program-service/src/test/java/com/dismai/integration/ProgramQueryIntegrationTest.java) | 节目列表、详情、票档和座位数据断言 |
| IT-003 | 锁座下单 | [OrderFlowIntegrationTest.java](../../dismai-server/dismai-order-service/src/test/java/com/dismai/integration/OrderFlowIntegrationTest.java) | 创建订单并再次提交相同请求，验证重复锁座被拒绝 |
| IT-004 | 订单超时取消 | [OrderTimeoutIntegrationTest.java](../../dismai-server/dismai-order-service/src/test/java/com/dismai/integration/OrderTimeoutIntegrationTest.java) | 触发超时取消入口并验证订单状态变更 |
| IT-005 | 支付回调 | [PayCallbackIntegrationTest.java](../../dismai-server/dismai-pay-service/src/test/java/com/dismai/integration/PayCallbackIntegrationTest.java) | 提交支付回调并查询订单支付状态 |

## 故障注入脚本

| 编号 | 场景 | 脚本位置 | 说明 |
| --- | --- | --- | --- |
| IT-006 | Kafka 异常 | [it-006-kafka-fault-injection.sh](scripts/it-006-kafka-fault-injection.sh) | 验证 Kafka 停止与恢复场景 |
| IT-007 | Redis 异常 | [it-007-redis-fault-injection.sh](scripts/it-007-redis-fault-injection.sh) | 验证 Redis 停止与恢复场景 |
| IT-008 | MySQL 异常 | [it-008-mysql-fault-injection.sh](scripts/it-008-mysql-fault-injection.sh) | 验证 MySQL 停止与恢复场景 |

## 约束

1. 集成测试覆盖登录、节目查询、锁座下单、订单超时取消和支付回调链路。
2. 故障注入测试覆盖 Kafka、Redis、MySQL 三类关键中间件异常。
3. 仓库内保留测试断言、脚本入口和测试报告，便于课程评审检查。
