# 后端本地启动流程与今日问题记录

## 1. IDEA 后端启动服务方法

### 基础依赖启动

本地开发时，后端微服务依赖 MySQL、Redis、Nacos、Kafka、Elasticsearch。建议先通过 Docker Desktop 启动基础设施，再用 IDEA 启动各个 Spring Boot 微服务。

Apple Silicon 机器需要指定 amd64 平台启动镜像：

```bash
DOCKER_DEFAULT_PLATFORM=linux/amd64 docker compose -f docker-compose.infra.yml up -d
```

查看基础设施状态：

```bash
docker compose -f docker-compose.infra.yml ps
```

所有容器应为 healthy 或 Up。Nacos 控制台地址：

```text
http://127.0.0.1:8848/nacos
```

如果第一次初始化 MySQL 时数据库表缺失，需要确认初始化脚本有执行权限：

```bash
chmod +x docker/mysql/init-databases.sh
```

重新初始化数据库时会清空 Docker volume：

```bash
docker compose -f docker-compose.infra.yml down -v
DOCKER_DEFAULT_PLATFORM=linux/amd64 docker compose -f docker-compose.infra.yml up -d
```

### Java 与 Maven 配置

项目后端使用 Java 17。IDEA 里需要统一配置为 JDK 17：

- `Project Structure -> Project -> SDK` 选择 JDK 17。
- `Project Structure -> Project -> Language level` 选择 17。
- `Settings -> Build, Execution, Deployment -> Build Tools -> Maven -> Importing` 使用 JDK 17。
- `Settings -> Build, Execution, Deployment -> Build Tools -> Maven -> Runner` 使用 JDK 17。

不要使用本机默认 JDK 24 启动后端服务，否则可能出现编译、运行或依赖兼容问题。

### IDEA Spring Boot 启动配置

在 IDEA 的 `Run/Debug Configurations` 中新增 Spring Boot 配置，每个后端服务一个配置。

已确认需要配置的主入口包括：

| 服务 | 启动类 |
| --- | --- |
| 网关服务 | `com.dismai.GatewayApplication` |
| 用户服务 | `com.dismai.UserApplication` |
| 节目/商品服务 | `com.dismai.ProgramApplication` |
| 订单服务 | `com.dismai.OrderApplication` |
| 支付服务 | `com.dismai.PayApplication` |
| 定制服务 | `com.dismai.CustomizeApplication` |
| 基础数据服务 | `com.dismai.BaseDataApplication` |

每个启动配置需要注意：

- JDK 选择 Java 17。
- classpath/module 选择对应的服务模块，例如 `dismai-gateway-service`、`dismai-user-service`。
- 主类选择对应的 `Application` 启动类。
- 本地开发建议统一增加环境变量：

```text
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:29092
```

用户服务验证码本地开发默认不强制真实验证码。如需强制开启验证码校验，可额外设置：

```text
ALWAYS_VERIFY_CAPTCHA=1
```

### 一键启动多个服务

所有单服务 Spring Boot 配置创建完成后，可以在 IDEA 中创建一个 Compound 配置，将以下服务加入同一个组合：

- `GatewayApplication`
- `UserApplication`
- `ProgramApplication`
- `OrderApplication`
- `PayApplication`
- `CustomizeApplication`
- `BaseDataApplication`

启动 Compound 配置后，所有服务会一起启动，并注册到 Nacos。可在 Nacos 控制台的服务列表中确认服务是否注册成功。

## 2. 今日发现的 Bug 与主要修改位置

### MySQL 初始化脚本未执行

现象：

- MySQL 容器启动后只有系统库，没有 `dismai_base_data`、`dismai_user_0`、`dismai_program_0` 等业务库。
- 后端启动时报 `Unknown database 'dismai_base_data'`。

原因：

- `docker/mysql/init-databases.sh` 没有执行权限，MySQL 初始化阶段跳过了业务库和 SQL 导入。

处理：

- 为初始化脚本增加执行权限。
- 删除旧 volume 后重新启动基础设施，让 MySQL 重新执行初始化脚本。

涉及位置：

```text
docker/mysql/init-databases.sh
```

### Program 服务 SQL 导入中断

现象：

- `ProgramApplication` 启动时报表不存在：
  - `d_program_show_time_0` 不存在
  - `d_program_category` 不存在
- MySQL 日志显示 SQL 初始化导入没有完整完成。

原因：

- `damai_program_0.sql`、`damai_program_1.sql` 中部分字段长度不足，导入测试数据时可能超过 `varchar(100)` 限制，导致后续表未导入。

处理：

- 将相关 `important_notice` 字段长度从 `varchar(100)` 调整为 `varchar(1024)`。

涉及位置：

```text
sql/cloud/damai_program_0.sql
sql/cloud/damai_program_1.sql
```

### Gateway 本地开发验签失败

现象：

- 前端请求经过网关时报 RSA 验签异常。
- 网关日志出现 `verifyRsaSign256 error`，并伴随 `NullPointerException`。

原因：

- 本地前端默认未启用签名，但请求没有稳定携带 `no_verify: true`。
- Gateway 仍按生产验签逻辑读取 `sign`、`code`、`businessBody`，字段缺失时触发异常。

处理：

- 前端请求拦截器在本地未启用签名时统一增加 `no_verify: true`。
- Gateway 验签前增加必要字段判断，避免空值导致 NPE。

涉及位置：

```text
vue3/src/utils/request.js
dismai-server/dismai-gateway-service/src/main/java/com/dismai/filter/RequestValidationFilter.java
```

### 注册接口缺少后端必填参数

现象：

- 注册请求到达 user-service 后参数校验失败。
- 日志提示 `captchaId`、`confirmPassword` 不能为空。

原因：

- 前端注册页提交参数与后端 `UserRegisterDto` 不一致。
- 前端未提交 `confirmPassword` 和 `captchaId`。

处理：

- 注册页补充提交 `confirmPassword`。
- 调用验证码检查接口后保存并提交 `captchaId`。
- 本地开发环境下验证码默认不强制真实校验。

涉及位置：

```text
vue3/src/views/Register.vue
dismai-server/dismai-user-service/src/main/resources/application.yml
```

### 登录接口后端成功但前端提示失败

现象：

- 直接通过 curl 调网关登录接口返回成功：

```json
{"code":"0","message":"","data":{"userId":"...","token":"..."}}
```

- 前端页面仍提示登录失败。

原因：

- 后端返回的 `code` 是字符串 `"0"`。
- 前端页面使用严格判断 `res.code === 0`，字符串 `"0"` 与数字 `0` 不相等。
- 后端返回字段为 `message`，而前端多处读取 `msg`。

处理：

- 在前端 axios 响应拦截器中统一规范化响应：
  - 数字字符串 `code` 转为 number。
  - 如果没有 `msg`，则用 `message` 兼容补齐。

涉及位置：

```text
vue3/src/utils/request.js
```

### 当前验证情况

- Docker 基础设施已能正常启动。
- MySQL 业务库和核心业务表已能正常初始化。
- 后端各服务可以通过 IDEA 启动并注册到 Nacos。
- 网关登录接口通过 curl 验证成功。
- 前端构建已通过：

```bash
npm --prefix vue3 run build
```

构建过程中仍有 Sass legacy API deprecation warning，但不影响当前构建结果。

## 3. 分支说明

今日相关修复已 push 到个人分支：

```text
feature/hyc/personal-branch
```

