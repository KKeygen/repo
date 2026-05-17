# DongxinL 网关与服务治理运行说明

本文档记录网关、灰度治理和 Docker 编排的本地运行要点，便于小组联调和验收。

## 网关入口

- 网关服务端口：`6085`
- 健康检查：`http://localhost:6085/actuator/health`
- 统一路由前缀：`/Dismai/{service}/**`
- 示例路由：`/Dismai/user/**`、`/Dismai/order/**`、`/Dismai/program/**`

网关配置位于 `dismai-server/dismai-gateway-service/src/main/resources/application.yml` 和 `application-pro.yml`。`application-pro.yml` 负责声明 Gateway 路由，容器环境通过 `docker-compose.yml` 覆盖 Nacos、Redis、Kafka 和 Sentinel 地址。

## 灰度治理

灰度路由使用请求头 `gray` 控制：

- `gray: true` 优先选择注册元数据为 `gray=true` 的服务实例。
- `gray: false` 或不传该请求头时，默认选择非灰度实例。
- 当请求灰度实例但注册中心没有灰度实例时，网关会回退到普通实例，保证联调可用性。

服务实例灰度标记来自 Nacos 元数据：

```yaml
spring:
  cloud:
    nacos:
      discovery:
        metadata:
          gray: false
```

## 网关限流

全局并发限流配置如下：

```yaml
rate:
  switch: false
  permits: 200
```

- `rate.switch=true` 时开启网关全局限流。
- `rate.permits` 控制可同时进入网关校验链路的请求数量。
- 接口级风控限流由 `api.limit.paths` 和 Redis Lua 脚本共同控制。

## Docker 启动

启动完整环境：

```bash
docker compose up -d
```

仅启动基础设施：

```bash
docker compose -f docker-compose.infra.yml up -d
```

查看网关日志：

```bash
docker compose logs -f gateway
```

验证 Compose 配置：

```bash
docker compose config
```

## 联调检查

1. `docker compose config` 能正常输出合并后的配置。
2. `dismai-nacos`、`dismai-redis`、`dismai-kafka`、`dismai-sentinel` 处于 healthy 状态。
3. `dismai-gateway` 启动日志无 Bean 初始化失败或配置绑定失败。
4. `http://localhost:6085/actuator/health` 返回可访问结果。
