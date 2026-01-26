package top.tangtian.balofhope.dlt.recommend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.tangtian.balofhope.dlt.recommend.entity.DltRecommendTaskEntity;
import top.tangtian.balofhope.dlt.storage.entity.DltLotteryDrawEntity;
import top.tangtian.balofhope.dlt.storage.service.IDltLotteryDrawService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ============================================================
 * 5. 开奖后验证服务（按用户验证）
 * ============================================================
 */
@Service
public class DltRecommendVerifyService {

	private static final Logger logger = LoggerFactory.getLogger(DltRecommendVerifyService.class);

	@Autowired
	private DltRecommendTaskService recommendTaskService;

	@Autowired
	private IDltLotteryDrawService lotteryDrawService;

	/**
	 * 验证所有未开奖的推荐任务
	 */
	@Transactional(rollbackFor = Exception.class)
	public void verifyAllUnDrawedTasks() {
		logger.info("========== 开始验证未开奖的推荐任务 ==========");

		QueryWrapper<DltRecommendTaskEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("is_drawed", false)
				.eq("is_deleted", 0)
				.orderByDesc("recommend_time");

		List<DltRecommendTaskEntity> tasks = recommendTaskService.list(wrapper);

		logger.info("找到 {} 个待验证任务", tasks.size());

		for (DltRecommendTaskEntity task : tasks) {
			try {
				verifyTask(task);
			} catch (Exception e) {
				logger.error("验证任务失败，用户：{}，批次：{}", task.getUserId(), task.getRecommendBatch(), e);
			}
		}

		logger.info("========== 推荐任务验证完成 ==========");
	}

	/**
	 * 验证指定用户的推荐任务
	 */
	@Transactional(rollbackFor = Exception.class)
	public void verifyUserTasks(Long userId) {
		logger.info("验证用户 {} 的推荐任务", userId);

		QueryWrapper<DltRecommendTaskEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("user_id", userId)
				.eq("is_drawed", false)
				.eq("is_deleted", 0);

		List<DltRecommendTaskEntity> tasks = recommendTaskService.list(wrapper);

		for (DltRecommendTaskEntity task : tasks) {
			verifyTask(task);
		}
	}

	/**
	 * 验证单个推荐任务
	 */
	@Transactional(rollbackFor = Exception.class)
	public void verifyTask(DltRecommendTaskEntity task) {
		logger.info("验证推荐任务，用户：{}，批次：{}，目标期号：{}",
				task.getUserId(), task.getRecommendBatch(), task.getTargetDrawNum());

		// 查询实际开奖结果
		QueryWrapper<DltLotteryDrawEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("lottery_draw_num", task.getTargetDrawNum())
				.eq("is_deleted", 0);

		DltLotteryDrawEntity draw = lotteryDrawService.getOne(wrapper);

		if (draw == null) {
			logger.warn("期号 {} 尚未开奖", task.getTargetDrawNum());
			return;
		}

		// 解析实际开奖号码
		String drawResult = draw.getLotteryDrawResult();
		String[] balls = drawResult.split(" ");

		Set<Integer> actualRedBalls = new HashSet<>();
		Set<Integer> actualBlueBalls = new HashSet<>();

		// 前5个是红球
		for (int i = 0; i < 5 && i < balls.length; i++) {
			actualRedBalls.add(Integer.parseInt(balls[i]));
		}

		// 后2个是蓝球
		for (int i = 5; i < balls.length; i++) {
			actualBlueBalls.add(Integer.parseInt(balls[i]));
		}

		// 验证4组推荐
		verifyRecommend(task, 1, task.getRedBalls1(), task.getBlueBalls1(), actualRedBalls, actualBlueBalls);
		verifyRecommend(task, 2, task.getRedBalls2(), task.getBlueBalls2(), actualRedBalls, actualBlueBalls);
		verifyRecommend(task, 3, task.getRedBalls3(), task.getBlueBalls3(), actualRedBalls, actualBlueBalls);
		verifyRecommend(task, 4, task.getRedBalls4(), task.getBlueBalls4(), actualRedBalls, actualBlueBalls);

		// 找出最佳推荐
		int bestNo = findBestRecommend(task);

		// 更新任务状态
		task.setIsDrawed(true);
		task.setActualDrawNum(draw.getLotteryDrawNum());
		task.setActualDrawResult(drawResult);
		task.setBestRecommendNo(bestNo);
		task.setVerifyTime(LocalDateTime.now());

		recommendTaskService.updateById(task);

		logger.info("验证完成，用户：{}，最佳推荐：第{}组", task.getUserId(), bestNo);
	}

