package top.tangtian;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import top.tangtian.esanalysis.IntelligentAnalysisApplication;
import top.tangtian.esanalysis.service.AIAnalysisService;
import top.tangtian.esanalysis.service.ElasticsearchService;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author tangtian
 * @date 2025-12-03 09:37
 */
@SpringBootTest(classes = {IntelligentAnalysisApplication.class},webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("AI分析报告单元测试")
public class AnalysisScheduledTasksTest {
	@Autowired
	private AIAnalysisService aiAnalysisService;

	@Autowired
	private ElasticsearchService esService;

	@Test
	@Order(1)
	@DisplayName("测试1: 每日分析报告生成 - 正常场景")
	void testGenerateDailyReport() {
		LocalDate testDate = LocalDate.of(2024, 12, 1);

		// When
		String report = aiAnalysisService.generateDailyReport(testDate);


		System.out.println("\n=== 每日分析报告 ===");
		System.out.println(report);
		System.out.println("报告长度: " + report.length() + " 字符\n");
	}

	@Test
	@Order(2)
	@DisplayName("测试2: 周度对比分析 - 正常场景")
	void testGenerateWeeklyComparison() {
		// When
		String report = aiAnalysisService.generateWeeklyComparison();

		// Then
		assertNotNull(report);
		assertFalse(report.isEmpty());


		System.out.println("\n=== 周度对比分析 ===");
		System.out.println(report);
		System.out.println("报告长度: " + report.length() + " 字符\n");
	}

	@Test
	@Order(3)
	@DisplayName("测试3: 热点问题分析 - 正常场景")
	void testAnalyzeHotTopics() {


		// When
		String report = aiAnalysisService.analyzeHotTopics(7);



		System.out.println("\n=== 热点问题分析 ===");
		System.out.println(report);
		System.out.println("报告长度: " + report.length() + " 字符\n");
	}

	@Test
	@Order(4)
	@DisplayName("测试4: 组织绩效评估 - 正常场景")
	void testEvaluateOrganizationPerformance() {
		// Given
		String orgName = "高新区管委会";
		LocalDate start = LocalDate.of(2025, 11, 1);
		LocalDate end = LocalDate.of(2025, 11, 30);

		// When
		String report = aiAnalysisService.evaluateOrganizationPerformance(orgName, start, end);



		System.out.println("\n=== 组织绩效评估 ===");
		System.out.println(report);
		System.out.println("报告长度: " + report.length() + " 字符\n");
	}

	@Test
	@Order(5)
	@DisplayName("测试5: 趋势预测分析 - 正常场景")
	void testPredictTrends() {

		// When
		String report = aiAnalysisService.predictTrends(30, 7);



		System.out.println("\n=== 趋势预测分析 ===");
		System.out.println(report);
		System.out.println("报告长度: " + report.length() + " 字符\n");
	}

	@Test
	@Order(6)
	@DisplayName("测试6: 空数据场景")
	void testEmptyDataHandling() {
		// Given - 空数据
		Map<String, Object> emptyData = new HashMap<>();
		emptyData.put("total", 0L);
		emptyData.put("statusDistribution", Collections.emptyMap());
		emptyData.put("organizationDistribution", Collections.emptyMap());
		emptyData.put("fieldDistribution", Collections.emptyMap());
		emptyData.put("sourceDistribution", Collections.emptyMap());
		emptyData.put("avgSatisfaction", 0.0);
		emptyData.put("replyRate", "0%");
		emptyData.put("replyCount", 0L);
		emptyData.put("dailyTrend", Collections.emptyList());

		when(esService.analyzeDailyData(any(), any()))
				.thenReturn(emptyData);

		// When
		String report = aiAnalysisService.generateDailyReport(LocalDate.now());

		// Then
		assertNotNull(report, "即使数据为空，也应返回报告");
		assertFalse(report.isEmpty(), "报告不应为空字符串");

		System.out.println("\n=== 空数据场景报告 ===");
		System.out.println(report);
		System.out.println("报告长度: " + report.length() + " 字符\n");
	}

	@Test
	@Order(7)
	@DisplayName("测试7: 异常数据场景 - 极端值")
	void testAbnormalDataHandling() {


		// When
		String report = aiAnalysisService.generateDailyReport(LocalDate.now());

		// Then
		assertNotNull(report);
		assertFalse(report.isEmpty());

		System.out.println("\n=== 异常数据场景报告 ===");
		System.out.println(report);
		System.out.println("报告长度: " + report.length() + " 字符\n");
	}

	@Test
	@Order(8)
	@DisplayName("测试8: 不同时间范围")
	void testDifferentDateRanges() {


		// When & Then - 昨天
		String yesterdayReport = aiAnalysisService.generateDailyReport(
				LocalDate.now().minusDays(1)
		);
		assertNotNull(yesterdayReport);

		// When & Then - 上周
		String lastWeekReport = aiAnalysisService.generateDailyReport(
				LocalDate.now().minusDays(7)
		);
		assertNotNull(lastWeekReport);

		// When & Then - 上月
		String lastMonthReport = aiAnalysisService.generateDailyReport(
				LocalDate.now().minusDays(30)
		);
		assertNotNull(lastMonthReport);

		System.out.println("\n=== 不同时间范围测试通过 ===");
		System.out.println("昨天报告长度: " + yesterdayReport.length());
		System.out.println("上周报告长度: " + lastWeekReport.length());
		System.out.println("上月报告长度: " + lastMonthReport.length() + "\n");
	}

	@Test
	@Order(9)
	@DisplayName("测试9: 多次调用稳定性")
	void testMultipleCallsStability() {


		// When - 连续调用5次
		List<String> reports = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			String report = aiAnalysisService.generateDailyReport(LocalDate.now());
			reports.add(report);
		}

		// Then
		assertEquals(5, reports.size(), "应该生成5份报告");
		for (String report : reports) {
			assertNotNull(report);
			assertFalse(report.isEmpty());
		}

		System.out.println("\n=== 多次调用稳定性测试通过 ===");
		System.out.println("成功生成 " + reports.size() + " 份报告\n");
	}

	@Test
	@Order(10)
	@DisplayName("测试10: 不同天数热点分析")
	void testHotTopicsWithDifferentDays() {
		// Given
		Map<String, Object> hotTopics = new HashMap<>();
		hotTopics.put("keywords", Arrays.asList("测试关键词"));
		hotTopics.put("tags", Map.of("测试标签", 10L));

		when(esService.getHotTopics(anyInt())).thenReturn(hotTopics);

		// When & Then - 7天
		String report7Days = aiAnalysisService.analyzeHotTopics(7);


		// When & Then - 30天
		String report30Days = aiAnalysisService.analyzeHotTopics(30);


		System.out.println("\n=== 不同天数热点分析测试通过 ===");
		System.out.println("7天报告长度: " + report7Days.length());
		System.out.println("30天报告长度: " + report30Days.length() + "\n");
	}


}
