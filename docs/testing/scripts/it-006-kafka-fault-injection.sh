#!/usr/bin/env bash
set -euo pipefail

cat <<'EOF'
IT-006 Kafka 异常注入骨架

目标:
  验证 Kafka 停止或网络隔离时，发送失败能够回滚锁座，消费失败可以重试。

待完善步骤:
  1. 通过 docker compose 或容器编排停止 Kafka。
  2. 发起锁座下单请求，记录失败返回和回滚路径。
  3. 恢复 Kafka 后，验证重试链路与补偿消息消费情况。

备注:
  该脚本只输出执行计划，不会实际变更环境。
EOF
