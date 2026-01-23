package top.tangtian.balofhope.dlt.analyze.service.analysisquery;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.tangtian.balofhope.dlt.analyze.entity.*;
import top.tangtian.balofhope.dlt.analyze.service.analysisstorage.*;
import top.tangtian.balofhope.dlt.analyze.service.vo.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tangtian
 * @date 2026-01-23 09:46
 */
@Service
public class DltAnalysisQueryService {

	private static final Logger logger = LoggerFactory.getLogger(DltAnalysisQueryService.class);

	@Autowired
	private IDltNumberFrequencyService numberFrequencyService;

	@Autowired
	private IDltPoolTrendService poolTrendService;

	@Autowired
	private IDltPrizeStatisticsService prizeStatisticsService;

	@Autowired
	private IDltNumberOmissionService numberOmissionService;

	@Autowired
	private IDltRecommendNumbersService recommendNumbersService;

	@Autowired
	private IDltAnalysisReportService analysisReportService;

	/**
	 * 1. 从数据库获取红球频率分析结果
	 */
	public NumberFrequencyStats getRedBallFrequencyFromDB(String batchNo) {
		logger.info("从数据库获取红球频率分析，批次：{}", batchNo);

		// 如果没有指定批次，获取最新批次
		if (batchNo == null || batchNo.isEmpty()) {
			batchNo = getLatestBatchNo();
		}

		QueryWrapper<DltNumberFrequencyEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("batch_no", batchNo)
				.eq("ball_type", "RED")
				.eq("is_deleted", 0)
				.orderByDesc("frequency_count");

		List<DltNumberFrequencyEntity> entities = numberFrequencyService.list(wrapper);

		if (entities.isEmpty()) {
			logger.warn("未找到红球频率数据，批次：{}", batchNo);
			return null;
		}

		return convertToFrequencyStats(entities, "红球");
	}

	/**
	 * 2. 从数据库获取蓝球频率分析结果
	 */
	public NumberFrequencyStats getBlueBallFrequencyFromDB(String batchNo) {
		logger.info("从数据库获取蓝球频率分析，批次：{}", batchNo);

		if (batchNo == null || batchNo.isEmpty()) {
			batchNo = getLatestBatchNo();
		}

		QueryWrapper<DltNumberFrequencyEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("batch_no", batchNo)
				.eq("ball_type", "BLUE")
				.eq("is_deleted", 0)
				.orderByDesc("frequency_count");

		List<DltNumberFrequencyEntity> entities = numberFrequencyService.list(wrapper);

		if (entities.isEmpty()) {
			logger.warn("未找到蓝球频率数据，批次：{}", batchNo);
			return null;
		}

		return convertToFrequencyStats(entities, "蓝球");
	}

	/**
	 * 3. 从数据库获取奖池趋势分析结果
	 */
	public PoolTrendAnalysis getPoolTrendFromDB(Integer limit) {
		logger.info("从数据库获取奖池趋势，最近 {} 期", limit);

		QueryWrapper<DltPoolTrendEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("is_deleted", 0)
				.orderByDesc("draw_num")
				.last("LIMIT " + (limit != null ? limit : 50));

		List<DltPoolTrendEntity> entities = poolTrendService.list(wrapper);

		if (entities.isEmpty()) {
			logger.warn("未找到奖池趋势数据");
			return null;
		}

		// 反转列表，按时间正序
		Collections.reverse(entities);

		List<PoolTrendData> trendData = new ArrayList<>();
		BigDecimal maxPool = BigDecimal.ZERO;
		BigDecimal minPool = new BigDecimal("999999999999");
		BigDecimal totalPool = BigDecimal.ZERO;

		for (DltPoolTrendEntity entity : entities) {
			PoolTrendData data = new PoolTrendData();
			data.setDrawNum(entity.getDrawNum());
			data.setDrawTime(entity.getDrawTime());
			data.setPoolBalance(entity.getPoolBalance());
			trendData.add(data);

			if (entity.getPoolBalance().compareTo(maxPool) > 0) {
				maxPool = entity.getPoolBalance();
			}
			if (entity.getPoolBalance().compareTo(minPool) < 0) {
				minPool = entity.getPoolBalance();
			}
			totalPool = totalPool.add(entity.getPoolBalance());
		}

		PoolTrendAnalysis analysis = new PoolTrendAnalysis();
		analysis.setTrendData(trendData);
		analysis.setMaxPool(maxPool);
		analysis.setMinPool(minPool);
		analysis.setAvgPool(totalPool.divide(BigDecimal.valueOf(entities.size()), 2, BigDecimal.ROUND_HALF_UP));
		analysis.setCurrentPool(trendData.isEmpty() ? BigDecimal.ZERO :
				trendData.get(trendData.size() - 1).getPoolBalance());

		return analysis;
	}

