package top.tangtian.balofhope.dlt.analyze.entity;

/**
 * @author tangtian
 * @date 2026-01-23 09:09
 */

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ============================================================
 * 4. 号码遗漏分析结果表实体
 * ============================================================
 */
@Data
@TableName("dlt_number_omission")
public class DltNumberOmissionEntity implements Serializable {

	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	/** 分析批次号 */
	@TableField("batch_no")
	private String batchNo;

	/** 号码类型 */
	@TableField("ball_type")
	private String ballType;

	/** 号码 */
	@TableField("number")
	private Integer number;

	/** 当前遗漏值 */
	@TableField("current_omission")
	private Integer currentOmission;

	/** 平均遗漏值 */
	@TableField("avg_omission")
	private Integer avgOmission;

	/** 最大遗漏值 */
	@TableField("max_omission")
	private Integer maxOmission;

	/** 遗漏等级：HIGH-高, MEDIUM-中, LOW-低 */
	@TableField("omission_level")
	private String omissionLevel;

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
