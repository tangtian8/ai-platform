package top.tangtian.balofhope.dlt.analyze.service.analysisstorage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.tangtian.balofhope.dlt.analyze.service.vo.*;
import top.tangtian.balofhope.dlt.storage.entity.DltLotteryDrawEntity;
import top.tangtian.balofhope.dlt.storage.entity.DltPrizeLevelEntity;
import top.tangtian.balofhope.dlt.storage.service.IDltLotteryDrawService;
import top.tangtian.balofhope.dlt.storage.service.IDltPrizeLevelService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author tangtian
 * @date 2026-01-23 11:00
 * ============================================================
 * 大乐透数据分析服务
 * ============================================================
 */
@Service
public class DltDataAnalysisService {

	private static final Logger logger = LoggerFactory.getLogger(DltDataAnalysisService.class);

	@Autowired
	private IDltLotteryDrawService lotteryDrawService;

	@Autowired
	private IDltPrizeLevelService prizeLevelService;

	/**
	 * 1. 红球号码频率分析
	 */
	public NumberFrequencyStats analyzeRedBallFrequency(Integer recentCount) {
		logger.info("开始分析红球频率，最近 {} 期", recentCount);

		List<DltLotteryDrawEntity> draws = getRecentDraws(recentCount);

		// 统计红球出现频率（01-35）
		Map<Integer, Integer> frequencyMap = new HashMap<>();
		for (int i = 1; i <= 35; i++) {
			frequencyMap.put(i, 0);
		}

		int totalDraws = 0;
		for (DltLotteryDrawEntity draw : draws) {
			String result = draw.getLotteryDrawResult();
			if (result != null && !result.isEmpty()) {
				String[] balls = result.split(" ");
				// 前5个是红球
				for (int i = 0; i < 5 && i < balls.length; i++) {
					try {
						int ball = Integer.parseInt(balls[i]);
						frequencyMap.put(ball, frequencyMap.getOrDefault(ball, 0) + 1);
					} catch (NumberFormatException e) {
						logger.warn("解析红球号码失败: {}", balls[i]);
					}
				}
				totalDraws++;
			}
		}

		return buildFrequencyStats("红球", frequencyMap, totalDraws, 10);
	}

	/**
	 * 2. 蓝球号码频率分析
	 */
	public NumberFrequencyStats analyzeBlueBallFrequency(Integer recentCount) {
		logger.info("开始分析蓝球频率，最近 {} 期", recentCount);

		List<DltLotteryDrawEntity> draws = getRecentDraws(recentCount);

		// 统计蓝球出现频率（01-12）
		Map<Integer, Integer> frequencyMap = new HashMap<>();
		for (int i = 1; i <= 12; i++) {
			frequencyMap.put(i, 0);
		}

		int totalDraws = 0;
		for (DltLotteryDrawEntity draw : draws) {
			String result = draw.getLotteryDrawResult();
			if (result != null && !result.isEmpty()) {
				String[] balls = result.split(" ");
				// 后2个是蓝球
				if (balls.length >= 6) {
					for (int i = 5; i < balls.length; i++) {
						try {
							int ball = Integer.parseInt(balls[i]);
							frequencyMap.put(ball, frequencyMap.getOrDefault(ball, 0) + 1);
						} catch (NumberFormatException e) {
							logger.warn("解析蓝球号码失败: {}", balls[i]);
						}
					}
				}
				totalDraws++;
			}
		}

		return buildFrequencyStats("蓝球", frequencyMap, totalDraws, 5);
	}

