#!/usr/bin/env bash
set -euo pipefail

SERVICE_NAME="${REDIS_SERVICE_NAME:-redis}"
COMPOSE_FILE="${COMPOSE_FILE:-docker-compose.yml}"
ORDER_URL="${DISMAI_ORDER_CREATE_URL:-http://127.0.0.1:6084/Dismai/program/program/order/create/v4}"
RUN_FAULT_TESTS="${RUN_FAULT_TESTS:-false}"

ORDER_BODY="${DISMAI_ORDER_CREATE_BODY:-{\"userId\":\"2181859000000000001\",\"programId\":\"2181859535445270528\",\"ticketCategoryId\":\"2181859569805017088\",\"ticketCount\":1,\"ticketUserIdList\":[\"2181859000000000002\"],\"code\":\"0001\"}}"

run_or_print() {
  if [[ "${RUN_FAULT_TESTS}" == "true" ]]; then
    "$@"
  else
    printf '[dry-run]'
    printf ' %q' "$@"
    printf '\n'
  fi
}

echo "IT-007 Redis 异常注入"
echo "目标：验证 Redis 不可用时下单写链路失败且不绕过锁座状态机。"
echo "默认 dry-run；设置 RUN_FAULT_TESTS=true 才会修改 docker compose 环境。"

run_or_print docker compose -f "${COMPOSE_FILE}" stop "${SERVICE_NAME}"
run_or_print curl -sS -X POST "${ORDER_URL}" -H "Content-Type: application/json" -d "${ORDER_BODY}"
run_or_print docker compose -f "${COMPOSE_FILE}" start "${SERVICE_NAME}"
run_or_print docker compose -f "${COMPOSE_FILE}" logs --tail=120 "${SERVICE_NAME}"

echo "人工验收点：请求明确失败；不会创建绕过 Redis 的订单；恢复后可重新初始化或读取缓存。"
