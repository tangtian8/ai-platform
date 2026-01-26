package top.tangtian.balofhope.dlt.recommend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.tangtian.balofhope.dlt.analyze.service.analysisstorage.impl.DltDataAnalysisService;
import top.tangtian.balofhope.dlt.analyze.service.vo.RecommendNumbers;
import top.tangtian.balofhope.dlt.recommend.entity.DltRecommendTaskEntity;
import top.tangtian.balofhope.dlt.storage.entity.DltLotteryDrawEntity;
import top.tangtian.balofhope.dlt.storage.service.IDltLotteryDrawService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
/**
		* ============================================================
		* 4. 推荐任务生成服务（增加用户维度）
		* ============================================================
		*/
@Service
public class DltRecommendTaskGenerator {

	private static final Logger logger = LoggerFactory.getLogger(DltRecommendTaskGenerator.class);

	@Autowired
	private DltRecommendTaskService recommendTaskService;

	@Autowired
	private DltDataAnalysisService dataAnalysisService;

	@Autowired
	private IDltLotteryDrawService lotteryDrawService;

	/**
	 * 生成推荐任务（增加用户参数）
	 */
	@Transactional(rollbackFor = Exception.class)
	public String generateRecommendTask(Long userId, String userName, Integer analyzedPeriods, String strategy) {
		logger.info("开始生成推荐任务，用户：{}，策略：{}，分析期数：{}", userId, strategy, analyzedPeriods);

		// 获取目标期号（下一期）
		String targetDrawNum = getNextDrawNum();

		// 检查用户是否已经为该期号生成过推荐
		if (recommendTaskService.hasRecommendForDrawNum(userId, targetDrawNum)) {
			logger.warn("用户 {} 已为期号 {} 生成过推荐，拒绝重复生成", userId, targetDrawNum);
			throw new RuntimeException("您已为期号 " + targetDrawNum + " 生成过推荐，每期只能推荐一次");
		}

		String batchNo = generateBatchNo(userId, strategy);

		// 生成4组推荐号码
		List<RecommendNumbers> recommendsList = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			RecommendNumbers recommend = dataAnalysisService.generateRecommendNumbers(analyzedPeriods);
			recommendsList.add(recommend);
		}

		// 保存到数据库
		DltRecommendTaskEntity entity = new DltRecommendTaskEntity();
		entity.setUserId(userId);
		entity.setUserName(userName);
		entity.setRecommendBatch(batchNo);
		entity.setRecommendStrategy(strategy);
		entity.setAnalyzedPeriods(analyzedPeriods);
		entity.setTargetDrawNum(targetDrawNum);
		entity.setRecommendTime(LocalDateTime.now());
		entity.setIsDrawed(false);

		// 推荐1
		entity.setRedBalls1(joinNumbers(recommendsList.get(0).getBalancedRedNumbers()));
		entity.setBlueBalls1(joinNumbers(recommendsList.get(0).getRecommendBlueNumbers()));

		// 推荐2
		entity.setRedBalls2(joinNumbers(recommendsList.get(1).getBalancedRedNumbers()));
		entity.setBlueBalls2(joinNumbers(recommendsList.get(1).getRecommendBlueNumbers()));

		// 推荐3
		entity.setRedBalls3(joinNumbers(recommendsList.get(2).getBalancedRedNumbers()));
		entity.setBlueBalls3(joinNumbers(recommendsList.get(2).getRecommendBlueNumbers()));

		// 推荐4
		entity.setRedBalls4(joinNumbers(recommendsList.get(3).getBalancedRedNumbers()));
		entity.setBlueBalls4(joinNumbers(recommendsList.get(3).getRecommendBlueNumbers()));

		recommendTaskService.save(entity);

		logger.info("推荐任务生成完成，用户：{}，批次：{}，目标期号：{}", userId, batchNo, targetDrawNum);
		return batchNo;
	}

	/**
	 * 为多个用户批量生成推荐（系统定时任务使用）
	 */
	@Transactional(rollbackFor = Exception.class)
	public void generateRecommendForAllUsers(List<Long> userIds, Integer analyzedPeriods, String strategy) {
		logger.info("开始为 {} 个用户批量生成推荐", userIds.size());

		String targetDrawNum = getNextDrawNum();

		for (Long userId : userIds) {
			try {
				// 检查是否已生成
				if (!recommendTaskService.hasRecommendForDrawNum(userId, targetDrawNum)) {
					generateRecommendTask(userId, "用户" + userId, analyzedPeriods, strategy);
				} else {
					logger.debug("用户 {} 已有推荐，跳过", userId);
				}
			} catch (Exception e) {
				logger.error("为用户 {} 生成推荐失败", userId, e);
			}
		}

		logger.info("批量推荐生成完成");
	}

	/**
	 * 执行4种策略的推荐任务（单用户）
	 */
	public void executeAllRecommendTasks(Long userId, String userName) {
		logger.info("========== 为用户 {} 执行所有推荐任务 ==========", userId);

		String targetDrawNum = getNextDrawNum();

		// 检查是否已生成
		if (recommendTaskService.hasRecommendForDrawNum(userId, targetDrawNum)) {
			logger.warn("用户 {} 已为期号 {} 生成过推荐", userId, targetDrawNum);
			return;
		}

		try {
			// 只生成一次（使用日推荐策略）
			generateRecommendTask(userId, userName, 100, "DAILY");
		} catch (Exception e) {
			logger.error("生成推荐任务失败", e);
			throw e;
		}

		logger.info("========== 推荐任务执行完成 ==========");
	}

	private String generateBatchNo(Long userId, String strategy) {
		return String.format("U%s_%s_%s",
				userId,
				strategy,
				LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
	}

	private String getNextDrawNum() {
		// 获取最新期号
		QueryWrapper<DltLotteryDrawEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("is_deleted", 0)
				.orderByDesc("lottery_draw_num")
				.last("LIMIT 1");

		DltLotteryDrawEntity latest = lotteryDrawService.getOne(wrapper);
		if (latest != null) {
			// 期号+1
			int currentNum = Integer.parseInt(latest.getLotteryDrawNum());
			return String.format("%05d", currentNum + 1);
		}

		return "00001";
	}

	private String joinNumbers(List<Integer> numbers) {
		if (numbers == null || numbers.isEmpty()) {
			return "";
		}
		return numbers.stream()
				.sorted()
				.map(String::valueOf)
				.collect(Collectors.joining(","));
	}
}
