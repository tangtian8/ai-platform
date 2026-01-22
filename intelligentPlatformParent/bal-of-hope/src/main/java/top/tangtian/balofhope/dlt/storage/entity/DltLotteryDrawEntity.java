package top.tangtian.balofhope.dlt.storage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author tangtian
 * @date 2026-01-21 16:40
 */
@Data
@TableName("dlt_lottery_draw")
public class DltLotteryDrawEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 主键ID（自增） */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	/** 期号（唯一索引） */
	@TableField("lottery_draw_num")
	private String lotteryDrawNum;

	/** 彩票游戏名称 */
	@TableField("lottery_game_name")
	private String lotteryGameName;

	/** 彩票游戏编号 */
	@TableField("lottery_game_num")
	private String lotteryGameNum;

	/** 开奖结果（排序后） */
	@TableField("lottery_draw_result")
	private String lotteryDrawResult;

	/** 开奖结果（未排序） */
	@TableField("lottery_unsort_drawresult")
	private String lotteryUnsortDrawresult;

	/** 开奖时间 */
	@TableField("lottery_draw_time")
	private String lotteryDrawTime;

	/** 销售开始时间 */
	@TableField("lottery_sale_begin_time")
	private String lotterySaleBeginTime;

	/** 销售结束时间 */
	@TableField("lottery_sale_endtime")
	private String lotterySaleEndtime;

	/** 兑奖开始时间 */
	@TableField("lottery_paid_begin_time")
	private String lotteryPaidBeginTime;

	/** 兑奖结束时间 */
	@TableField("lottery_paid_end_time")
	private String lotteryPaidEndTime;

	/** 开奖后奖池余额 */
	@TableField("pool_balance_afterdraw")
	private String poolBalanceAfterdraw;

	/** 开奖流动基金 */
	@TableField("draw_flow_fund")
	private String drawFlowFund;

	/** 总销售额 */
	@TableField("total_sale_amount")
	private String totalSaleAmount;

	/** 开奖状态 */
	@TableField("lottery_draw_status")
	private Integer lotteryDrawStatus;

	/** 暂停销售标志 */
	@TableField("lottery_suspended_flag")
	private Integer lotterySuspendedFlag;

	/** 开奖PDF链接 */
	@TableField("draw_pdf_url")
	private String drawPdfUrl;

	/** 创建时间 */
	@TableField(value = "create_time", fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	/** 更新时间 */
	@TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;

	/** 逻辑删除标志 0-未删除 1-已删除 */
	@TableLogic
	@TableField("is_deleted")
	private Integer isDeleted;
}
