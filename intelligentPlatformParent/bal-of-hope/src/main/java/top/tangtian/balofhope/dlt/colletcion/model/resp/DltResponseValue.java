package top.tangtian.balofhope.dlt.colletcion.model.resp;

/**
 * @author tangtian
 * @date 2026-01-21 13:07
 */

import java.util.List;

/**
 * 响应数据主体
 */
public class DltResponseValue {

	/** 最新一期开奖信息 */
	private LastPoolDraw lastPoolDraw;

	/** 开奖列表 */
	private List<DltLotteryDraw> list;

	/** 当前页码 */
	private Integer pageNo;

	/** 每页大小 */
	private Integer pageSize;

	/** 总页数 */
	private Integer pages;

	/** 总记录数 */
	private Integer total;

	// Getters and Setters
	public LastPoolDraw getLastPoolDraw() {
		return lastPoolDraw;
	}

	public void setLastPoolDraw(LastPoolDraw lastPoolDraw) {
		this.lastPoolDraw = lastPoolDraw;
	}

	public List<DltLotteryDraw> getList() {
		return list;
	}

	public void setList(List<DltLotteryDraw> list) {
		this.list = list;
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

	public Integer getPages() {
		return pages;
	}

	public void setPages(Integer pages) {
		this.pages = pages;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}
}

