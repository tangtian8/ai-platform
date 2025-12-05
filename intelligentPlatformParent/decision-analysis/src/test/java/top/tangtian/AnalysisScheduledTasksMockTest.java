package top.tangtian;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
public class AnalysisScheduledTasksMockTest {
	@Autowired
	private AIAnalysisService aiAnalysisService;

	@MockBean
	private ElasticsearchService esService;


	private Map<String, Object> mockData;

	@BeforeEach
	void setUp() {
		// 准备 Mock 数据
		mockData = createMockAnalysisData();
	}

	@Test
	@Order(1)
	@DisplayName("测试1: 每日分析报告生成 - 正常场景")
	void testGenerateDailyReport() {
		// Given
		LocalDate testDate = LocalDate.of(2025, 12, 1);
		when(esService.analyzeDailyData(testDate, testDate))
				.thenReturn(mockData);

		// When
		String report = aiAnalysisService.generateDailyReport(testDate,testDate);

		// Then
		assertNotNull(report, "报告不应为null");
		assertFalse(report.isEmpty(), "报告不应为空字符串");
		assertTrue(report.length() > 50, "报告内容应该有足够的长度");

		// 验证ES服务被调用
		verify(esService, times(1)).analyzeDailyData(testDate, testDate);

		System.out.println("\n=== 每日分析报告 ===");
		System.out.println(report);
		System.out.println("报告长度: " + report.length() + " 字符\n");
	}

	@Test
	@Order(2)
	@DisplayName("测试2: 周度对比分析 - 正常场景")
	void testGenerateWeeklyComparison() {
		// Given
		LocalDate endDate = LocalDate.now();
		LocalDate startDate = endDate.minusDays(7);

		Map<String, Object> thisWeek = createMockAnalysisData();
		Map<String, Object> lastWeek = createMockAnalysisData();
		thisWeek.put("total", 150L);
		lastWeek.put("total", 120L);

		when(esService.analyzeDailyData(startDate, endDate))
				.thenReturn(thisWeek);
		when(esService.analyzeDailyData(any(), any()))
				.thenReturn(lastWeek);

		// When
		String report = aiAnalysisService.generateWeeklyComparison();

		// Then
		assertNotNull(report);
		assertFalse(report.isEmpty());

		// 验证ES服务被调用了2次（本周+上周）
		verify(esService, atLeast(2)).analyzeDailyData(any(), any());

		System.out.println("\n=== 周度对比分析 ===");
		System.out.println(report);
		System.out.println("报告长度: " + report.length() + " 字符\n");
	}

	@Test
	@Order(3)
	@DisplayName("测试3: 热点问题分析 - 正常场景")
	void testAnalyzeHotTopics() {
		// Given
		Map<String, Object> hotTopics = new HashMap<>();
		hotTopics.put("keywords", Arrays.asList("交通", "环境", "教育"));
		hotTopics.put("tags", Map.of(
				"交通拥堵", 25L,
				"噪音污染", 18L,
				"学校建设", 15L
		));

		when(esService.getHotTopics(7)).thenReturn(hotTopics);

		// When
		String report = aiAnalysisService.analyzeHotTopics(7);

		// Then
		assertNotNull(report);
		assertFalse(report.isEmpty());
		verify(esService, times(1)).getHotTopics(7);

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

		when(esService.analyzeDailyData(start, end))
				.thenReturn(mockData);

		// When
		String report = aiAnalysisService.evaluateOrganizationPerformance(orgName, start, end);

		// Then
		assertNotNull(report);
		assertFalse(report.isEmpty());
		verify(esService, times(1)).analyzeDailyData(start, end);

		System.out.println("\n=== 组织绩效评估 ===");
		System.out.println(report);
		System.out.println("报告长度: " + report.length() + " 字符\n");
	}

