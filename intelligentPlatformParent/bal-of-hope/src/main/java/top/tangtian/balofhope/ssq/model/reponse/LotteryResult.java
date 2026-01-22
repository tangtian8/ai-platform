package top.tangtian.balofhope.ssq.model.reponse;

import java.util.List;

/**
 * @author tangtian
 * @date 2026-01-21 12:40
 */
public class LotteryResult {

	/** 彩票名称，如"双色球" */
	private String name;

	/** 期号，如"2025130" */
	private String code;

	/** 详情链接 */
	private String detailsLink;

	/** 视频链接 */
	private String videoLink;

	/** 开奖日期，格式：yyyy-MM-dd(星期) */
	private String date;

	/** 星期几 */
	private String week;

	/** 红球号码，逗号分隔，如"01,05,08,14,19,23" */
	private String red;

	/** 蓝球号码 */
	private String blue;

	/** 蓝球2号码（备用字段） */
	private String blue2;

	/** 本期销售额（单位：元） */
	private String sales;

	/** 奖池金额（单位：元） */
	private String poolmoney;

	/** 一等奖中奖地区说明 */
	private String content;

	/** 追加金额 */
	private String addmoney;

	/** 追加金额2（备用字段） */
	private String addmoney2;

	/** 消息说明 */
	private String msg;

	/** 追加标志 */
	private String z2add;

	/** 追加标志2 */
	private String m2add;

	/** 各奖项中奖详情列表 */
	private List<PrizeGrade> prizegrades;

	// Getters and Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDetailsLink() {
		return detailsLink;
	}

	public void setDetailsLink(String detailsLink) {
		this.detailsLink = detailsLink;
	}

	public String getVideoLink() {
		return videoLink;
	}

	public void setVideoLink(String videoLink) {
		this.videoLink = videoLink;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public String getRed() {
		return red;
	}

	public void setRed(String red) {
		this.red = red;
	}

	public String getBlue() {
		return blue;
	}

	public void setBlue(String blue) {
		this.blue = blue;
	}

	public String getBlue2() {
		return blue2;
	}

	public void setBlue2(String blue2) {
		this.blue2 = blue2;
	}

	public String getSales() {
		return sales;
	}

	public void setSales(String sales) {
		this.sales = sales;
	}

	public String getPoolmoney() {
		return poolmoney;
	}

	public void setPoolmoney(String poolmoney) {
		this.poolmoney = poolmoney;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAddmoney() {
		return addmoney;
	}

	public void setAddmoney(String addmoney) {
		this.addmoney = addmoney;
	}

	public String getAddmoney2() {
		return addmoney2;
	}

	public void setAddmoney2(String addmoney2) {
		this.addmoney2 = addmoney2;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getZ2add() {
		return z2add;
	}

	public void setZ2add(String z2add) {
		this.z2add = z2add;
	}

	public String getM2add() {
		return m2add;
	}

	public void setM2add(String m2add) {
		this.m2add = m2add;
	}

	public List<PrizeGrade> getPrizegrades() {
		return prizegrades;
	}

	public void setPrizegrades(List<PrizeGrade> prizegrades) {
		this.prizegrades = prizegrades;
	}
}