	/**
	 * 4. 从数据库获取销售额趋势分析结果
	 */
	public SalesTrendAnalysis getSalesTrendFromDB(Integer limit) {
		logger.info("从数据库获取销售额趋势，最近 {} 期", limit);

		QueryWrapper<DltPoolTrendEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("is_deleted", 0)
				.isNotNull("sales_amount")
				.orderByDesc("draw_num")
				.last("LIMIT " + (limit != null ? limit : 50));

		List<DltPoolTrendEntity> entities = poolTrendService.list(wrapper);

		if (entities.isEmpty()) {
			logger.warn("未找到销售额趋势数据");
			return null;
		}

		Collections.reverse(entities);

		List<SalesTrendData> trendData = new ArrayList<>();
		BigDecimal totalSales = BigDecimal.ZERO;
		BigDecimal maxSales = BigDecimal.ZERO;
		BigDecimal minSales = new BigDecimal("999999999999");

		for (DltPoolTrendEntity entity : entities) {
			if (entity.getSalesAmount() != null) {
				SalesTrendData data = new SalesTrendData();
				data.setDrawNum(entity.getDrawNum());
				data.setDrawTime(entity.getDrawTime());
				data.setSalesAmount(entity.getSalesAmount());
				trendData.add(data);

				totalSales = totalSales.add(entity.getSalesAmount());
				if (entity.getSalesAmount().compareTo(maxSales) > 0) {
					maxSales = entity.getSalesAmount();
				}
				if (entity.getSalesAmount().compareTo(minSales) < 0) {
					minSales = entity.getSalesAmount();
				}
			}
		}

		SalesTrendAnalysis analysis = new SalesTrendAnalysis();
		analysis.setTrendData(trendData);
		analysis.setTotalSales(totalSales);
		analysis.setMaxSales(maxSales);
		analysis.setMinSales(minSales);
		analysis.setAvgSales(trendData.size() > 0 ?
				totalSales.divide(BigDecimal.valueOf(trendData.size()), 2, BigDecimal.ROUND_HALF_UP) :
				BigDecimal.ZERO);

		return analysis;
	}

	/**
	 * 5. 从数据库获取奖项统计分析结果
	 */
	public PrizeStatisticsAnalysis getPrizeStatisticsFromDB(String batchNo) {
		logger.info("从数据库获取奖项统计，批次：{}", batchNo);

		if (batchNo == null || batchNo.isEmpty()) {
			batchNo = getLatestBatchNo();
		}

		QueryWrapper<DltPrizeStatisticsEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("batch_no", batchNo)
				.eq("is_deleted", 0)
				.orderBySql("CASE prize_level " +
						"WHEN '一等奖' THEN 1 " +
						"WHEN '二等奖' THEN 2 " +
						"WHEN '三等奖' THEN 3 " +
						"WHEN '四等奖' THEN 4 " +
						"WHEN '五等奖' THEN 5 " +
						"WHEN '六等奖' THEN 6 " +
						"WHEN '七等奖' THEN 7 " +
						"WHEN '八等奖' THEN 8 " +
						"WHEN '九等奖' THEN 9 " +
						"ELSE 10 END");

		List<DltPrizeStatisticsEntity> entities = prizeStatisticsService.list(wrapper);

		if (entities.isEmpty()) {
			logger.warn("未找到奖项统计数据，批次：{}", batchNo);
			return null;
		}

		List<PrizeLevelStats> prizeStats = new ArrayList<>();
		for (DltPrizeStatisticsEntity entity : entities) {
			PrizeLevelStats stats = new PrizeLevelStats();
			stats.setPrizeLevel(entity.getPrizeLevel());
			stats.setTotalCount(entity.getTotalCount());
			stats.setTotalAmount(entity.getTotalAmount());
			prizeStats.add(stats);
		}

		PrizeStatisticsAnalysis analysis = new PrizeStatisticsAnalysis();
		analysis.setPrizeStats(prizeStats);
		analysis.setAnalyzedDraws(entities.isEmpty() ? 0 : entities.get(0).getAnalyzedPeriods());

		return analysis;
	}