	/**
	 * 3. 奖池趋势分析
	 */
	public PoolTrendAnalysis analyzePoolTrend(Integer recentCount) {
		logger.info("开始分析奖池趋势，最近 {} 期", recentCount);

		List<DltLotteryDrawEntity> draws = getRecentDraws(recentCount);
		Collections.reverse(draws); // 按时间正序

		List<PoolTrendData> trendData = new ArrayList<>();
		BigDecimal maxPool = BigDecimal.ZERO;
		BigDecimal minPool = new BigDecimal("999999999999");
		BigDecimal totalPool = BigDecimal.ZERO;

		for (DltLotteryDrawEntity draw : draws) {
			String poolStr = draw.getPoolBalanceAfterdraw();
			if (poolStr != null && !poolStr.isEmpty()) {
				try {
					BigDecimal pool = new BigDecimal(poolStr.replace(",", ""));

					PoolTrendData data = new PoolTrendData();
					data.setDrawNum(draw.getLotteryDrawNum());
					data.setDrawTime(draw.getLotteryDrawTime());
					data.setPoolBalance(pool);
					trendData.add(data);

					if (pool.compareTo(maxPool) > 0) maxPool = pool;
					if (pool.compareTo(minPool) < 0) minPool = pool;
					totalPool = totalPool.add(pool);
				} catch (Exception e) {
					logger.warn("解析奖池金额失败: {}", poolStr);
				}
			}
		}

		PoolTrendAnalysis analysis = new PoolTrendAnalysis();
		analysis.setTrendData(trendData);
		analysis.setMaxPool(maxPool);
		analysis.setMinPool(minPool);
		analysis.setAvgPool(trendData.size() > 0 ?
				totalPool.divide(BigDecimal.valueOf(trendData.size()), 2, RoundingMode.HALF_UP) :
				BigDecimal.ZERO);
		analysis.setCurrentPool(trendData.isEmpty() ? BigDecimal.ZERO :
				trendData.get(trendData.size() - 1).getPoolBalance());

		return analysis;
	}

	/**
	 * 4. 销售额趋势分析
	 */
	public SalesTrendAnalysis analyzeSalesTrend(Integer recentCount) {
		logger.info("开始分析销售额趋势，最近 {} 期", recentCount);

		List<DltLotteryDrawEntity> draws = getRecentDraws(recentCount);
		Collections.reverse(draws);

		List<SalesTrendData> trendData = new ArrayList<>();
		BigDecimal totalSales = BigDecimal.ZERO;
		BigDecimal maxSales = BigDecimal.ZERO;
		BigDecimal minSales = new BigDecimal("999999999999");

		for (DltLotteryDrawEntity draw : draws) {
			String salesStr = draw.getTotalSaleAmount();
			if (salesStr != null && !salesStr.isEmpty()) {
				try {
					BigDecimal sales = new BigDecimal(salesStr.replace(",", ""));

					SalesTrendData data = new SalesTrendData();
					data.setDrawNum(draw.getLotteryDrawNum());
					data.setDrawTime(draw.getLotteryDrawTime());
					data.setSalesAmount(sales);
					trendData.add(data);

					totalSales = totalSales.add(sales);
					if (sales.compareTo(maxSales) > 0) maxSales = sales;
					if (sales.compareTo(minSales) < 0) minSales = sales;
				} catch (Exception e) {
					logger.warn("解析销售额失败: {}", salesStr);
				}
			}
		}

		SalesTrendAnalysis analysis = new SalesTrendAnalysis();
		analysis.setTrendData(trendData);
		analysis.setTotalSales(totalSales);
		analysis.setMaxSales(maxSales);
		analysis.setMinSales(minSales);
		analysis.setAvgSales(trendData.size() > 0 ?
				totalSales.divide(BigDecimal.valueOf(trendData.size()), 2, RoundingMode.HALF_UP) :
				BigDecimal.ZERO);

		return analysis;
	}