	@Test
	@Order(5)
	@DisplayName("测试5: 趋势预测分析 - 正常场景")
	void testPredictTrends() {
		// Given
		when(esService.analyzeDailyData(any(), any()))
				.thenReturn(mockData);

		// When
		String report = aiAnalysisService.predictTrends(30, 7);

		// Then
		assertNotNull(report);
		assertFalse(report.isEmpty());
		verify(esService, times(1)).analyzeDailyData(any(), any());

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
		String report = aiAnalysisService.generateDailyReport(LocalDate.now(),LocalDate.now());

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
		// Given - 异常数据
		Map<String, Object> abnormalData = createMockAnalysisData();
		abnormalData.put("total", 10000L); // 异常高
		abnormalData.put("avgSatisfaction", 1.2); // 异常低
		abnormalData.put("replyRate", "5.0%"); // 异常低

		when(esService.analyzeDailyData(any(), any()))
				.thenReturn(abnormalData);

		// When
		String report = aiAnalysisService.generateDailyReport(LocalDate.now(),LocalDate.now());

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
		// Given
		when(esService.analyzeDailyData(any(), any()))
				.thenReturn(mockData);

		// When & Then - 昨天
		String yesterdayReport = aiAnalysisService.generateDailyReport(
				LocalDate.now().minusDays(1),LocalDate.now()
		);
		assertNotNull(yesterdayReport);

		// When & Then - 上周
		String lastWeekReport = aiAnalysisService.generateDailyReport(
				LocalDate.now().minusDays(7),LocalDate.now()
		);
		assertNotNull(lastWeekReport);

		// When & Then - 上月
		String lastMonthReport = aiAnalysisService.generateDailyReport(
				LocalDate.now().minusDays(30),LocalDate.now()
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
		// Given
		when(esService.analyzeDailyData(any(), any()))
				.thenReturn(mockData);

		// When - 连续调用5次
		List<String> reports = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			String report = aiAnalysisService.generateDailyReport(LocalDate.now(),LocalDate.now());
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
		assertNotNull(report7Days);
		verify(esService, times(1)).getHotTopics(7);

		// When & Then - 30天
		String report30Days = aiAnalysisService.analyzeHotTopics(30);
		assertNotNull(report30Days);
		verify(esService, times(1)).getHotTopics(30);

		System.out.println("\n=== 不同天数热点分析测试通过 ===");
		System.out.println("7天报告长度: " + report7Days.length());
		System.out.println("30天报告长度: " + report30Days.length() + "\n");
	}

	// ========== 辅助方法 ==========

	/**
	 * 创建模拟分析数据
	 */
	private Map<String, Object> createMockAnalysisData() {
		Map<String, Object> data = new HashMap<>();

		// 基础统计
		data.put("total", 126L);
		data.put("avgSatisfaction", 4.2);
		data.put("replyRate", "85.5%");
		data.put("replyCount", 108L);

		// 状态分布
		Map<String, Long> statusDist = new LinkedHashMap<>();
		statusDist.put("已处理", 95L);
		statusDist.put("处理中", 20L);
		statusDist.put("未处理", 11L);
		data.put("statusDistribution", statusDist);

		// 组织分布
		Map<String, Long> orgDist = new LinkedHashMap<>();
		orgDist.put("高新区管委会", 45L);
		orgDist.put("市政府办公室", 38L);
		orgDist.put("交通局", 28L);
		orgDist.put("环保局", 15L);
		data.put("organizationDistribution", orgDist);

		// 领域分布
		Map<String, Long> fieldDist = new LinkedHashMap<>();
		fieldDist.put("交通出行", 42L);
		fieldDist.put("环境卫生", 35L);
		fieldDist.put("教育文化", 28L);
		fieldDist.put("医疗健康", 21L);
		data.put("fieldDistribution", fieldDist);

		// 来源分布
		Map<String, Long> sourceDist = new LinkedHashMap<>();
		sourceDist.put("WAP", 78L);
		sourceDist.put("微信", 32L);
		sourceDist.put("APP", 16L);
		data.put("sourceDistribution", sourceDist);

		// 每日趋势
		List<Map<String, Object>> dailyTrend = new ArrayList<>();
		for (int i = 0; i < 7; i++) {
			Map<String, Object> dayData = new HashMap<>();
			dayData.put("date", LocalDate.now().minusDays(6 - i).toString());
			dayData.put("count", 15 + (i * 2));
			dailyTrend.add(dayData);
		}
		data.put("dailyTrend", dailyTrend);

		return data;
	}

	@AfterEach
	void tearDown() {
		// 清理工作（如果需要）
		reset(esService);
	}
}