	/**
	 * 验证单组推荐
	 */
	private void verifyRecommend(DltRecommendTaskEntity task, int recommendNo,
								 String redBalls, String blueBalls,
								 Set<Integer> actualRed, Set<Integer> actualBlue) {

		Set<Integer> recommendRed = parseNumbers(redBalls);
		Set<Integer> recommendBlue = parseNumbers(blueBalls);

		// 计算命中数
		int hitRedCount = (int) recommendRed.stream().filter(actualRed::contains).count();
		int hitBlueCount = (int) recommendBlue.stream().filter(actualBlue::contains).count();

		// 判断中奖等级
		String prizeLevel = determinePrizeLevel(hitRedCount, hitBlueCount);

		// 更新到对应字段
		switch (recommendNo) {
			case 1:
				task.setHitRedCount1(hitRedCount);
				task.setHitBlueCount1(hitBlueCount);
				task.setPrizeLevel1(prizeLevel);
				break;
			case 2:
				task.setHitRedCount2(hitRedCount);
				task.setHitBlueCount2(hitBlueCount);
				task.setPrizeLevel2(prizeLevel);
				break;
			case 3:
				task.setHitRedCount3(hitRedCount);
				task.setHitBlueCount3(hitBlueCount);
				task.setPrizeLevel3(prizeLevel);
				break;
			case 4:
				task.setHitRedCount4(hitRedCount);
				task.setHitBlueCount4(hitBlueCount);
				task.setPrizeLevel4(prizeLevel);
				break;
		}

		logger.debug("用户 {} 推荐{}：命中红球{}个，蓝球{}个，等级：{}",
				task.getUserId(), recommendNo, hitRedCount, hitBlueCount, prizeLevel);
	}

	/**
	 * 判断中奖等级
	 */
	private String determinePrizeLevel(int hitRed, int hitBlue) {
		if (hitRed == 5 && hitBlue == 2) return "一等奖";
		if (hitRed == 5 && hitBlue == 1) return "二等奖";
		if (hitRed == 5 && hitBlue == 0) return "三等奖";
		if (hitRed == 4 && hitBlue == 2) return "四等奖";
		if (hitRed == 4 && hitBlue == 1) return "五等奖";
		if ((hitRed == 3 && hitBlue == 2) || (hitRed == 4 && hitBlue == 0)) return "六等奖";
		if ((hitRed == 2 && hitBlue == 2) || (hitRed == 3 && hitBlue == 1)) return "七等奖";
		if ((hitRed == 1 && hitBlue == 2) || (hitRed == 2 && hitBlue == 1) || (hitRed == 3 && hitBlue == 0)) return "八等奖";
		if ((hitRed == 0 && hitBlue == 2) || (hitRed == 1 && hitBlue == 1)) return "九等奖";
		return "未中奖";
	}

	/**
	 * 找出最佳推荐
	 */
	private int findBestRecommend(DltRecommendTaskEntity task) {
		int[] scores = new int[4];

		scores[0] = calculateScore(task.getHitRedCount1(), task.getHitBlueCount1());
		scores[1] = calculateScore(task.getHitRedCount2(), task.getHitBlueCount2());
		scores[2] = calculateScore(task.getHitRedCount3(), task.getHitBlueCount3());
		scores[3] = calculateScore(task.getHitRedCount4(), task.getHitBlueCount4());

		int maxScore = 0;
		int bestNo = 1;

		for (int i = 0; i < 4; i++) {
			if (scores[i] > maxScore) {
				maxScore = scores[i];
				bestNo = i + 1;
			}
		}

		return bestNo;
	}

	/**
	 * 计算推荐分数
	 */
	private int calculateScore(Integer hitRed, Integer hitBlue) {
		if (hitRed == null) hitRed = 0;
		if (hitBlue == null) hitBlue = 0;
		return hitRed * 10 + hitBlue * 5;
	}

	private Set<Integer> parseNumbers(String numbersStr) {
		if (numbersStr == null || numbersStr.isEmpty()) {
			return new HashSet<>();
		}

		return Arrays.stream(numbersStr.split(","))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.map(Integer::parseInt)
				.collect(Collectors.toSet());
	}
}
