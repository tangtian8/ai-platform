package top.tangtian.balofhope.dlt.colletcion.model.resp;

/**
 * @author tangtian
 * @date 2026-01-21 13:08
 */

import java.util.List;

/**
 * 大乐透单期开奖详细信息
 */
public class DltLotteryDraw {

	/** 彩票游戏名称 */
	private String lotteryGameName;

	/** 彩票游戏编号 */
	private String lotteryGameNum;

	/** 期号 */
	private String lotteryDrawNum;

	/** 开奖结果（排序后），格式："03 06 17 21 33 05 11" */
	private String lotteryDrawResult;

	/** 开奖结果（未排序） */
	private String lotteryUnsortDrawresult;

	/** 暂停销售标志，0-正常 */
	private Integer lotterySuspendedFlag;

	/** 开奖状态，20-已开奖 */
	private Integer lotteryDrawStatus;

	/** 开奖状态编号 */
	private String lotteryDrawStatusNo;

	/** 销售结束时间 */
	private String lotterySaleEndtime;

	/** 销售开始时间 */
	private String lotterySaleBeginTime;

	/** 开奖时间 */
	private String lotteryDrawTime;

	/** 兑奖开始时间 */
	private String lotteryPaidBeginTime;

	/** 兑奖结束时间 */
	private String lotteryPaidEndTime;

	/** 预计开奖时间 */
	private String estimateDrawTime;

	/** 验证标志 */
	private Integer verify;

	/** 促销标志 */
	private Integer lotteryPromotionFlag;

	/** 促销标志（rj） */
	private Integer lotteryPromotionFlagRj;

	/** 是否获取开奖PDF */
	private Integer isGetKjpdf;

	/** 是否获取详情PDF */
	private Integer isGetXlpdf;

	/** PDF类型 */
	private Integer pdfType;

	/** 开奖PDF链接 */
	private String drawPdfUrl;

	/** 开奖后奖池余额 */
	private String poolBalanceAfterdraw;

	/** 开奖后奖池余额（rj） */
	private String poolBalanceAfterdrawRj;

	/** 开奖流动基金 */
	private String drawFlowFund;

	/** 开奖流动基金（rj） */
	private String drawFlowFundRj;

	/** 总销售额 */
	private String totalSaleAmount;

	/** 总销售额（rj） */
	private String totalSaleAmountRj;

	/** 彩票设备数量 */
	private Integer lotteryEquipmentCount;

	/** 彩票游戏项目编号 */
	private Integer lotteryGamePronum;

	/** 奖项等级列表 */
	private List<PrizeLevel> prizeLevelList;

	/** 奖项等级列表（rj） */
	private List<PrizeLevel> prizeLevelListRj;

	/** 比赛列表 */
	private List<Object> matchList;

	/** 期次列表 */
	private List<Object> termList;

	/** 期次结果列表 */
	private List<Object> termResultList;

	/** 规则类型 */
	private Integer ruleType;

	/** 工具配置 */
	private Object vtoolsConfig;

	/** 剩余金额 */
	private String surplusAmount;

	/** 剩余金额（rj） */
	private String surplusAmountRj;

	/** 彩票公告 */
	private Integer lotteryNotice;

	/** 彩票公告显示标志 */
	private Integer lotteryNoticeShowFlag;

	/** 销售结束时间Unix时间戳 */
	private Object lotterySaleEndTimeUnix;

	// Getters and Setters
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

	public String getLotteryUnsortDrawresult() {
		return lotteryUnsortDrawresult;
	}

	public void setLotteryUnsortDrawresult(String lotteryUnsortDrawresult) {
		this.lotteryUnsortDrawresult = lotteryUnsortDrawresult;
	}

	public Integer getLotterySuspendedFlag() {
		return lotterySuspendedFlag;
	}

	public void setLotterySuspendedFlag(Integer lotterySuspendedFlag) {
		this.lotterySuspendedFlag = lotterySuspendedFlag;
	}

