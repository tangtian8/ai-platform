package top.tangtian.balofhope.dlt.schedule;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import top.tangtian.balofhope.dlt.analyze.entity.DltAnalysisReportEntity;
import top.tangtian.balofhope.dlt.analyze.service.analysisstorage.DltAnalysisStorageService;
import top.tangtian.balofhope.dlt.analyze.service.analysisstorage.DltDataAnalysisService;
import top.tangtian.balofhope.dlt.analyze.service.analysisstorage.IDltAnalysisReportService;
import top.tangtian.balofhope.dlt.analyze.service.vo.ComprehensiveAnalysisReport;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tangtian
 * @date 2026-01-23 10:52
 */
@Service
public class ScheduledGenerateReport {
	private static final Logger logger = LoggerFactory.getLogger(ScheduledGenerateReport.class);

	@Autowired
	private DltDataAnalysisService dataAnalysisService;

	@Autowired
	private DltAnalysisStorageService dltAnalysisStorageService;

	@Autowired
	private IDltAnalysisReportService dltAnalysisReportService;

//	/**
//	 * 定时任务1：每天凌晨2点执行全面分析（100期数据）
//	 */
//	@Scheduled(cron = "0 0 2 * * ?")
//	public void scheduledDailyAnalysis() {
//		logger.info("========== 定时任务：开始执行每日全面分析 ==========");
//		try {
//			executeAnalysisTask(100, "DAILY");
//			logger.info("========== 定时任务：每日全面分析完成 ==========");
//		} catch (Exception e) {
//			logger.error("定时任务执行失败", e);
//		}
//	}
//
//	/**
//	 * 定时任务2：每周一凌晨3点执行周报分析（500期数据）
//	 */
//	@Scheduled(cron = "0 0 3 ? * MON")
//	public void scheduledWeeklyAnalysis() {
//		logger.info("========== 定时任务：开始执行每周分析 ==========");
//		try {
//			executeAnalysisTask(500, "WEEKLY");
//			logger.info("========== 定时任务：每周分析完成 ==========");
//		} catch (Exception e) {
//			logger.error("周报分析执行失败", e);
//		}
//	}
//
//	/**
//	 * 定时任务3：每月1号凌晨4点执行月报分析（1000期数据）
//	 */
//	@Scheduled(cron = "0 0 4 1 * ?")
//	public void scheduledMonthlyAnalysis() {
//		logger.info("========== 定时任务：开始执行每月分析 ==========");
//		try {
//			executeAnalysisTask(1000, "MONTHLY");
//			logger.info("========== 定时任务：每月分析完成 ==========");
//		} catch (Exception e) {
//			logger.error("月报分析执行失败", e);
//		}
//	}
//
//	/**
//	 * 定时任务4：每2小时执行一次实时分析（最近30期）
//	 * 用于快速更新最新数据
//	 */
//	@Scheduled(cron = "0 0 */2 * * ?")
//	public void scheduledRealTimeAnalysis() {
//		logger.info("========== 定时任务：开始执行实时分析 ==========");
//		try {
//			executeAnalysisTask(30, "REALTIME");
//			logger.info("========== 定时任务：实时分析完成 ==========");
//		} catch (Exception e) {
//			logger.error("实时分析执行失败", e);
//		}
//	}
//
//	/**
//	 * 定时任务5：每天晚上22:30执行（大乐透开奖后30分钟）
//	 * 用于分析最新开奖数据
//	 */
//	@Scheduled(cron = "0 30 22 * * ?")
//	public void scheduledPostDrawAnalysis() {
//		logger.info("========== 定时任务：开始执行开奖后分析 ==========");
//		try {
//			// 等待3分钟确保数据已爬取
//			Thread.sleep(180000);
//			executeAnalysisTask(100, "POST_DRAW");
//			logger.info("========== 定时任务：开奖后分析完成 ==========");
//		} catch (Exception e) {
//			logger.error("开奖后分析执行失败", e);
//		}
//	}
//


	/**
	 * 核心分析执行方法
	 */
	@Transactional(rollbackFor = Exception.class)
	public String executeAnalysisTask(Integer analyzedPeriods, String reportType) {
		long startTime = System.currentTimeMillis();
		String batchNo = generateBatchNo(reportType);

		logger.info("开始执行分析任务，批次：{}，类型：{}，期数：{}", batchNo, reportType, analyzedPeriods);

		try {
			// 1. 生成综合分析报告
			ComprehensiveAnalysisReport report = dataAnalysisService.generateComprehensiveReport(analyzedPeriods);

			// 2. 保存各项分析结果到数据库
			dltAnalysisStorageService.saveNumberFrequency(report.getRedBallFrequency(), batchNo);
			dltAnalysisStorageService.saveNumberFrequency(report.getBlueBallFrequency(), batchNo);
			dltAnalysisStorageService.savePoolTrend(report.getPoolTrend());
			dltAnalysisStorageService.savePrizeStatistics(report.getPrizeStatistics(), batchNo);
			dltAnalysisStorageService.saveNumberOmission(report.getRedBallOmission(), batchNo);
			dltAnalysisStorageService.saveRecommendNumbers(report.getRecommendNumbers(), batchNo);

			// 3. 保存报告主记录
			DltAnalysisReportEntity entity = new DltAnalysisReportEntity();
			entity.setReportBatch(batchNo);
			entity.setReportType(reportType);
			entity.setAnalyzedPeriods(analyzedPeriods);
			entity.setCurrentPool(report.getPoolTrend() != null ? report.getPoolTrend().getCurrentPool() : null);
			entity.setAvgSales(report.getSalesTrend() != null ? report.getSalesTrend().getAvgSales() : null);
			entity.setGenerateTime(LocalDateTime.now());

			// 构建报告摘要JSON
			String summary = buildReportSummary(report);
			entity.setReportSummary(summary);

			dltAnalysisReportService.save(entity);

			long duration = System.currentTimeMillis() - startTime;
			logger.info("分析任务完成，批次：{}，耗时：{} 秒", batchNo, duration / 1000);

			return batchNo;

		} catch (Exception e) {
			logger.error("分析任务执行失败，批次：{}", batchNo, e);
			throw new RuntimeException("分析任务执行失败: " + e.getMessage(), e);
		}
	}
	/**
	 * 生成批次号
	 */
	private String generateBatchNo(String type) {
		return String.format("%s_%s_%d",
				type,
				LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")),
				System.currentTimeMillis());
	}

	/**
	 * 构建报告摘要JSON
	 */
	private String buildReportSummary(ComprehensiveAnalysisReport report) throws JsonProcessingException {
		Map<String, Object> summary = new HashMap<>();
		summary.put("analyzedPeriods", report.getAnalyzedPeriods());
		summary.put("generateTime", report.getGenerateTime());

		if (report.getRedBallFrequency() != null) {
			summary.put("redHotCount", report.getRedBallFrequency().getHotNumbers().size());
			summary.put("redColdCount", report.getRedBallFrequency().getColdNumbers().size());
		}

		if (report.getPoolTrend() != null) {
			summary.put("currentPool", report.getPoolTrend().getCurrentPool());
			summary.put("avgPool", report.getPoolTrend().getAvgPool());
		}

		if (report.getRecommendNumbers() != null) {
			summary.put("hasRecommend", true);
		}

		return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(summary);
	}

}
