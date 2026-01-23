package top.tangtian.balofhope.dlt.analyze.service.analysisstorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.tangtian.balofhope.dlt.analyze.entity.*;
import top.tangtian.balofhope.dlt.analyze.service.vo.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tangtian
 * @date 2026-01-23 09:24
 */
@Service
public class DltAnalysisStorageService {

	private static final Logger logger = LoggerFactory.getLogger(DltAnalysisStorageService.class);

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
	 * 保存号码频率分析结果
	 */
	@Transactional(rollbackFor = Exception.class)
	public void saveNumberFrequency(NumberFrequencyStats stats, String batchNo) {
		logger.info("保存号码频率分析结果，批次：{}", batchNo);

		List<DltNumberFrequencyEntity> entities = new ArrayList<>();

		for (NumberFrequency freq : stats.getFrequencies()) {
			DltNumberFrequencyEntity entity = new DltNumberFrequencyEntity();
			entity.setBatchNo(batchNo);
			entity.setBallType(stats.getType().equals("红球") ? "RED" : "BLUE");
			entity.setNumber(freq.getNumber());
			entity.setFrequencyCount(freq.getCount());
			entity.setFrequencyPercentage(freq.getPercentage());
			entity.setAnalyzedPeriods(stats.getTotalDraws());
			entity.setAnalysisTime(LocalDateTime.now());

			// 判断是否为热号或冷号
			entity.setIsHot(stats.getHotNumbers().stream()
					.anyMatch(h -> h.getNumber().equals(freq.getNumber())));
			entity.setIsCold(stats.getColdNumbers().stream()
					.anyMatch(c -> c.getNumber().equals(freq.getNumber())));

			entities.add(entity);
		}

		numberFrequencyService.saveBatch(entities);
		logger.info("保存完成，共 {} 条记录", entities.size());
	}

	/**
	 * 保存奖池趋势分析结果
	 */
	@Transactional(rollbackFor = Exception.class)
	public void savePoolTrend(PoolTrendAnalysis analysis) {
		logger.info("保存奖池趋势分析结果");

		List<DltPoolTrendEntity> entities = new ArrayList<>();
		BigDecimal previousPool = null;

		for (PoolTrendData data : analysis.getTrendData()) {
			DltPoolTrendEntity entity = new DltPoolTrendEntity();
			entity.setDrawNum(data.getDrawNum());
			entity.setDrawTime(data.getDrawTime());
			entity.setPoolBalance(data.getPoolBalance());

			// 计算环比增长率
			if (previousPool != null && previousPool.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal growthRate = data.getPoolBalance()
						.subtract(previousPool)
						.divide(previousPool, 4, RoundingMode.HALF_UP)
						.multiply(BigDecimal.valueOf(100));
				entity.setGrowthRate(growthRate);
			}

			previousPool = data.getPoolBalance();
			entities.add(entity);
		}

		poolTrendService.saveBatch(entities);
		logger.info("保存完成，共 {} 条记录", entities.size());
	}

	/**
	 * 保存奖项统计分析结果
	 */
	@Transactional(rollbackFor = Exception.class)
	public void savePrizeStatistics(PrizeStatisticsAnalysis analysis, String batchNo) {
		logger.info("保存奖项统计分析结果，批次：{}", batchNo);

		List<DltPrizeStatisticsEntity> entities = new ArrayList<>();

		for (PrizeLevelStats stats : analysis.getPrizeStats()) {
			DltPrizeStatisticsEntity entity = new DltPrizeStatisticsEntity();
			entity.setBatchNo(batchNo);
			entity.setPrizeLevel(stats.getPrizeLevel());
			entity.setTotalCount(stats.getTotalCount());
			entity.setTotalAmount(stats.getTotalAmount());
			entity.setAnalyzedPeriods(analysis.getAnalyzedDraws());
			entity.setAnalysisTime(LocalDateTime.now());

			// 计算平均奖金
			if (stats.getTotalCount() > 0) {
				entity.setAvgAmount(stats.getTotalAmount()
						.divide(BigDecimal.valueOf(stats.getTotalCount()), 2, RoundingMode.HALF_UP));
			}

			entities.add(entity);
		}

		prizeStatisticsService.saveBatch(entities);
		logger.info("保存完成，共 {} 条记录", entities.size());
	}

