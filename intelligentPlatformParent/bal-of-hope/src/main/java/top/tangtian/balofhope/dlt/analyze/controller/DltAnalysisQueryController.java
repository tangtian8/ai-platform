package top.tangtian.balofhope.dlt.analyze.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.tangtian.balofhope.dlt.analyze.service.analysisquery.DltAnalysisQueryService;
import top.tangtian.balofhope.dlt.analyze.service.vo.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tangtian
 * @date 2026-01-23 09:47
 */
@RestController
@RequestMapping("/api/dlt/analysis/query")
class DltAnalysisQueryController {

	@Autowired
	private DltAnalysisQueryService queryService;

	/**
	 * 获取红球频率分析（从数据库）
	 */
	@GetMapping("/red-ball-frequency")
	public NumberFrequencyStats getRedBallFrequency(
			@RequestParam(required = false) String batchNo) {
		return queryService.getRedBallFrequencyFromDB(batchNo);
	}

	/**
	 * 获取蓝球频率分析（从数据库）
	 */
	@GetMapping("/blue-ball-frequency")
	public NumberFrequencyStats getBlueBallFrequency(
			@RequestParam(required = false) String batchNo) {
		return queryService.getBlueBallFrequencyFromDB(batchNo);
	}

	/**
	 * 获取奖池趋势（从数据库）
	 */
	@GetMapping("/pool-trend")
	public PoolTrendAnalysis getPoolTrend(
			@RequestParam(defaultValue = "50") Integer limit) {
		return queryService.getPoolTrendFromDB(limit);
	}

	/**
	 * 获取销售额趋势（从数据库）
	 */
	@GetMapping("/sales-trend")
	public SalesTrendAnalysis getSalesTrend(
			@RequestParam(defaultValue = "50") Integer limit) {
		return queryService.getSalesTrendFromDB(limit);
	}

	/**
	 * 获取奖项统计（从数据库）
	 */
	@GetMapping("/prize-statistics")
	public PrizeStatisticsAnalysis getPrizeStatistics(
			@RequestParam(required = false) String batchNo) {
		return queryService.getPrizeStatisticsFromDB(batchNo);
	}

	/**
	 * 获取号码遗漏（从数据库）
	 */
	@GetMapping("/number-omission")
	public NumberOmissionAnalysis getNumberOmission(
			@RequestParam(required = false) String batchNo,
			@RequestParam(defaultValue = "RED") String ballType) {
		return queryService.getNumberOmissionFromDB(batchNo, ballType);
	}

	/**
	 * 获取推荐号码（从数据库）
	 */
	@GetMapping("/recommend")
	public RecommendNumbers getRecommendNumbers(
			@RequestParam(required = false) String batchNo) {
		return queryService.getRecommendNumbersFromDB(batchNo);
	}

	/**
	 * 获取综合分析报告（从数据库）
	 */
	@GetMapping("/comprehensive-report")
	public ComprehensiveAnalysisReport getComprehensiveReport(
			@RequestParam(required = false) String batchNo) {
		return queryService.getComprehensiveReportFromDB(batchNo);
	}

	/**
	 * 获取最新批次号
	 */
	@GetMapping("/latest-batch")
	public Map<String, String> getLatestBatch() {
		String batchNo = queryService.getLatestBatchNo();
		Map<String, String> result = new HashMap<>();
		result.put("batchNo", batchNo);
		return result;
	}

	/**
	 * 获取历史报告列表
	 */
	@GetMapping("/report-history")
	public List<AnalysisReportSummary> getReportHistory(
			@RequestParam(defaultValue = "10") Integer limit) {
		return queryService.getReportHistory(limit);
	}

	/**
	 * 获取热号排行
	 */
	@GetMapping("/hot-numbers-ranking")
	public List<NumberFrequency> getHotNumbersRanking(
			@RequestParam(defaultValue = "RED") String ballType,
			@RequestParam(defaultValue = "10") Integer limit) {
		return queryService.getHotNumbersRanking(ballType, limit);
	}

	/**
	 * 获取高遗漏号码
	 */
	@GetMapping("/high-omission-numbers")
	public List<NumberOmission> getHighOmissionNumbers(
			@RequestParam(defaultValue = "RED") String ballType,
			@RequestParam(defaultValue = "10") Integer limit) {
		return queryService.getHighOmissionNumbers(ballType, limit);
	}
}

