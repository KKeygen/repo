#!/usr/bin/env bash
set -euo pipefail

SERVICE_NAME="${MYSQL_SERVICE_NAME:-mysql}"
COMPOSE_FILE="${COMPOSE_FILE:-docker-compose.yml}"
PROGRAM_URL="${DISMAI_PROGRAM_QUERY_URL:-http://127.0.0.1:6084/Dismai/program/program/list}"
RUN_FAULT_TESTS="${RUN_FAULT_TESTS:-false}"

run_or_print() {
  if [[ "${RUN_FAULT_TESTS}" == "true" ]]; then
    "$@"
  else
    printf '[dry-run]'
    printf ' %q' "$@"
    printf '\n'
  fi
}

echo "IT-008 MySQL 异常注入"
echo "目标：验证 MySQL 不可用时系统返回明确错误，恢复后可继续查询并执行对账。"
echo "默认 dry-run；设置 RUN_FAULT_TESTS=true 才会修改 docker compose 环境。"

run_or_print docker compose -f "${COMPOSE_FILE}" stop "${SERVICE_NAME}"
run_or_print curl -sS "${PROGRAM_URL}"
run_or_print docker compose -f "${COMPOSE_FILE}" start "${SERVICE_NAME}"
run_or_print docker compose -f "${COMPOSE_FILE}" logs --tail=120 "${SERVICE_NAME}"

echo "人工验收点：数据库故障期间错误可解释；恢复后节目查询可恢复；订单与库存可对账。"
