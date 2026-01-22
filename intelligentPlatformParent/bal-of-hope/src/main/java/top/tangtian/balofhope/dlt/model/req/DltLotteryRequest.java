package top.tangtian.balofhope.dlt.model.req;

/**
 * @author tangtian
 * @date 2026-01-21 13:12
 */
/**
 * 大乐透查询请求参数实体
 */
public class DltLotteryRequest {

	/** 游戏编号，85代表超级大乐透 */
	private String gameNo;

	/** 省份ID，0表示全国 */
	private Integer provinceId;

	/** 每页大小 */
	private Integer pageSize;

	/** 是否验证，1-是，0-否 */
	private Integer isVerify;

	/** 当前页码 */
	private Integer pageNo;

	/** 开始期号 */
	private String startTerm;

	/** 结束期号 */
	private String endTerm;

	// 构造函数
	public DltLotteryRequest() {
	}

	public DltLotteryRequest(String gameNo, Integer provinceId, Integer pageSize,
							 Integer isVerify, Integer pageNo, String startTerm, String endTerm) {
		this.gameNo = gameNo;
		this.provinceId = provinceId;
		this.pageSize = pageSize;
		this.isVerify = isVerify;
		this.pageNo = pageNo;
		this.startTerm = startTerm;
		this.endTerm = endTerm;
	}

	// Getters and Setters
	public String getGameNo() {
		return gameNo;
	}

	public void setGameNo(String gameNo) {
		this.gameNo = gameNo;
	}

	public Integer getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getIsVerify() {
		return isVerify;
	}

	public void setIsVerify(Integer isVerify) {
		this.isVerify = isVerify;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public String getStartTerm() {
		return startTerm;
	}

	public void setStartTerm(String startTerm) {
		this.startTerm = startTerm;
	}

	public String getEndTerm() {
		return endTerm;
	}

	public void setEndTerm(String endTerm) {
		this.endTerm = endTerm;
	}

	@Override
	public String toString() {
		return "DltLotteryRequest{" +
				"gameNo='" + gameNo + '\'' +
				", provinceId=" + provinceId +
				", pageSize=" + pageSize +
				", isVerify=" + isVerify +
				", pageNo=" + pageNo +
				", startTerm='" + startTerm + '\'' +
				", endTerm='" + endTerm + '\'' +
				'}';
	}

	/**
	 * 构建器模式（可选，方便创建对象）
	 */
	public static class Builder {
		private String gameNo = "85"; // 默认大乐透
		private Integer provinceId = 0;
		private Integer pageSize = 30;
		private Integer isVerify = 1;
		private Integer pageNo = 1;
		private String startTerm;
		private String endTerm;

		public Builder gameNo(String gameNo) {
			this.gameNo = gameNo;
			return this;
		}

		public Builder provinceId(Integer provinceId) {
			this.provinceId = provinceId;
			return this;
		}

		public Builder pageSize(Integer pageSize) {
			this.pageSize = pageSize;
			return this;
		}

		public Builder isVerify(Integer isVerify) {
			this.isVerify = isVerify;
			return this;
		}

		public Builder pageNo(Integer pageNo) {
			this.pageNo = pageNo;
			return this;
		}

		public Builder startTerm(String startTerm) {
			this.startTerm = startTerm;
			return this;
		}

		public Builder endTerm(String endTerm) {
			this.endTerm = endTerm;
			return this;
		}

		public DltLotteryRequest build() {
			return new DltLotteryRequest(gameNo, provinceId, pageSize,
					isVerify, pageNo, startTerm, endTerm);
		}
	}
}
