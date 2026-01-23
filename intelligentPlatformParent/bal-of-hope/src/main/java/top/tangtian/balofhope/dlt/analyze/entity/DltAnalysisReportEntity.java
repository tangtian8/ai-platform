package top.tangtian.balofhope.dlt.analyze.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author tangtian
 * @date 2026-01-23 09:12
 */
@Data
@TableName("dlt_analysis_report")
public class DltAnalysisReportEntity implements Serializable {

	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	/** 报告批次号 */
	@TableField("report_batch")
	private String reportBatch;

	/** 报告类型：DAILY-日报, WEEKLY-周报, MONTHLY-月报 */
	@TableField("report_type")
	private String reportType;

	/** 分析期数 */
	@TableField("analyzed_periods")
	private Integer analyzedPeriods;

	/** 当前奖池 */
	@TableField("current_pool")
	private BigDecimal currentPool;

	/** 平均销售额 */
	@TableField("avg_sales")
	private BigDecimal avgSales;

	/** 报告摘要（JSON格式） */
	@TableField("report_summary")
	private String reportSummary;

	/** 生成时间 */
	@TableField("generate_time")
	private LocalDateTime generateTime;

	@TableField(value = "create_time", fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	@TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;

	@TableLogic
	@TableField("is_deleted")
	private Integer isDeleted;
}