	/**
	 * 5. 奖项中奖统计分析
	 */
	public PrizeStatisticsAnalysis analyzePrizeStatistics(Integer recentCount) {
		logger.info("开始分析奖项统计，最近 {} 期", recentCount);

		List<DltLotteryDrawEntity> draws = getRecentDraws(recentCount);

		Map<String, PrizeLevelStats> prizeStatsMap = new LinkedHashMap<>();
		String[] prizeLevels = {"一等奖", "二等奖", "三等奖", "四等奖", "五等奖",
				"六等奖", "七等奖", "八等奖", "九等奖"};

		for (String level : prizeLevels) {
			PrizeLevelStats stats = new PrizeLevelStats();
			stats.setPrizeLevel(level);
			stats.setTotalCount(0);
			stats.setTotalAmount(BigDecimal.ZERO);
			prizeStatsMap.put(level, stats);
		}

		for (DltLotteryDrawEntity draw : draws) {
			QueryWrapper<DltPrizeLevelEntity> wrapper = new QueryWrapper<>();
			wrapper.eq("lottery_draw_num", draw.getLotteryDrawNum())
					.eq("is_deleted", 0);
			List<DltPrizeLevelEntity> prizes = prizeLevelService.list(wrapper);

			for (DltPrizeLevelEntity prize : prizes) {
				String level = prize.getPrizeLevel().replace("(追加)", "");
				PrizeLevelStats stats = prizeStatsMap.get(level);

				if (stats != null) {
					try {
						int count = Integer.parseInt(prize.getStakeCount().replace(",", ""));
						BigDecimal amount = new BigDecimal(prize.getTotalPrizeamount().replace(",", ""));

						stats.setTotalCount(stats.getTotalCount() + count);
						stats.setTotalAmount(stats.getTotalAmount().add(amount));
					} catch (Exception e) {
						logger.warn("解析奖项数据失败: {}", prize.getPrizeLevel());
					}
				}
			}
		}

		PrizeStatisticsAnalysis analysis = new PrizeStatisticsAnalysis();
		analysis.setPrizeStats(new ArrayList<>(prizeStatsMap.values()));
		analysis.setAnalyzedDraws(draws.size());

		return analysis;
	}

	/**
	 * 6. 号码遗漏分析（红球）
	 */
	public NumberOmissionAnalysis analyzeRedBallOmission() {
		logger.info("开始分析红球遗漏情况");

		List<DltLotteryDrawEntity> draws = getRecentDraws(100);
		Collections.reverse(draws); // 按时间正序

		Map<Integer, Integer> lastAppearIndex = new HashMap<>();
		Map<Integer, Integer> currentOmission = new HashMap<>();
		Map<Integer, Integer> maxOmission = new HashMap<>();

		for (int i = 1; i <= 35; i++) {
			currentOmission.put(i, 0);
			maxOmission.put(i, 0);
		}

		for (int index = 0; index < draws.size(); index++) {
			String result = draws.get(index).getLotteryDrawResult();
			if (result != null && !result.isEmpty()) {
				String[] balls = result.split(" ");
				Set<Integer> currentBalls = new HashSet<>();

				for (int i = 0; i < 5 && i < balls.length; i++) {
					try {
						int ball = Integer.parseInt(balls[i]);
						currentBalls.add(ball);
						lastAppearIndex.put(ball, index);
					} catch (NumberFormatException e) {
						// ignore
					}
				}

				// 更新遗漏值
				for (int ball = 1; ball <= 35; ball++) {
					if (currentBalls.contains(ball)) {
						currentOmission.put(ball, 0);
					} else {
						int omission = currentOmission.get(ball) + 1;
						currentOmission.put(ball, omission);
						maxOmission.put(ball, Math.max(maxOmission.get(ball), omission));
					}
				}
			}
		}

		List<NumberOmission> omissions = new ArrayList<>();
		for (int ball = 1; ball <= 35; ball++) {
			NumberOmission omission = new NumberOmission();
			omission.setNumber(ball);
			omission.setCurrentOmission(currentOmission.get(ball));
			omission.setMaxOmission(maxOmission.get(ball));
			omissions.add(omission);
		}

		omissions.sort((a, b) -> b.getCurrentOmission().compareTo(a.getCurrentOmission()));

		NumberOmissionAnalysis analysis = new NumberOmissionAnalysis();
		analysis.setType("红球");
		analysis.setOmissions(omissions);
		analysis.setTopOmissions(omissions.stream().limit(10).collect(Collectors.toList()));

		return analysis;
	}

