package top.tangtian.esanalysis.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.tangtian.esanalysis.service.AIAnalysisService;

import java.time.LocalDate;

/**
 * @author tangtian
 * @date 2025-12-01 09:41
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnalysisScheduledTasks {

	private final AIAnalysisService aiAnalysisService;

	/**
	 * 每天凌晨1点生成前一天的分析报告
	 */
	@Scheduled(cron = "0 0 1 * * ?")
	public void generateDailyReport() {
		try {
			LocalDate now = LocalDate.now();

			LocalDate yesterday = LocalDate.now().minusDays(1);
			log.info("开始生成 {} 的每日分析报告", yesterday);

			String report = aiAnalysisService.generateDailyReport(yesterday,now);

			// 保存报告到数据库或文件系统
			saveReport("daily", yesterday.toString(), report);

			log.info("每日分析报告生成完成");
		} catch (Exception e) {
			log.error("生成每日报告失败", e);
		}
	}

	/**
	 * 每周一凌晨2点生成周度对比分析
	 */
	@Scheduled(cron = "0 0 2 ? * MON")
	public void generateWeeklyReport() {
		try {
			log.info("开始生成周度对比分析报告");

			String report = aiAnalysisService.generateWeeklyComparison();

			saveReport("weekly", LocalDate.now().toString(), report);

			log.info("周度分析报告生成完成");
		} catch (Exception e) {
			log.error("生成周度报告失败", e);
		}
	}

	/**
	 * 每天早上8点分析最近7天的热点问题
	 */
	@Scheduled(cron = "0 0 8 * * ?")
	public void analyzeHotTopics() {
		try {
			log.info("开始分析热点问题");

			String report = aiAnalysisService.analyzeHotTopics(7);

			saveReport("hot_topics", LocalDate.now().toString(), report);

			log.info("热点问题分析完成");
		} catch (Exception e) {
			log.error("热点问题分析失败", e);
		}
	}

	/**
	 * 每月1日凌晨3点生成月度预测分析
	 */
	@Scheduled(cron = "0 0 3 1 * ?")
	public void generateMonthlyForecast() {
		try {
			log.info("开始生成月度预测分析");

			// 基于过去30天预测未来7天
			String report = aiAnalysisService.predictTrends(30, 7);

			saveReport("forecast", LocalDate.now().toString(), report);

			log.info("月度预测分析完成");
		} catch (Exception e) {
			log.error("生成预测分析失败", e);
		}
	}

	private void saveReport(String type, String date, String content) {
		// 实现报告保存逻辑：可以保存到数据库、文件系统或发送邮件
		log.info("保存 {} 类型报告，日期：{}", type, date);
		// TODO: 实现具体的保存逻辑
	}
}
