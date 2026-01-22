package top.tangtian.balofhope.dlt.storage.entity;

/**
 * @author tangtian
 * @date 2026-01-21 16:40
 */

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ============================================================
 * 2. 奖项等级实体类
 * ============================================================
 */
@Data
@TableName("dlt_prize_level")
public class DltPrizeLevelEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 主键ID */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	/** 期号（关联主表） */
	@TableField("lottery_draw_num")
	private String lotteryDrawNum;

	/** 奖项等级名称 */
	@TableField("prize_level")
	private String prizeLevel;

	/** 中奖注数 */
	@TableField("stake_count")
	private String stakeCount;

	/** 单注奖金 */
	@TableField("stake_amount")
	private String stakeAmount;

	/** 单注奖金（不带格式） */
	@TableField("stake_amount_format")
	private String stakeAmountFormat;

	/** 总奖金 */
	@TableField("total_prizeamount")
	private String totalPrizeamount;

	/** 排序 */
	@TableField("sort")
	private Integer sort;

	/** 奖项类型 */
	@TableField("award_type")
	private Integer awardType;

	/** 分组 */
	@TableField("group_code")
	private String groupCode;

	/** 创建时间 */
	@TableField(value = "create_time", fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	/** 更新时间 */
	@TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;

	/** 逻辑删除 */
	@TableLogic
	@TableField("is_deleted")
	private Integer isDeleted;
}