package top.tangtian.esanalysis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.tangtian.esanalysis.entity.FeedbackEntity;
import top.tangtian.esanalysis.service.AIAnalysisService;
import top.tangtian.esanalysis.service.ElasticsearchService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author tangtian
 * @date 2025-12-01 09:40
 */
@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {
	private final ElasticsearchService esService;
	private final AIAnalysisService aiAnalysisService;

	/**
	 * 批量导入反馈数据
	 */
	@PostMapping("/import")
	public ResponseEntity<Map<String, Object>> importData(@RequestBody List<FeedbackEntity> feedbacks) {
		boolean success = esService.bulkInsert(feedbacks);
		return ResponseEntity.ok(Map.of(
				"success", success,
				"count", feedbacks.size()
		));
	}

	/**
	 * 获取指定日期范围的分析数据
	 */
	@GetMapping("/data")
	public ResponseEntity<Map<String, Object>> getAnalysisData(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		Map<String, Object> data = esService.analyzeDailyData(startDate, endDate);
		return ResponseEntity.ok(data);
	}

	/**
	 * 生成每日AI分析报告
	 */
	@GetMapping("/report/daily")
	public ResponseEntity<Map<String, String>> getDailyReport(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		LocalDate targetDate = date != null ? date : LocalDate.now().minusDays(1);
		String report = aiAnalysisService.generateDailyReport(targetDate);
		return ResponseEntity.ok(Map.of(
				"date", targetDate.toString(),
				"report", report
		));
	}

	/**
	 * 生成周度对比分析
	 */
	@GetMapping("/report/weekly")
	public ResponseEntity<Map<String, String>> getWeeklyReport() {
		String report = aiAnalysisService.generateWeeklyComparison();
		return ResponseEntity.ok(Map.of(
				"type", "weekly",
				"report", report
		));
	}

	/**
	 * 热点问题分析
	 */
	@GetMapping("/hot-topics")
	public ResponseEntity<Map<String, Object>> getHotTopics(
			@RequestParam(defaultValue = "7") int days) {
		String report = aiAnalysisService.analyzeHotTopics(days);
		Map<String, Object> hotTopics = esService.getHotTopics(days);
		return ResponseEntity.ok(Map.of(
				"days", days,
				"data", hotTopics,
				"analysis", report
		));
	}

	/**
	 * 组织机构绩效评估
	 */
	@GetMapping("/performance/{orgName}")
	public ResponseEntity<Map<String, String>> evaluatePerformance(
			@PathVariable String orgName,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		String report = aiAnalysisService.evaluateOrganizationPerformance(orgName, startDate, endDate);
		return ResponseEntity.ok(Map.of(
				"organization", orgName,
				"period", startDate + " to " + endDate,
				"report", report
		));
	}

	/**
	 * 趋势预测分析
	 */
	@GetMapping("/forecast")
	public ResponseEntity<Map<String, Object>> getForecast(
			@RequestParam(defaultValue = "30") int historicalDays,
			@RequestParam(defaultValue = "7") int forecastDays) {
		String report = aiAnalysisService.predictTrends(historicalDays, forecastDays);
		return ResponseEntity.ok(Map.of(
				"historicalDays", historicalDays,
				"forecastDays", forecastDays,
				"forecast", report
		));
	}
}
