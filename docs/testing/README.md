# 测试索引

> 本目录收录已补齐的集成测试与故障注入脚本。默认会通过环境变量跳过真实联调，避免在缺少中间件时误改环境。

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
| IT-006 | Kafka 异常 | [it-006-kafka-fault-injection.sh](scripts/it-006-kafka-fault-injection.sh) | 默认 dry-run，`RUN_FAULT_TESTS=true` 时停止/恢复 Kafka |
| IT-007 | Redis 异常 | [it-007-redis-fault-injection.sh](scripts/it-007-redis-fault-injection.sh) | 默认 dry-run，`RUN_FAULT_TESTS=true` 时停止/恢复 Redis |
| IT-008 | MySQL 异常 | [it-008-mysql-fault-injection.sh](scripts/it-008-mysql-fault-injection.sh) | 默认 dry-run，`RUN_FAULT_TESTS=true` 时停止/恢复 MySQL |

## 约束

1. 这些测试默认通过 `RUN_INTEGRATION_TESTS=true` 才会进入真实执行路径。
2. 故障注入脚本默认只打印计划，不会修改 compose 环境，除非显式设置 `RUN_FAULT_TESTS=true`。
3. 真实联调所需的中间件、URL 路径和测试数据仍由环境变量提供；仓库内保留可执行断言和安全脚本入口。
