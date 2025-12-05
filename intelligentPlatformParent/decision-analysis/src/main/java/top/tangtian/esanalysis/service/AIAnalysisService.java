package top.tangtian.esanalysis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tangtian
 * @date 2025-12-01 09:42
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIAnalysisService {
	private final ChatClient chatClient;
	private final ElasticsearchService esService;

	/**
	 * 每日智能分析报告
	 */
	public String generateDailyReport(LocalDate strdate,LocalDate endDate) {
		// 1. 获取ES分析数据
		Map<String, Object> analysisData = esService.analyzeDailyData(strdate, endDate);

		// 2. 构建提示词
		String prompt = buildDailyReportPrompt(strdate,endDate, analysisData);

		// 3. 调用AI生成分析（使用新版本API）
		String report = chatClient.prompt()
				.user(prompt)
				.call()
				.content();

		log.info("完成日期 {}-{} 的AI分析报告", strdate,endDate);
		return report;
	}

	/**
	 * 周度对比分析
	 */
	public String generateWeeklyComparison() {
		LocalDate endDate = LocalDate.now();
		LocalDate startDate = endDate.minusDays(7);

		Map<String, Object> thisWeek = esService.analyzeDailyData(startDate, endDate);
		Map<String, Object> lastWeek = esService.analyzeDailyData(
				startDate.minusDays(7),
				startDate.minusDays(1)
		);

		String prompt = buildWeeklyComparisonPrompt(thisWeek, lastWeek);

		return chatClient.prompt()
				.user(prompt)
				.call()
				.content();
	}

	/**
	 * 热点问题智能分析
	 */
	public String analyzeHotTopics(int days) {
		Map<String, Object> hotTopics = esService.getHotTopics(days);

		String prompt = buildHotTopicsPrompt(days, hotTopics);

		return chatClient.prompt()
				.user(prompt)
				.call()
				.content();
	}

	/**
	 * 组织机构绩效评估
	 */
	public String evaluateOrganizationPerformance(String orgName, LocalDate start, LocalDate end) {
		Map<String, Object> data = esService.analyzeDailyData(start, end);

		String prompt = buildPerformancePrompt(orgName, start, end, data);

		return chatClient.prompt()
				.user(prompt)
				.call()
				.content();
	}

	/**
	 * 智能预测分析
	 */
	public String predictTrends(int historicalDays, int forecastDays) {
		LocalDate endDate = LocalDate.now();
		LocalDate startDate = endDate.minusDays(historicalDays);

		Map<String, Object> historical = esService.analyzeDailyData(startDate, endDate);

		String prompt = buildPredictionPrompt(historicalDays, forecastDays, historical);

		return chatClient.prompt()
				.user(prompt)
				.call()
				.content();
	}

	// ========== 私有方法：构建提示词 ==========

	private String buildDailyReportPrompt(LocalDate strDate,LocalDate endDate, Map<String, Object> data) {
		return String.format("""
            作为数据分析专家，请分析以下反馈数据并生成专业的分析报告：
            
            开始日期：%s
            结束日期：%s
            总数据量：%s
            
            处理状态分布：
            %s
            
            组织机构分布：
            %s
            
            标题热点关键词：
            %s
            
            内容热点关键词：
            %s
            
            来源渠道分布：
            %s
            
            平均满意度：%s
            回复率：%s
            
            每日趋势：
            %s
            
            请从以下维度进行分析：
            1. 数据整体概况与关键指标
            2. 处理效率分析（回复率、处理状态）
            3. 用户满意度分析
            4. 各组织机构工作表现
            5. 热点问题与趋势识别（基于标题和内容关键词）
            6. 存在的问题与风险预警
            7. 改进建议
            
            请用专业、简洁的语言，生成一份结构化的分析报告。
            """,
				strDate,
				endDate,
				data.get("total"),
				data.get("statusDistribution"),
				data.get("organizationDistribution"),
				data.get("titleKeywords"),
				data.get("contentKeywords"),
				data.get("sourceDistribution"),
				data.get("avgSatisfaction"),
				data.get("replyRate"),
				data.get("dailyTrend")
		);
	}

	private String buildWeeklyComparisonPrompt(Map<String, Object> thisWeek, Map<String, Object> lastWeek) {
		return String.format("""
            作为数据分析专家，请对比分析以下两周的反馈数据：
            
            本周数据：
            %s
            
            上周数据：
            %s
            
            请分析：
            1. 关键指标变化趋势（数量、回复率、满意度等）
            2. 各维度环比变化分析
            3. 显著改善的方面
            4. 需要关注的下降趋势
            5. 下周工作建议
            
            请生成专业的对比分析报告。
            """,
				thisWeek,
				lastWeek
		);
	}

	private String buildHotTopicsPrompt(int days, Map<String, Object> hotTopics) {
		return String.format("""
            作为舆情分析专家，请分析近%d天的热点问题：
            
            热点关键词：
            %s
            
            高频标签：
            %s
            
            请分析：
            1. 识别主要关注点和问题类型
            2. 分析问题的严重程度和紧急性
            3. 预测可能的发展趋势
            4. 提出应对策略和解决方案
            5. 舆情风险预警
            
            请生成专业的热点分析报告。
            """,
				days,
				hotTopics.get("keywords"),
				hotTopics.get("tags")
		);
	}

	private String buildPerformancePrompt(String orgName, LocalDate start, LocalDate end, Map<String, Object> data) {
		return String.format("""
            请对以下组织机构的工作绩效进行评估：
            
            组织名称：%s
            统计周期：%s 至 %s
            
            数据概况：
            %s
            
            请评估：
            1. 工作量与处理效率
            2. 响应速度与服务质量
            3. 用户满意度表现
            4. 与其他组织的对比分析
            5. 优势与不足
            6. 改进建议与目标设定
            
            请生成专业的绩效评估报告。
            """,
				orgName,
				start,
				end,
				data
		);
	}

	private String buildPredictionPrompt(int historicalDays, int forecastDays, Map<String, Object> historical) {
		return String.format("""
            基于过去%d天的历史数据，预测未来%d天的趋势：
            
            历史数据：
            %s
            
            请预测：
            1. 反馈量趋势预测
            2. 各类问题的可能变化
            3. 资源需求预测
            4. 潜在风险点识别
            5. 建议的应对措施
            
            请基于数据趋势和业务常识，生成合理的预测分析报告。
            """,
				historicalDays,
				forecastDays,
				historical
		);
	}
}