	public Integer getLotteryDrawStatus() {
		return lotteryDrawStatus;
	}

	public void setLotteryDrawStatus(Integer lotteryDrawStatus) {
		this.lotteryDrawStatus = lotteryDrawStatus;
	}

	public String getLotteryDrawStatusNo() {
		return lotteryDrawStatusNo;
	}

	public void setLotteryDrawStatusNo(String lotteryDrawStatusNo) {
		this.lotteryDrawStatusNo = lotteryDrawStatusNo;
	}

	public String getLotterySaleEndtime() {
		return lotterySaleEndtime;
	}

	public void setLotterySaleEndtime(String lotterySaleEndtime) {
		this.lotterySaleEndtime = lotterySaleEndtime;
	}

	public String getLotterySaleBeginTime() {
		return lotterySaleBeginTime;
	}

	public void setLotterySaleBeginTime(String lotterySaleBeginTime) {
		this.lotterySaleBeginTime = lotterySaleBeginTime;
	}

	public String getLotteryDrawTime() {
		return lotteryDrawTime;
	}

	public void setLotteryDrawTime(String lotteryDrawTime) {
		this.lotteryDrawTime = lotteryDrawTime;
	}

	public String getLotteryPaidBeginTime() {
		return lotteryPaidBeginTime;
	}

	public void setLotteryPaidBeginTime(String lotteryPaidBeginTime) {
		this.lotteryPaidBeginTime = lotteryPaidBeginTime;
	}

	public String getLotteryPaidEndTime() {
		return lotteryPaidEndTime;
	}

	public void setLotteryPaidEndTime(String lotteryPaidEndTime) {
		this.lotteryPaidEndTime = lotteryPaidEndTime;
	}

	public String getEstimateDrawTime() {
		return estimateDrawTime;
	}

	public void setEstimateDrawTime(String estimateDrawTime) {
		this.estimateDrawTime = estimateDrawTime;
	}

	public Integer getVerify() {
		return verify;
	}

	public void setVerify(Integer verify) {
		this.verify = verify;
	}

	public Integer getLotteryPromotionFlag() {
		return lotteryPromotionFlag;
	}

	public void setLotteryPromotionFlag(Integer lotteryPromotionFlag) {
		this.lotteryPromotionFlag = lotteryPromotionFlag;
	}

	public Integer getLotteryPromotionFlagRj() {
		return lotteryPromotionFlagRj;
	}

	public void setLotteryPromotionFlagRj(Integer lotteryPromotionFlagRj) {
		this.lotteryPromotionFlagRj = lotteryPromotionFlagRj;
	}

	public Integer getIsGetKjpdf() {
		return isGetKjpdf;
	}

	public void setIsGetKjpdf(Integer isGetKjpdf) {
		this.isGetKjpdf = isGetKjpdf;
	}

	public Integer getIsGetXlpdf() {
		return isGetXlpdf;
	}

	public void setIsGetXlpdf(Integer isGetXlpdf) {
		this.isGetXlpdf = isGetXlpdf;
	}

	public Integer getPdfType() {
		return pdfType;
	}

	public void setPdfType(Integer pdfType) {
		this.pdfType = pdfType;
	}

	public String getDrawPdfUrl() {
		return drawPdfUrl;
	}

	public void setDrawPdfUrl(String drawPdfUrl) {
		this.drawPdfUrl = drawPdfUrl;
	}

	public String getPoolBalanceAfterdraw() {
		return poolBalanceAfterdraw;
	}

	public void setPoolBalanceAfterdraw(String poolBalanceAfterdraw) {
		this.poolBalanceAfterdraw = poolBalanceAfterdraw;
	}

	public String getPoolBalanceAfterdrawRj() {
		return poolBalanceAfterdrawRj;
	}

	public void setPoolBalanceAfterdrawRj(String poolBalanceAfterdrawRj) {
		this.poolBalanceAfterdrawRj = poolBalanceAfterdrawRj;
	}

