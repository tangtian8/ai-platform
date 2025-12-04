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
	public String generateDailyReport(LocalDate date) {
		// 1. 获取ES分析数据
		Map<String, Object> analysisData = esService.analyzeDailyData(date, date);

		// 2. 构建AI提示词
		String promptText = """
            作为数据分析专家，请分析以下反馈数据并生成专业的分析报告：
            
            日期：{date}
            总数据量：{total}
            
            处理状态分布：
            {statusDistribution}
            
            组织机构分布：
            {organizationDistribution}
            
            领域分类分布：
            {fieldDistribution}
            
            来源渠道分布：
            {sourceDistribution}
            
            平均满意度：{avgSatisfaction}
            回复率：{replyRate}
            
            每日趋势：
            {dailyTrend}
            
            请从以下维度进行分析：
            1. 数据整体概况与关键指标
            2. 处理效率分析（回复率、处理状态）
            3. 用户满意度分析
            4. 各组织机构工作表现
            5. 热点问题与趋势识别
            6. 存在的问题与风险预警
            7. 改进建议
            
            请用专业、简洁的语言，生成一份结构化的分析报告。
            """;

		PromptTemplate promptTemplate = new PromptTemplate(promptText);
		Map<String, Object> params = new HashMap<>();
		params.put("date", date.toString());
		params.putAll(analysisData);

		Prompt prompt = promptTemplate.create(params);

		// 3. 调用AI生成分析
		String report = chatClient.prompt(prompt).call().content();

		log.info("完成日期 {} 的AI分析报告", date);
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

		String promptText = """
            作为数据分析专家，请对比分析以下两周的反馈数据：
            
            本周数据：
            {thisWeek}
            
            上周数据：
            {lastWeek}
            
            请分析：
            1. 关键指标变化趋势（数量、回复率、满意度等）
            2. 各维度环比变化分析
            3. 显著改善的方面
            4. 需要关注的下降趋势
            5. 下周工作建议
            
            请生成专业的对比分析报告。
            """;

		PromptTemplate template = new PromptTemplate(promptText);
		Map<String, Object> params = new HashMap<>();
		params.put("thisWeek", thisWeek);
		params.put("lastWeek", lastWeek);

		return chatClient.prompt(template.create(params)).call().content();
	}

	/**
	 * 热点问题智能分析
	 */
	public String analyzeHotTopics(int days) {
		Map<String, Object> hotTopics = esService.getHotTopics(days);

		String promptText = """
            作为舆情分析专家，请分析近{days}天的热点问题：
            
            热点关键词：
            {keywords}
            
            高频标签：
            {tags}
            
            请分析：
            1. 识别主要关注点和问题类型
            2. 分析问题的严重程度和紧急性
            3. 预测可能的发展趋势
            4. 提出应对策略和解决方案
            5. 舆情风险预警
            
            请生成专业的热点分析报告。
            """;

		PromptTemplate template = new PromptTemplate(promptText);
		Map<String, Object> params = new HashMap<>();
		params.put("days", days);
		params.putAll(hotTopics);

		return chatClient.prompt(template.create(params)).call().content();
	}

	/**
	 * 组织机构绩效评估
	 */
	public String evaluateOrganizationPerformance(String orgName, LocalDate start, LocalDate end) {
		Map<String, Object> data = esService.analyzeDailyData(start, end);

		String promptText = """
            请对以下组织机构的工作绩效进行评估：
            
            组织名称：{orgName}
            统计周期：{start} 至 {end}
            
            数据概况：
            {data}
            
            请评估：
            1. 工作量与处理效率
            2. 响应速度与服务质量
            3. 用户满意度表现
            4. 与其他组织的对比分析
            5. 优势与不足
            6. 改进建议与目标设定
            
            请生成专业的绩效评估报告。
            """;

		PromptTemplate template = new PromptTemplate(promptText);
		Map<String, Object> params = new HashMap<>();
		params.put("orgName", orgName);
		params.put("start", start.toString());
		params.put("end", end.toString());
		params.put("data", data);

		return chatClient.prompt(template.create(params)).call().content();
	}

	/**
	 * 智能预测分析
	 */
	public String predictTrends(int historicalDays, int forecastDays) {
		LocalDate endDate = LocalDate.now();
		LocalDate startDate = endDate.minusDays(historicalDays);

		Map<String, Object> historical = esService.analyzeDailyData(startDate, endDate);

		String promptText = """
            基于过去{historicalDays}天的历史数据，预测未来{forecastDays}天的趋势：
            
            历史数据：
            {historical}
            
            请预测：
            1. 反馈量趋势预测
            2. 各类问题的可能变化
            3. 资源需求预测
            4. 潜在风险点识别
            5. 建议的应对措施
            
            请基于数据趋势和业务常识，生成合理的预测分析报告。
            """;

		PromptTemplate template = new PromptTemplate(promptText);
		Map<String, Object> params = new HashMap<>();
		params.put("historicalDays", historicalDays);
		params.put("forecastDays", forecastDays);
		params.put("historical", historical);

		return chatClient.prompt(template.create(params)).call().content();
	}
}