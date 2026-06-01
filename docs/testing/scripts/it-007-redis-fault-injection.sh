#!/usr/bin/env bash
set -euo pipefail

cat <<'EOF'
IT-007 Redis 异常注入骨架

目标:
  验证 Redis 停止或网络隔离时，下单写链路停止，不产生重复售卖。

待完善步骤:
  1. 通过 docker compose 或容器编排停止 Redis。
  2. 发起锁座或下单请求，记录明确的失败提示。
  3. 恢复 Redis 后，检查写链路恢复与幂等行为。

备注:
  该脚本只输出执行计划，不会实际变更环境。
EOF
