package top.tangtian.balofhope.ssq.model.reponse;

import java.util.List;

/**
 * @author tangtian
 * @date 2026-01-21 12:38
 */
public class LotteryResponse {
	/** 状态码，0表示成功 */
	private Integer state;

	/** 返回消息 */
	private String message;

	/** 总记录数 */
	private Integer total;

	/** 总页数 */
	private Integer pageNum;

	/** 当前页码 */
	private Integer pageNo;

	/** 每页大小 */
	private Integer pageSize;

	/** 标志位 */
	private Integer Tflag;

	/** 双色球开奖结果列表 */
	private List<LotteryResult> result;

	// Getters and Setters
	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
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

	public Integer getTflag() {
		return Tflag;
	}

	public void setTflag(Integer tflag) {
		Tflag = tflag;
	}

	public List<LotteryResult> getResult() {
		return result;
	}

	public void setResult(List<LotteryResult> result) {
		this.result = result;
	}
}