	/**
	 * 保存号码遗漏分析结果
	 */
	@Transactional(rollbackFor = Exception.class)
	public void saveNumberOmission(NumberOmissionAnalysis analysis, String batchNo) {
		logger.info("保存号码遗漏分析结果，批次：{}", batchNo);

		List<DltNumberOmissionEntity> entities = new ArrayList<>();

		for (NumberOmission omission : analysis.getOmissions()) {
			DltNumberOmissionEntity entity = new DltNumberOmissionEntity();
			entity.setBatchNo(batchNo);
			entity.setBallType(analysis.getType().equals("红球") ? "RED" : "BLUE");
			entity.setNumber(omission.getNumber());
			entity.setCurrentOmission(omission.getCurrentOmission());
			entity.setMaxOmission(omission.getMaxOmission());
			entity.setAnalysisTime(LocalDateTime.now());

			// 判断遗漏等级
			if (omission.getCurrentOmission() >= 20) {
				entity.setOmissionLevel("HIGH");
			} else if (omission.getCurrentOmission() >= 10) {
				entity.setOmissionLevel("MEDIUM");
			} else {
				entity.setOmissionLevel("LOW");
			}

			entities.add(entity);
		}

		numberOmissionService.saveBatch(entities);
		logger.info("保存完成，共 {} 条记录", entities.size());
	}

	/**
	 * 保存推荐号码
	 */
	@Transactional(rollbackFor = Exception.class)
	public void saveRecommendNumbers(RecommendNumbers recommend, String batchNo) {
		logger.info("保存推荐号码，批次：{}", batchNo);

		List<DltRecommendNumbersEntity> entities = new ArrayList<>();

		// 热号推荐
		DltRecommendNumbersEntity hotEntity = new DltRecommendNumbersEntity();
		hotEntity.setRecommendBatch(batchNo);
		hotEntity.setRecommendStrategy("HOT");
		hotEntity.setRedNumbers(recommend.getHotRedNumbers().stream()
				.map(String::valueOf)
				.collect(Collectors.joining(",")));
		hotEntity.setRecommendReason("基于高频号码统计");
		hotEntity.setRecommendTime(LocalDateTime.now());
		hotEntity.setIsDrawed(false);
		entities.add(hotEntity);

		// 遗漏号推荐
		DltRecommendNumbersEntity omissionEntity = new DltRecommendNumbersEntity();
		omissionEntity.setRecommendBatch(batchNo);
		omissionEntity.setRecommendStrategy("OMISSION");
		omissionEntity.setRedNumbers(recommend.getOmissionRedNumbers().stream()
				.map(String::valueOf)
				.collect(Collectors.joining(",")));
		omissionEntity.setRecommendReason("基于号码遗漏分析");
		omissionEntity.setRecommendTime(LocalDateTime.now());
		omissionEntity.setIsDrawed(false);
		entities.add(omissionEntity);

		// 均衡推荐
		DltRecommendNumbersEntity balancedEntity = new DltRecommendNumbersEntity();
		balancedEntity.setRecommendBatch(batchNo);
		balancedEntity.setRecommendStrategy("BALANCED");
		balancedEntity.setRedNumbers(recommend.getBalancedRedNumbers().stream()
				.map(String::valueOf)
				.collect(Collectors.joining(",")));
		balancedEntity.setBlueNumbers(recommend.getRecommendBlueNumbers().stream()
				.map(String::valueOf)
				.collect(Collectors.joining(",")));
		balancedEntity.setRecommendReason("综合热号、冷号和遗漏号");
		balancedEntity.setRecommendTime(LocalDateTime.now());
		balancedEntity.setIsDrawed(false);
		entities.add(balancedEntity);

		recommendNumbersService.saveBatch(entities);
		logger.info("保存完成，共 {} 条推荐", entities.size());
	}

	/**
	 * 保存综合分析报告
	 */
	@Transactional(rollbackFor = Exception.class)
	public String saveComprehensiveReport(ComprehensiveAnalysisReport report) {
		String batchNo = generateBatchNo();
		logger.info("保存综合分析报告，批次：{}", batchNo);

		// 保存各项分析结果
		saveNumberFrequency(report.getRedBallFrequency(), batchNo);
		saveNumberFrequency(report.getBlueBallFrequency(), batchNo);
		savePoolTrend(report.getPoolTrend());
		savePrizeStatistics(report.getPrizeStatistics(), batchNo);
		saveNumberOmission(report.getRedBallOmission(), batchNo);
		saveRecommendNumbers(report.getRecommendNumbers(), batchNo);

		// 保存报告主记录
		DltAnalysisReportEntity entity = new DltAnalysisReportEntity();
		entity.setReportBatch(batchNo);
		entity.setReportType("COMPREHENSIVE");
		entity.setAnalyzedPeriods(report.getAnalyzedPeriods());
		entity.setCurrentPool(report.getPoolTrend().getCurrentPool());
		entity.setAvgSales(report.getSalesTrend().getAvgSales());
		entity.setGenerateTime(LocalDateTime.now());

		analysisReportService.save(entity);

		logger.info("综合分析报告保存完成");
		return batchNo;
	}

	/**
	 * 生成批次号
	 */
	private String generateBatchNo() {
		return "BATCH_" + System.currentTimeMillis();
	}
}
