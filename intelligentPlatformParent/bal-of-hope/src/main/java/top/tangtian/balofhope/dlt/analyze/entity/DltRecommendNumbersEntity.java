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
 * 5. 推荐号码记录表实体
 * ============================================================
 */
@Data
@TableName("dlt_recommend_numbers")
public class DltRecommendNumbersEntity implements Serializable {

	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	/** 推荐批次号 */
	@TableField("recommend_batch")
	private String recommendBatch;

	/** 推荐策略：HOT-热号, OMISSION-遗漏, BALANCED-均衡 */
	@TableField("recommend_strategy")
	private String recommendStrategy;

	/** 推荐红球（逗号分隔） */
	@TableField("red_numbers")
	private String redNumbers;

	/** 推荐蓝球（逗号分隔） */
	@TableField("blue_numbers")
	private String blueNumbers;

	/** 推荐理由 */
	@TableField("recommend_reason")
	private String recommendReason;

	/** 推荐时间 */
	@TableField("recommend_time")
	private LocalDateTime recommendTime;

	/** 是否已开奖 */
	@TableField("is_drawed")
	private Boolean isDrawed;

	/** 实际开奖期号 */
	@TableField("actual_draw_num")
	private String actualDrawNum;

	/** 命中红球数 */
	@TableField("hit_red_count")
	private Integer hitRedCount;

	/** 命中蓝球数 */
	@TableField("hit_blue_count")
	private Integer hitBlueCount;

	@TableField(value = "create_time", fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	@TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;

	@TableLogic
	@TableField("is_deleted")
	private Integer isDeleted;
}
