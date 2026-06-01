#!/usr/bin/env bash
set -euo pipefail

cat <<'EOF'
IT-008 MySQL 异常注入骨架

目标:
  验证 MySQL 停止或网络隔离时，错误提示明确，恢复后可对账修正。

待完善步骤:
  1. 通过 docker compose 或容器编排停止 MySQL。
  2. 发起节目查询、下单或支付相关请求，记录错误提示与降级行为。
  3. 恢复 MySQL 后，检查数据一致性和对账修复入口。

备注:
  该脚本只输出执行计划，不会实际变更环境。
EOF
