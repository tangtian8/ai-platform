package top.tangtian.balofhope.dlt.analyze.entity;

/**
 * @author tangtian
 * @date 2026-01-23 09:08
 */

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ============================================================
 * 2. 奖池趋势分析结果表实体
 * ============================================================
 */
@Data
@TableName("dlt_pool_trend")
public class DltPoolTrendEntity implements Serializable {

	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	/** 期号 */
	@TableField("draw_num")
	private String drawNum;

	/** 开奖时间 */
	@TableField("draw_time")
	private String drawTime;

	/** 奖池余额 */
	@TableField("pool_balance")
	private BigDecimal poolBalance;

	/** 销售额 */
	@TableField("sales_amount")
	private BigDecimal salesAmount;

	/** 环比增长率（%） */
	@TableField("growth_rate")
	private BigDecimal growthRate;

	/** 统计日期（用于按天/周/月统计） */
	@TableField("stat_date")
	private String statDate;

	@TableField(value = "create_time", fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	@TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;

	@TableLogic
	@TableField("is_deleted")
	private Integer isDeleted;
}

