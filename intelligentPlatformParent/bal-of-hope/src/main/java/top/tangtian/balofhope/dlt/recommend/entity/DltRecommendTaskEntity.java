package top.tangtian.balofhope.dlt.recommend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author tangtian
 * @date 2026-01-26 18:15
 */
@Data
@TableName("dlt_recommend_task")
public class DltRecommendTaskEntity implements Serializable {

	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	/** 用户ID */
	@TableField("user_id")
	private Long userId;

	/** 用户名称（冗余字段，方便查询） */
	@TableField("user_name")
	private String userName;

	/** 推荐批次号 */
	@TableField("recommend_batch")
	private String recommendBatch;

	/** 推荐策略：DAILY-日推荐, WEEKLY-周推荐, MONTHLY-月推荐, ALL-全量推荐 */
	@TableField("recommend_strategy")
	private String recommendStrategy;

	/** 分析数据量（期数） */
	@TableField("analyzed_periods")
	private Integer analyzedPeriods;

	/** 推荐红球1（5个号码，逗号分隔） */
	@TableField("red_balls_1")
	private String redBalls1;

	/** 推荐红球2（5个号码，逗号分隔） */
	@TableField("red_balls_2")
	private String redBalls2;

	/** 推荐红球3（5个号码，逗号分隔） */
	@TableField("red_balls_3")
	private String redBalls3;

	/** 推荐红球4（5个号码，逗号分隔） */
	@TableField("red_balls_4")
	private String redBalls4;

	/** 推荐蓝球1（2个号码，逗号分隔） */
	@TableField("blue_balls_1")
	private String blueBalls1;

	/** 推荐蓝球2（2个号码，逗号分隔） */
	@TableField("blue_balls_2")
	private String blueBalls2;

	/** 推荐蓝球3（2个号码，逗号分隔） */
	@TableField("blue_balls_3")
	private String blueBalls3;

	/** 推荐蓝球4（2个号码，逗号分隔） */
	@TableField("blue_balls_4")
	private String blueBalls4;

	/** 推荐时间 */
	@TableField("recommend_time")
	private LocalDateTime recommendTime;

	/** 目标期号（预测下一期的期号） */
	@TableField("target_draw_num")
	private String targetDrawNum;

	/** 是否已开奖 */
	@TableField("is_drawed")
	private Boolean isDrawed;

	/** 实际开奖期号 */
	@TableField("actual_draw_num")
	private String actualDrawNum;

	/** 实际开奖号码 */
	@TableField("actual_draw_result")
	private String actualDrawResult;

	/** 推荐1命中红球数 */
	@TableField("hit_red_count_1")
	private Integer hitRedCount1;

	/** 推荐1命中蓝球数 */
	@TableField("hit_blue_count_1")
	private Integer hitBlueCount1;

	/** 推荐1中奖等级 */
	@TableField("prize_level_1")
	private String prizeLevel1;

	/** 推荐2命中红球数 */
	@TableField("hit_red_count_2")
	private Integer hitRedCount2;

	/** 推荐2命中蓝球数 */
	@TableField("hit_blue_count_2")
	private Integer hitBlueCount2;

	/** 推荐2中奖等级 */
	@TableField("prize_level_2")
	private String prizeLevel2;

	/** 推荐3命中红球数 */
	@TableField("hit_red_count_3")
	private Integer hitRedCount3;

	/** 推荐3命中蓝球数 */
	@TableField("hit_blue_count_3")
	private Integer hitBlueCount3;

	/** 推荐3中奖等级 */
	@TableField("prize_level_3")
	private String prizeLevel3;

	/** 推荐4命中红球数 */
	@TableField("hit_red_count_4")
	private Integer hitRedCount4;

	/** 推荐4命中蓝球数 */
	@TableField("hit_blue_count_4")
	private Integer hitBlueCount4;

	/** 推荐4中奖等级 */
	@TableField("prize_level_4")
	private String prizeLevel4;

	/** 最佳推荐编号（1-4） */
	@TableField("best_recommend_no")
	private Integer bestRecommendNo;

	/** 验证时间 */
	@TableField("verify_time")
	private LocalDateTime verifyTime;

	/** 备注 */
	@TableField("remark")
	private String remark;

	@TableField(value = "create_time", fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	@TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;

	@TableLogic
	@TableField("is_deleted")
	private Integer isDeleted;
}

