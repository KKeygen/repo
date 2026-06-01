# Dismai 并发性能测试报告

## 测试环境
- CPU: 64核 | 内存: 503GB | OS: Ubuntu 22.04
- 部署: Docker Compose 单节点全栈
- 工具: wrk (williamyeh/wrk Docker) + Python requests (Session pool)
- 日期: 2026-05-24

## 测试数据
- 节目: 1个, 票档: 2个 (100+50), 座位: 150个
- 注册用户: 455人, 每人1个独立购票人(唯一身份证)

---

## Phase 1: 基准延迟 (10次取样均值)

| API | avg | min | max |
|-----|-----|-----|-----|
| 登录 /user/login | 20ms | 14ms | 28ms |
| 详情 /program/detail | 40ms | 26ms | 119ms |
| 分类 /program/page | 75ms | 50ms | 238ms |
| 首页 /program/home/list | 100ms | 53ms | 472ms |

---

## Phase 2: 读并发

### wrk - nginx静态首页
| 并发 | 持续时间 | 总请求 | TPS | avg | p99 | 成功率 |
|------|---------|--------|-----|-----|-----|--------|
| 200c/8t | 30s | 7,634,376 | **253,834** | 0.93ms | 4.57ms | 100% |

### Python - 详情API
| 并发 | 总请求 | TPS | avg | p99 | 成功率 |
|------|--------|-----|-----|-----|--------|
| 50 workers | 250 | **412** | 63ms | 179ms | 100% |
| 100 workers | 500 | **425** | 73ms | 260ms | 100% |
| 200 workers | 1000 | **414** | 74ms | 289ms | 100% |

---

## Phase 3: 写并发——下单 (wrk 8t/c50/30s × 5轮)

| 连接数 | 总请求 | TPS | avg延迟 | p99 | timeout | 成功订单 |
|--------|--------|-----|---------|-----|---------|----------|
| 10c | 2,238 | 74.52 | - | - | 0 | - |
| 25c | 2,229 | 74.06 | - | - | 0 | - |
| 50c | 2,226 | 74.11 | 640ms | 1.23s | 0 | 126 |
| 100c | 2,110 | 70.10 | - | - | 432 | - |

> TPS恒定~70-74，与连接数无关 → 系统是锁限流型。

### 瓶颈分析

下单锁链路:
```
@RepeatExecuteLimit(userId, programId)           Redis令牌桶, 单用户防刷
  └─ localLockCreateOrder()                       JVM ReentrantLock, 按票档串行 (瓶颈)
       └─ @ServiceLock(SEAT_LOCK, pid, tcId)      Redis分布式锁
            └─ Lua原子脚本                         座位分配
                 └─ Kafka                         异步消费落库
```

- 瓶颈: JVM ReentrantLock 按 {pid}-{ticketCategoryId} 单JVM串行化
- 优化方向: 库存分片——同一票档拆N个分片，锁粒度降为 {pid}-{tcId}-{shardId}
- 多实例部署: 每个实例独立本地锁，TPS可线性扩展

### 可靠性

| 测试项 | 结果 |
|--------|------|
| 不超卖 | ✅ 库存扣减准确 |
| ID Card去重 | ✅ PROGRAM_ID_CARD hash 防重 |
| 50c持续30s | ✅ 无崩溃、无HTTP错误 |
| 100c过载 | ⚠️ 432超时，优雅降级 |

## 脚本留存

```
bench_prep.py      - 批量注册用户+购票人
bench_order.lua    - wrk Lua 下单脚本
bench.py           - Phase 1/2 完整脚本
bench_p3_v2.py     - Phase 3 阶梯并发脚本
```

## 提交记录

| commit | 说明 |
|--------|------|
| 8708819 | debug: showTime诊断日志 |
| 7ab2c0d | fix: batchAdd整数除零 / JSON精度 / SETEX |
| 1f4d279 | fix: ticketCategoryId JSON精度丢失 |
| 255eeb3 | fix: 网关no_verify解析token |
| da8da55 | fix: code默认值修复 |
| eb0bb95 | fix: BloomFilter add/contains日志 |
| cbf9a47 | fix: program_group_id DEFAULT 0 |
| fc0eb0e | perf: BuildKit构建缓存 |
