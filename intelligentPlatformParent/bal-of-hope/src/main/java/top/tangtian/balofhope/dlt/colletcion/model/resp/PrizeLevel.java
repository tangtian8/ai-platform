package top.tangtian.balofhope.dlt.colletcion.model.resp;

/**
 * @author tangtian
 * @date 2026-01-21 13:09
 */
/**
 * 奖项等级信息
 */
public class PrizeLevel {

	/** 奖项等级名称，如"一等奖"、"二等奖" */
	private String prizeLevel;

	/** 中奖注数 */
	private String stakeCount;

	/** 单注奖金（带格式） */
	private String stakeAmount;

	/** 单注奖金（不带格式） */
	private String stakeAmountFormat;

	/** 总奖金 */
	private String totalPrizeamount;

	/** 排序 */
	private Integer sort;

	/** 奖项类型，0-普通奖项 */
	private Integer awardType;

	/** 中奖条件 */
	private Object lotteryCondition;

	/** 分组 */
	private String group;

	// Getters and Setters
	public String getPrizeLevel() {
		return prizeLevel;
	}

	public void setPrizeLevel(String prizeLevel) {
		this.prizeLevel = prizeLevel;
	}

	public String getStakeCount() {
		return stakeCount;
	}

	public void setStakeCount(String stakeCount) {
		this.stakeCount = stakeCount;
	}

	public String getStakeAmount() {
		return stakeAmount;
	}

	public void setStakeAmount(String stakeAmount) {
		this.stakeAmount = stakeAmount;
	}

	public String getStakeAmountFormat() {
		return stakeAmountFormat;
	}

	public void setStakeAmountFormat(String stakeAmountFormat) {
		this.stakeAmountFormat = stakeAmountFormat;
	}

	public String getTotalPrizeamount() {
		return totalPrizeamount;
	}

	public void setTotalPrizeamount(String totalPrizeamount) {
		this.totalPrizeamount = totalPrizeamount;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getAwardType() {
		return awardType;
	}

	public void setAwardType(Integer awardType) {
		this.awardType = awardType;
	}

	public Object getLotteryCondition() {
		return lotteryCondition;
	}

	public void setLotteryCondition(Object lotteryCondition) {
		this.lotteryCondition = lotteryCondition;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
}
