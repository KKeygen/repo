# 260519 JMeter 压测脚本

## 文件

- `create-order-v4.jmx`: 创建订单 V4 接口压测计划。
- `order-users.csv`: 1000 组压测用户与购票人数据。

## 压测接口

```text
POST http://127.0.0.1:6085/Dismai/program/program/order/create/v4
```

## 默认压测数据

```text
programId = 32
ticketCategoryId = 35
ticketCount = 1
```

## 执行命令

在仓库根目录执行：

```bash
mkdir -p STRESS-TEST/results/260519-v4

jmeter -n \
  -t STRESS-TEST/jmeter-260519/create-order-v4.jmx \
  -l STRESS-TEST/results/260519-v4/result.jtl \
  -e \
  -o STRESS-TEST/results/260519-v4/html \
  -Jthreads=100 \
  -Jramp_up=10 \
  -Jduration=60 \
  -Jcsv=STRESS-TEST/jmeter-260519/order-users.csv
```

## 参数

| 参数 | 默认值 | 含义 |
| --- | ---: | --- |
| `threads` | 100 | 并发线程数 |
| `ramp_up` | 10 | 启动线程爬坡时间，单位秒 |
| `duration` | 60 | 最大运行时间，单位秒 |
| `csv` | `STRESS-TEST/jmeter-260519/order-users.csv` | 压测数据文件 |

`order-users.csv` 设置为不循环读取，1000 行数据消耗完成后线程会自动停止。
