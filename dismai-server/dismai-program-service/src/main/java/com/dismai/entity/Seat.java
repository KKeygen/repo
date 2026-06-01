package com.dismai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dismai.data.BaseTableData;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@TableName("d_seat")
public class Seat extends BaseTableData implements Serializable {

    /**
     * 库存分片数。与 V4 下单策略 (ProgramOrderV4Strategy) 的
     * {@code ThreadLocalRandom.nextInt(SHARD_COUNT)} 保持一致。
     * 改这个值需要同步重算 Redis 中所有库存 key（实际生产应通过数据迁移）。
     */
    public static final int SHARD_COUNT = 10;

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 节目表id
     */
    private Long programId;

    /**
     * 节目票档id
     * */
    private Long ticketCategoryId;

    /**
     * 排号
     */
    private Integer rowCode;

    /**
     * 列号
     */
    private Integer colCode;

    /**
     * 座位类型 详见seatType枚举
     */
    private Integer seatType;

    /**
     * 座位价格
     */
    private BigDecimal price;

    /**
     * 1未售卖 2锁定 3已售卖
     */
    private Integer sellStatus;

    /**
     * 库存分片id（路由键，非持久化字段）。
     * <p>
     * shardId 是 {@code id mod SHARD_COUNT} 的纯函数，作为 Redis key / 分布式锁
     * 的分区路由使用。它不属于 seat 的固有属性，因此不存到 DB 列里 —— 避免
     * 真源漂移和 schema/代码不同步（参见 SQL 4 个 d_seat 表无 shard_id 列）。
     * <p>
     * MyBatis 通过 {@link TableField#exist()} 忽略该字段；seat id % 10 即可
     * 推导出归属分片。
     */
    @TableField(exist = false)
    private Integer shardId;

    /**
     * 计算分片 id：{@code id mod SHARD_COUNT}。
     * 用于路由/查询过滤；id 为空时返回 0。
     */
    public Integer getShardId() {
        if (id == null) {
            return 0;
        }
        return (int) Math.floorMod(id, SHARD_COUNT);
    }
}