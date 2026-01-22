package top.tangtian.balofhope.dlt.model.resp;

/**
 * @author tangtian
 * @date 2026-01-21 13:08
 */

import java.util.List;

/**
 * 最新一期奖池信息
 */
public class LastPoolDraw {

	/** 期号 */
	private String lotteryDrawNum;

	/** 开奖结果，格式："03 06 17 21 33 05 11" */
	private String lotteryDrawResult;

	/** 开奖时间 */
	private String lotteryDrawTime;

	/** 彩票游戏名称 */
	private String lotteryGameName;

	/** 彩票游戏编号 */
	private String lotteryGameNum;

	/** 开奖后奖池余额 */
	private String poolBalanceAfterdraw;

	/** 奖项等级列表 */
	private List<PrizeLevel> prizeLevelList;

	// Getters and Setters
	public String getLotteryDrawNum() {
		return lotteryDrawNum;
	}

	public void setLotteryDrawNum(String lotteryDrawNum) {
		this.lotteryDrawNum = lotteryDrawNum;
	}

	public String getLotteryDrawResult() {
		return lotteryDrawResult;
	}

	public void setLotteryDrawResult(String lotteryDrawResult) {
		this.lotteryDrawResult = lotteryDrawResult;
	}

	public String getLotteryDrawTime() {
		return lotteryDrawTime;
	}

	public void setLotteryDrawTime(String lotteryDrawTime) {
		this.lotteryDrawTime = lotteryDrawTime;
	}

	public String getLotteryGameName() {
		return lotteryGameName;
	}

	public void setLotteryGameName(String lotteryGameName) {
		this.lotteryGameName = lotteryGameName;
	}

	public String getLotteryGameNum() {
		return lotteryGameNum;
	}

	public void setLotteryGameNum(String lotteryGameNum) {
		this.lotteryGameNum = lotteryGameNum;
	}

	public String getPoolBalanceAfterdraw() {
		return poolBalanceAfterdraw;
	}

	public void setPoolBalanceAfterdraw(String poolBalanceAfterdraw) {
		this.poolBalanceAfterdraw = poolBalanceAfterdraw;
	}

	public List<PrizeLevel> getPrizeLevelList() {
		return prizeLevelList;
	}

	public void setPrizeLevelList(List<PrizeLevel> prizeLevelList) {
		this.prizeLevelList = prizeLevelList;
	}
}

