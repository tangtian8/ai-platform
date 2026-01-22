package top.tangtian.balofhope.ssq.model.request;

/**
 * @author tangtian
 * @date 2026-01-21 12:47
 * https://www.cwl.gov.cn/cwl_admin/front/cwlkj/search/kjxx/findDrawNotice?name=ssq&issueCount=&issueStart=&issueEnd=&dayStart=&dayEnd=&pageNo=1&pageSize=30&week=&systemType=PC
 */
public class LotteryRequest {
	/** 彩票名称，如"ssq"代表双色球 */
	private String name;

	/** 当前页码，从1开始 */
	private Integer pageNo;

	/** 每页大小 */
	private Integer pageSize;

	/** 系统类型，如"PC"、"Mobile"等 */
	private String systemType;

	// 构造函数
	public LotteryRequest() {
	}

	public LotteryRequest(String name, Integer pageNo, Integer pageSize, String systemType) {
		this.name = name;
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.systemType = systemType;
	}

	// Getters and Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getSystemType() {
		return systemType;
	}

	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}

	@Override
	public String toString() {
		return "LotteryRequest{" +
				"name='" + name + '\'' +
				", pageNo=" + pageNo +
				", pageSize=" + pageSize +
				", systemType='" + systemType + '\'' +
				'}';
	}
}