	/**
	 * 6. 从数据库获取号码遗漏分析结果
	 */
	public NumberOmissionAnalysis getNumberOmissionFromDB(String batchNo, String ballType) {
		logger.info("从数据库获取号码遗漏分析，批次：{}，类型：{}", batchNo, ballType);

		if (batchNo == null || batchNo.isEmpty()) {
			batchNo = getLatestBatchNo();
		}

		QueryWrapper<DltNumberOmissionEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("batch_no", batchNo)
				.eq("ball_type", ballType)
				.eq("is_deleted", 0)
				.orderByDesc("current_omission");

		List<DltNumberOmissionEntity> entities = numberOmissionService.list(wrapper);

		if (entities.isEmpty()) {
			logger.warn("未找到遗漏数据，批次：{}，类型：{}", batchNo, ballType);
			return null;
		}

		List<NumberOmission> omissions = new ArrayList<>();
		for (DltNumberOmissionEntity entity : entities) {
			NumberOmission omission = new NumberOmission();
			omission.setNumber(entity.getNumber());
			omission.setCurrentOmission(entity.getCurrentOmission());
			omission.setMaxOmission(entity.getMaxOmission());
			omissions.add(omission);
		}

		NumberOmissionAnalysis analysis = new NumberOmissionAnalysis();
		analysis.setType(ballType.equals("RED") ? "红球" : "蓝球");
		analysis.setOmissions(omissions);
		analysis.setTopOmissions(omissions.stream().limit(10).collect(Collectors.toList()));

		return analysis;
	}

	/**
	 * 7. 从数据库获取推荐号码
	 */
	public RecommendNumbers getRecommendNumbersFromDB(String batchNo) {
		logger.info("从数据库获取推荐号码，批次：{}", batchNo);

		if (batchNo == null || batchNo.isEmpty()) {
			batchNo = getLatestBatchNo();
		}

		QueryWrapper<DltRecommendNumbersEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("recommend_batch", batchNo)
				.eq("is_deleted", 0);

		List<DltRecommendNumbersEntity> entities = recommendNumbersService.list(wrapper);

		if (entities.isEmpty()) {
			logger.warn("未找到推荐号码，批次：{}", batchNo);
			return null;
		}

		RecommendNumbers recommend = new RecommendNumbers();

		for (DltRecommendNumbersEntity entity : entities) {
			String strategy = entity.getRecommendStrategy();
			List<Integer> redNumbers = parseNumbers(entity.getRedNumbers());
			List<Integer> blueNumbers = parseNumbers(entity.getBlueNumbers());

			switch (strategy) {
				case "HOT":
					recommend.setHotRedNumbers(redNumbers);
					break;
				case "OMISSION":
					recommend.setOmissionRedNumbers(redNumbers);
					break;
				case "BALANCED":
					recommend.setBalancedRedNumbers(redNumbers);
					recommend.setRecommendBlueNumbers(blueNumbers);
					recommend.setGenerateTime(entity.getRecommendTime() != null ?
							Date.from(entity.getRecommendTime().atZone(java.time.ZoneId.systemDefault()).toInstant()) :
							new Date());
					break;
			}
		}

		return recommend;
	}

	/**
	 * 8. 从数据库获取综合分析报告
	 */
	public ComprehensiveAnalysisReport getComprehensiveReportFromDB(String batchNo) {
		logger.info("从数据库获取综合分析报告，批次：{}", batchNo);

		if (batchNo == null || batchNo.isEmpty()) {
			batchNo = getLatestBatchNo();
		}

		// 获取报告主记录
		QueryWrapper<DltAnalysisReportEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("report_batch", batchNo)
				.eq("is_deleted", 0);
		DltAnalysisReportEntity reportEntity = analysisReportService.getOne(wrapper);

		if (reportEntity == null) {
			logger.warn("未找到分析报告，批次：{}", batchNo);
			return null;
		}

		// 组装综合报告
		ComprehensiveAnalysisReport report = new ComprehensiveAnalysisReport();
		report.setAnalyzedPeriods(reportEntity.getAnalyzedPeriods());
		report.setGenerateTime(reportEntity.getGenerateTime() != null ?
				Date.from(reportEntity.getGenerateTime().atZone(java.time.ZoneId.systemDefault()).toInstant()) :
				new Date());

		// 获取各项分析结果
		report.setRedBallFrequency(getRedBallFrequencyFromDB(batchNo));
		report.setBlueBallFrequency(getBlueBallFrequencyFromDB(batchNo));
		report.setPoolTrend(getPoolTrendFromDB(reportEntity.getAnalyzedPeriods()));
		report.setSalesTrend(getSalesTrendFromDB(reportEntity.getAnalyzedPeriods()));
		report.setPrizeStatistics(getPrizeStatisticsFromDB(batchNo));
		report.setRedBallOmission(getNumberOmissionFromDB(batchNo, "RED"));
		report.setRecommendNumbers(getRecommendNumbersFromDB(batchNo));

		logger.info("综合分析报告获取完成");
		return report;
	}

	/**
	 * 9. 获取最新的分析批次号
	 */
	public String getLatestBatchNo() {
		QueryWrapper<DltAnalysisReportEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("is_deleted", 0)
				.orderByDesc("generate_time")
				.last("LIMIT 1");

		DltAnalysisReportEntity latest = analysisReportService.getOne(wrapper);

		if (latest != null) {
			logger.debug("最新批次号：{}", latest.getReportBatch());
			return latest.getReportBatch();
		}

		logger.warn("未找到任何分析报告");
		return null;
	}

