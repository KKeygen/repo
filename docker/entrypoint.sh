#!/bin/bash
set -e

MYSQL_HOST="${MYSQL_HOST:-127.0.0.1}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
MYSQL_USER="${MYSQL_USER:-root}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-root}"

# For services using ShardingSphere, patch the config to use Docker hostnames.
# ShardingSphere JDBC uses "classpath:" scheme by default (reads from inside JAR),
# so we patch the file and override spring.datasource.url to use "absolutepath:" scheme
# which reads from the filesystem instead.
if [ -n "$SHARDING_CONFIG_NAME" ]; then
  TEMPLATE="/app/sharding-templates/${SHARDING_CONFIG_NAME}"
  if [ -f "$TEMPLATE" ]; then
    mkdir -p /app/config
    sed -e "s|127\.0\.0\.1:3306|${MYSQL_HOST}:${MYSQL_PORT}|g" \
        -e "s|127\.0\.0\.1|${MYSQL_HOST}|g" \
        -e "s|password: root|password: ${MYSQL_PASSWORD}|g" \
        -e "s|username: root|username: ${MYSQL_USER}|g" \
        "$TEMPLATE" > "/app/config/${SHARDING_CONFIG_NAME}"
    # Override datasource URL to load patched config from filesystem
    export SPRING_DATASOURCE_URL="jdbc:shardingsphere:absolutepath:/app/config/${SHARDING_CONFIG_NAME}"
    echo "==> ShardingSphere config patched: ${SHARDING_CONFIG_NAME} (MySQL at ${MYSQL_HOST}:${MYSQL_PORT})"
    echo "==> Datasource URL overridden to: ${SPRING_DATASOURCE_URL}"
  else
    echo "ERROR: ShardingSphere template not found: ${TEMPLATE}" >&2
    echo "ERROR: Available templates:" >&2
    ls -la /app/sharding-templates/ >&2 || true
    exit 1
  fi
fi

exec java ${JAVA_OPTS} -jar /app/app.jar "$@"