	/**
	 * 7. 综合分析报告
	 */
	public ComprehensiveAnalysisReport generateComprehensiveReport(Integer recentCount) {
		logger.info("生成综合分析报告，最近 {} 期", recentCount);

		ComprehensiveAnalysisReport report = new ComprehensiveAnalysisReport();
		report.setAnalyzedPeriods(recentCount != null ? recentCount : 100);
		report.setGenerateTime(new Date());

		// 1. 号码频率分析
		report.setRedBallFrequency(analyzeRedBallFrequency(recentCount));
		report.setBlueBallFrequency(analyzeBlueBallFrequency(recentCount));

		// 2. 趋势分析
		report.setPoolTrend(analyzePoolTrend(recentCount));
		report.setSalesTrend(analyzeSalesTrend(recentCount));

		// 3. 奖项统计
		report.setPrizeStatistics(analyzePrizeStatistics(recentCount));

		// 4. 遗漏分析
		report.setRedBallOmission(analyzeRedBallOmission());

		// 5. 推荐号码
		report.setRecommendNumbers(generateRecommendNumbers());

		logger.info("综合分析报告生成完成");
		return report;
	}

	/**
	 * 8. 智能推荐号码（基于统计分析）
	 */
	public RecommendNumbers generateRecommendNumbers() {
		logger.info("生成推荐号码");

		// 基于最近100期数据分析
		NumberFrequencyStats redStats = analyzeRedBallFrequency(100);
		NumberFrequencyStats blueStats = analyzeBlueBallFrequency(100);
		NumberOmissionAnalysis omission = analyzeRedBallOmission();

		// 策略1: 热号推荐（高频号码）
		List<Integer> hotReds = redStats.getHotNumbers().stream()
				.limit(10)
				.map(NumberFrequency::getNumber)
				.collect(Collectors.toList());

		// 策略2: 遗漏号推荐（当前遗漏较大的号码）
		List<Integer> omissionReds = omission.getTopOmissions().stream()
				.limit(10)
				.map(NumberOmission::getNumber)
				.collect(Collectors.toList());

		// 策略3: 均衡推荐（热号+冷号+遗漏号）
		Set<Integer> balancedReds = new HashSet<>();
		balancedReds.addAll(hotReds.subList(0, Math.min(3, hotReds.size())));
		balancedReds.addAll(omissionReds.subList(0, Math.min(2, omissionReds.size())));

		// 蓝球推荐
		List<Integer> recommendBlues = blueStats.getHotNumbers().stream()
				.limit(3)
				.map(NumberFrequency::getNumber)
				.collect(Collectors.toList());

		RecommendNumbers recommend = new RecommendNumbers();
		recommend.setHotRedNumbers(hotReds);
		recommend.setOmissionRedNumbers(omissionReds);
		recommend.setBalancedRedNumbers(new ArrayList<>(balancedReds));
		recommend.setRecommendBlueNumbers(recommendBlues);
		recommend.setGenerateTime(new Date());

		return recommend;
	}

	// ========== 辅助方法 ==========

	private List<DltLotteryDrawEntity> getRecentDraws(Integer count) {
		QueryWrapper<DltLotteryDrawEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("is_deleted", 0)
				.orderByDesc("lottery_draw_num")
				.last("LIMIT " + (count != null ? count : 100));
		return lotteryDrawService.list(wrapper);
	}

	private NumberFrequencyStats buildFrequencyStats(String type, Map<Integer, Integer> frequencyMap,
													 int totalDraws, int topCount) {
		List<NumberFrequency> frequencies = new ArrayList<>();
		for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
			NumberFrequency freq = new NumberFrequency();
			freq.setNumber(entry.getKey());
			freq.setCount(entry.getValue());
			freq.setPercentage(totalDraws > 0 ?
					BigDecimal.valueOf(entry.getValue() * 100.0 / totalDraws)
							.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
			frequencies.add(freq);
		}

		frequencies.sort((a, b) -> b.getCount().compareTo(a.getCount()));

		NumberFrequencyStats stats = new NumberFrequencyStats();
		stats.setType(type);
		stats.setTotalDraws(totalDraws);
		stats.setFrequencies(frequencies);
		stats.setHotNumbers(frequencies.stream().limit(topCount).collect(Collectors.toList()));
		stats.setColdNumbers(frequencies.stream()
				.skip(Math.max(0, frequencies.size() - topCount))
				.collect(Collectors.toList()));

		return stats;
	}
}