	/**
	 * 10. 获取历史批次列表
	 */
	public List<AnalysisReportSummary> getReportHistory(Integer limit) {
		logger.info("获取历史分析报告列表，数量：{}", limit);

		QueryWrapper<DltAnalysisReportEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("is_deleted", 0)
				.orderByDesc("generate_time")
				.last("LIMIT " + (limit != null ? limit : 10));

		List<DltAnalysisReportEntity> entities = analysisReportService.list(wrapper);

		return entities.stream().map(entity -> {
			AnalysisReportSummary summary = new AnalysisReportSummary();
			summary.setBatchNo(entity.getReportBatch());
			summary.setReportType(entity.getReportType());
			summary.setAnalyzedPeriods(entity.getAnalyzedPeriods());
			summary.setCurrentPool(entity.getCurrentPool());
			summary.setAvgSales(entity.getAvgSales());
			summary.setGenerateTime(entity.getGenerateTime() != null ?
					Date.from(entity.getGenerateTime().atZone(java.time.ZoneId.systemDefault()).toInstant()) :
					null);
			return summary;
		}).collect(Collectors.toList());
	}

	/**
	 * 11. 获取热号TOP排行（最新批次）
	 */
	public List<NumberFrequency> getHotNumbersRanking(String ballType, Integer limit) {
		String batchNo = getLatestBatchNo();

		QueryWrapper<DltNumberFrequencyEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("batch_no", batchNo)
				.eq("ball_type", ballType)
				.eq("is_hot", true)
				.eq("is_deleted", 0)
				.orderByDesc("frequency_count")
				.last("LIMIT " + (limit != null ? limit : 10));

		List<DltNumberFrequencyEntity> entities = numberFrequencyService.list(wrapper);

		return entities.stream().map(entity -> {
			NumberFrequency freq = new NumberFrequency();
			freq.setNumber(entity.getNumber());
			freq.setCount(entity.getFrequencyCount());
			freq.setPercentage(entity.getFrequencyPercentage());
			return freq;
		}).collect(Collectors.toList());
	}

	/**
	 * 12. 获取高遗漏号码（最新批次）
	 */
	public List<NumberOmission> getHighOmissionNumbers(String ballType, Integer limit) {
		String batchNo = getLatestBatchNo();

		QueryWrapper<DltNumberOmissionEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("batch_no", batchNo)
				.eq("ball_type", ballType)
				.eq("omission_level", "HIGH")
				.eq("is_deleted", 0)
				.orderByDesc("current_omission")
				.last("LIMIT " + (limit != null ? limit : 10));

		List<DltNumberOmissionEntity> entities = numberOmissionService.list(wrapper);

		return entities.stream().map(entity -> {
			NumberOmission omission = new NumberOmission();
			omission.setNumber(entity.getNumber());
			omission.setCurrentOmission(entity.getCurrentOmission());
			omission.setMaxOmission(entity.getMaxOmission());
			return omission;
		}).collect(Collectors.toList());
	}

	// ========== 辅助方法 ==========

	private NumberFrequencyStats convertToFrequencyStats(List<DltNumberFrequencyEntity> entities, String type) {
		List<NumberFrequency> frequencies = new ArrayList<>();
		List<NumberFrequency> hotNumbers = new ArrayList<>();
		List<NumberFrequency> coldNumbers = new ArrayList<>();

		Integer totalDraws = entities.isEmpty() ? 0 : entities.get(0).getAnalyzedPeriods();

		for (DltNumberFrequencyEntity entity : entities) {
			NumberFrequency freq = new NumberFrequency();
			freq.setNumber(entity.getNumber());
			freq.setCount(entity.getFrequencyCount());
			freq.setPercentage(entity.getFrequencyPercentage());
			frequencies.add(freq);

			if (entity.getIsHot()) {
				hotNumbers.add(freq);
			}
			if (entity.getIsCold()) {
				coldNumbers.add(freq);
			}
		}

		NumberFrequencyStats stats = new NumberFrequencyStats();
		stats.setType(type);
		stats.setTotalDraws(totalDraws);
		stats.setFrequencies(frequencies);
		stats.setHotNumbers(hotNumbers);
		stats.setColdNumbers(coldNumbers);

		return stats;
	}

	private List<Integer> parseNumbers(String numbersStr) {
		if (numbersStr == null || numbersStr.isEmpty()) {
			return new ArrayList<>();
		}

		return Arrays.stream(numbersStr.split(","))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.map(Integer::parseInt)
				.collect(Collectors.toList());
	}
}
