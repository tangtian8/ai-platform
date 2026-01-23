package top.tangtian.balofhope.dlt.analyze.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * @author tangtian
 * @date 2026-01-23 09:09
 * ============================================================
 * 3. 奖项统计分析结果表实体
 * ============================================================
 */
@Data
@TableName("dlt_prize_statistics")
public class DltPrizeStatisticsEntity implements Serializable {

	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	/** 分析批次号 */
	@TableField("batch_no")
	private String batchNo;

	/** 奖项等级 */
	@TableField("prize_level")
	private String prizeLevel;

	/** 中奖总次数 */
	@TableField("total_count")
	private Integer totalCount;

	/** 中奖总金额 */
	@TableField("total_amount")
	private BigDecimal totalAmount;

	/** 平均单注奖金 */
	@TableField("avg_amount")
	private BigDecimal avgAmount;

	/** 最高单注奖金 */
	@TableField("max_amount")
	private BigDecimal maxAmount;

	/** 最低单注奖金 */
	@TableField("min_amount")
	private BigDecimal minAmount;

	/** 分析期数 */
	@TableField("analyzed_periods")
	private Integer analyzedPeriods;

	/** 分析时间 */
	@TableField("analysis_time")
	private LocalDateTime analysisTime;

	@TableField(value = "create_time", fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	@TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;

	@TableLogic
	@TableField("is_deleted")
	private Integer isDeleted;
}
