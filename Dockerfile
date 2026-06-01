# ============================================================
# Stage 1: Build all Maven modules
# ============================================================
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /build
COPY . .

RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -Dmaven.test.skip=true -B -q \
    && echo "==> Maven build complete"

# Collect ShardingSphere config templates for runtime patching
RUN mkdir -p /sharding-templates \
    && find /build/dismai-server -path "*/src/main/resources/shardingsphere-*-local.yaml" \
       -exec cp {} /sharding-templates/ \;

# ============================================================
# Stage 2: Runtime image
# ============================================================
FROM eclipse-temurin:17-jre-jammy

ARG SERVICE_NAME
ARG SERVICE_PORT=8080

LABEL maintainer="dismai" \
      service="${SERVICE_NAME}"

RUN groupadd -r appuser && useradd -r -g appuser appuser \
    && mkdir -p /app/config /app/sharding-templates /app/logs /home/appuser/logs/csp \
    && chown -R appuser:appuser /app /home/appuser

WORKDIR /app

# Copy the target service JAR
COPY --from=build /build/dismai-server/dismai-${SERVICE_NAME}-service/target/dismai-${SERVICE_NAME}-service-0.0.1-SNAPSHOT.jar /app/app.jar

# Copy ShardingSphere templates (used by entrypoint for sharded services)
COPY --from=build /sharding-templates/ /app/sharding-templates/

# Copy entrypoint script (fix CRLF in case of Windows checkout)
COPY docker/entrypoint.sh /app/entrypoint.sh
RUN sed -i 's/\r$//' /app/entrypoint.sh && chmod +x /app/entrypoint.sh && chown -R appuser:appuser /app

USER appuser

EXPOSE ${SERVICE_PORT}

ENTRYPOINT ["/app/entrypoint.sh"]
