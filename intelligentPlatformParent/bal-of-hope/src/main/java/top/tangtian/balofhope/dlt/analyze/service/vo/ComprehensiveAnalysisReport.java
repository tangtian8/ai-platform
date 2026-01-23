package top.tangtian.balofhope.dlt.analyze.service.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author tangtian
 * @date 2026-01-23 09:36
 */
@Data
public class ComprehensiveAnalysisReport {
	private Integer analyzedPeriods;                    // 分析期数
	private Date generateTime;                          // 生成时间
	private NumberFrequencyStats redBallFrequency;      // 红球频率分析
	private NumberFrequencyStats blueBallFrequency;     // 蓝球频率分析
	private PoolTrendAnalysis poolTrend;                // 奖池趋势分析
	private SalesTrendAnalysis salesTrend;              // 销售额趋势分析
	private PrizeStatisticsAnalysis prizeStatistics;    // 奖项统计分析
	private NumberOmissionAnalysis redBallOmission;     // 红球遗漏分析
	private RecommendNumbers recommendNumbers;          // 推荐号码
}
