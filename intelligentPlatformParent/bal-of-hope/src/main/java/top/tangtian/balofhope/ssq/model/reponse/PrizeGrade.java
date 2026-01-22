package top.tangtian.balofhope.ssq.model.reponse;

/**
 * @author tangtian
 * @date 2026-01-21 12:41
 */
public class PrizeGrade {
	/**
	 * 奖项等级
	 * 1-一等奖, 2-二等奖, 3-三等奖, 4-四等奖, 5-五等奖, 6-六等奖
	 */
	private Integer type;

	/** 中奖注数 */
	private String typenum;

	/** 单注奖金（单位：元） */
	private String typemoney;

	// Getters and Setters
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getTypenum() {
		return typenum;
	}

	public void setTypenum(String typenum) {
		this.typenum = typenum;
	}

	public String getTypemoney() {
		return typemoney;
	}

	public void setTypemoney(String typemoney) {
		this.typemoney = typemoney;
	}
}
