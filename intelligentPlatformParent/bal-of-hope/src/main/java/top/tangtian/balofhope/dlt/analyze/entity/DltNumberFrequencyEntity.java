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
//		* ============================================================
//		* 1. 号码频率分析结果表实体
// * ============================================================
//		 */
@Data
@TableName("dlt_number_frequency")
public class DltNumberFrequencyEntity implements Serializable {

	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	/** 分析批次号（关联查询用） */
	@TableField("batch_no")
	private String batchNo;

	/** 号码类型：RED-红球, BLUE-蓝球 */
	@TableField("ball_type")
	private String ballType;

	/** 号码 */
	@TableField("number")
	private Integer number;

	/** 出现次数 */
	@TableField("frequency_count")
	private Integer frequencyCount;

	/** 出现百分比 */
	@TableField("frequency_percentage")
	private BigDecimal frequencyPercentage;

	/** 是否热号 */
	@TableField("is_hot")
	private Boolean isHot;

	/** 是否冷号 */
	@TableField("is_cold")
	private Boolean isCold;

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