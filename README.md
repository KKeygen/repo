# 万花筒 (Dismai) — 高并发在线票务服务系统

## 项目简介

万花筒是一个基于微服务架构的高并发在线票务服务系统，支持演唱会、话剧歌剧、体育比赛、儿童亲子等多种节目类型的在线订票。用户可注册登录后选择节目和座位进行购票、支付及订单查询。

本项目为课程作业项目，旨在学习和实践高并发分布式微服务系统的设计与实现。

## 技术栈

| 层次 | 技术 |
|------|------|
| 核心框架 | Spring Boot 3.3 + Spring Cloud 2023 + Spring Cloud Alibaba |
| 服务注册/配置 | Nacos |
| 分布式缓存 | Redis（Lua脚本、延迟队列、Stream） |
| 消息队列 | Kafka |
| 搜索引擎 | ElasticSearch |
| 数据库 | MySQL 8 + ShardingSphere（分库分表） |
| 分布式锁 | Redisson |
| 网关 | Spring Cloud Gateway |
| 前端 | Vue 3 + Vite |
| 语言 | Java 17 |

## 项目架构

```
dismai/
├── dismai-common                     # 公共模块（工具类、通用常量、异常处理）
├── dismai-server/                    # 微服务集合
│   ├── dismai-gateway-service        # API网关服务
│   ├── dismai-user-service           # 用户服务
│   ├── dismai-program-service        # 节目服务
│   ├── dismai-order-service          # 订单服务
│   ├── dismai-pay-service            # 支付服务
│   ├── dismai-base-data-service      # 基础数据服务
│   ├── dismai-customize-service      # 自定义规则服务
│   └── dismai-admin-service          # 管理后台服务
├── dismai-server-client/             # 服务间调用客户端（Feign）
├── dismai-redis-tool-framework/      # Redis工具框架
├── dismai-redisson-framework/        # Redisson分布式锁框架
├── dismai-elasticsearch-framework/   # ES搜索框架
├── dismai-spring-cloud-framework/    # Spring Cloud扩展框架
├── dismai-id-generator-framework/    # 分布式ID生成器
├── dismai-thread-pool-framework/     # 线程池管理框架
├── dismai-captcha-manage-framework/  # 验证码管理框架
├── vue3/                             # 前端工程
└── sql/                              # 数据库初始化脚本
```

## 核心功能

- **用户管理**：注册、登录、JWT鉴权、购票人管理
- **节目管理**：节目发布、分类、搜索（ES）、详情展示
- **订单管理**：下单、多版本高并发优化、订单状态管理
- **支付管理**：支付宝集成、支付回调处理
- **座位管理**：选座购票、座位锁定
- **基础数据**：地区管理、通用字典

## 技术亮点

- **多版本订单生成优化**：提供4种订单创建方案，逐步优化高并发性能
- **多级缓存架构**：本地缓存 + 分布式缓存（Redis），解决缓存穿透/击穿/雪崩
- **分库分表**：基于ShardingSphere的用户、订单、支付、节目表水平拆分
- **分布式锁**：Redisson实现的分布式锁，防止超卖
- **布隆过滤器**：用户注册防重复、缓存穿透防护
- **延迟队列**：Redis延迟队列 + Kafka实现超时未支付自动取消
- **灰度发布**：基于自定义标签的服务灰度路由

## 快速开始

### 环境要求
- JDK 17+
- MySQL 8.0+
- Redis 7+
- Nacos 2.3+
- Kafka 3.x
- ElasticSearch 8.x
- Node.js 18+

### 后端启动
```bash
# 1. 初始化数据库
mysql -u root -p < sql/cloud/1_dismai_cloud_create_database.sql

# 2. 启动Nacos、Redis、Kafka、ES

# 3. 按顺序启动各微服务
# Gateway -> User -> Program -> Order -> Pay -> BaseData -> Customize
```

### 前端启动
```bash
cd vue3
npm install
npm run dev
```

## 许可证

本项目仅用于课程学习，请勿用于商业用途。