	public String getDrawFlowFund() {
		return drawFlowFund;
	}

	public void setDrawFlowFund(String drawFlowFund) {
		this.drawFlowFund = drawFlowFund;
	}

	public String getDrawFlowFundRj() {
		return drawFlowFundRj;
	}

	public void setDrawFlowFundRj(String drawFlowFundRj) {
		this.drawFlowFundRj = drawFlowFundRj;
	}

	public String getTotalSaleAmount() {
		return totalSaleAmount;
	}

	public void setTotalSaleAmount(String totalSaleAmount) {
		this.totalSaleAmount = totalSaleAmount;
	}

	public String getTotalSaleAmountRj() {
		return totalSaleAmountRj;
	}

	public void setTotalSaleAmountRj(String totalSaleAmountRj) {
		this.totalSaleAmountRj = totalSaleAmountRj;
	}

	public Integer getLotteryEquipmentCount() {
		return lotteryEquipmentCount;
	}

	public void setLotteryEquipmentCount(Integer lotteryEquipmentCount) {
		this.lotteryEquipmentCount = lotteryEquipmentCount;
	}

	public Integer getLotteryGamePronum() {
		return lotteryGamePronum;
	}

	public void setLotteryGamePronum(Integer lotteryGamePronum) {
		this.lotteryGamePronum = lotteryGamePronum;
	}

	public List<PrizeLevel> getPrizeLevelList() {
		return prizeLevelList;
	}

	public void setPrizeLevelList(List<PrizeLevel> prizeLevelList) {
		this.prizeLevelList = prizeLevelList;
	}

	public List<PrizeLevel> getPrizeLevelListRj() {
		return prizeLevelListRj;
	}

	public void setPrizeLevelListRj(List<PrizeLevel> prizeLevelListRj) {
		this.prizeLevelListRj = prizeLevelListRj;
	}

	public List<Object> getMatchList() {
		return matchList;
	}

	public void setMatchList(List<Object> matchList) {
		this.matchList = matchList;
	}

	public List<Object> getTermList() {
		return termList;
	}

	public void setTermList(List<Object> termList) {
		this.termList = termList;
	}

	public List<Object> getTermResultList() {
		return termResultList;
	}

	public void setTermResultList(List<Object> termResultList) {
		this.termResultList = termResultList;
	}

	public Integer getRuleType() {
		return ruleType;
	}

	public void setRuleType(Integer ruleType) {
		this.ruleType = ruleType;
	}

	public Object getVtoolsConfig() {
		return vtoolsConfig;
	}

	public void setVtoolsConfig(Object vtoolsConfig) {
		this.vtoolsConfig = vtoolsConfig;
	}

	public String getSurplusAmount() {
		return surplusAmount;
	}

	public void setSurplusAmount(String surplusAmount) {
		this.surplusAmount = surplusAmount;
	}

	public String getSurplusAmountRj() {
		return surplusAmountRj;
	}

	public void setSurplusAmountRj(String surplusAmountRj) {
		this.surplusAmountRj = surplusAmountRj;
	}

	public Integer getLotteryNotice() {
		return lotteryNotice;
	}

	public void setLotteryNotice(Integer lotteryNotice) {
		this.lotteryNotice = lotteryNotice;
	}

	public Integer getLotteryNoticeShowFlag() {
		return lotteryNoticeShowFlag;
	}

	public void setLotteryNoticeShowFlag(Integer lotteryNoticeShowFlag) {
		this.lotteryNoticeShowFlag = lotteryNoticeShowFlag;
	}

	public Object getLotterySaleEndTimeUnix() {
		return lotterySaleEndTimeUnix;
	}

	public void setLotterySaleEndTimeUnix(Object lotterySaleEndTimeUnix) {
		this.lotterySaleEndTimeUnix = lotterySaleEndTimeUnix;
	}
}

