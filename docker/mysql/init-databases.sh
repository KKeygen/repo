#!/bin/bash
#
# MySQL initialization script for Dismai microservices.
# Executed automatically by the MySQL Docker entrypoint on first start.
#
set -e

MYSQL_CMD="mysql -u root -p${MYSQL_ROOT_PASSWORD} --default-character-set=utf8mb4"
SQL_DIR="/docker-entrypoint-initdb.d/sql"

echo "============================================"
echo "  Dismai Database Initialization"
echo "============================================"

# Step 1: Create all databases
echo "==> Creating databases..."
${MYSQL_CMD} < "${SQL_DIR}/1_damai_cloud_create_database.sql"
echo "    Done: 10 databases created."

# Step 2: Import table schemas into each database
# The SQL files already contain USE statements targeting the correct database.
declare -a SQL_FILES=(
  "damai_base_data.sql"
  "damai_customize.sql"
  "damai_user_0.sql"
  "damai_user_1.sql"
  "damai_program_0.sql"
  "damai_program_1.sql"
  "damai_order_0.sql"
  "damai_order_1.sql"
  "damai_pay_0.sql"
  "damai_pay_1.sql"
)

for sql_file in "${SQL_FILES[@]}"; do
  if [ -f "${SQL_DIR}/${sql_file}" ]; then
    echo "==> Importing ${sql_file}..."
    ${MYSQL_CMD} < "${SQL_DIR}/${sql_file}"
  else
    echo "WARN: ${sql_file} not found, skipping."
  fi
done

echo "============================================"
echo "  Database initialization complete!"
echo "============================================"
