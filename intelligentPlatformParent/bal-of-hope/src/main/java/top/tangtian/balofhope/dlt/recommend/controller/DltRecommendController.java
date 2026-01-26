package top.tangtian.balofhope.dlt.recommend.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.tangtian.balofhope.dlt.recommend.entity.DltRecommendTaskEntity;
import top.tangtian.balofhope.dlt.recommend.service.DltRecommendTaskGenerator;
import top.tangtian.balofhope.dlt.recommend.service.DltRecommendTaskService;
import top.tangtian.balofhope.dlt.recommend.service.DltRecommendVerifyService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
		* ============================================================
		* 7. REST API 控制器（增加用户维度）
		* ============================================================
 * /**
 *  * @author tangtian
 *  * @date 2026-01-26 18:22
 *  */
@RestController
@RequestMapping("/api/dlt/recommend")
class DltRecommendController {

	@Autowired
	private DltRecommendTaskGenerator taskGenerator;

	@Autowired
	private DltRecommendVerifyService verifyService;

	@Autowired
	private DltRecommendTaskService recommendTaskService;

	/**
	 * 生成推荐任务（需要传入用户ID）
	 */
	@PostMapping("/generate")
	public Map<String, Object> generateRecommend(
			@RequestParam Long userId,
			@RequestParam(required = false) String userName,
			@RequestParam(defaultValue = "100") Integer periods,
			@RequestParam(defaultValue = "DAILY") String strategy) {

		Map<String, Object> result = new HashMap<>();

		try {
			String batchNo = taskGenerator.generateRecommendTask(
					userId,
					userName != null ? userName : "用户" + userId,
					periods,
					strategy
			);

			result.put("success", true);
			result.put("batchNo", batchNo);
			result.put("message", "推荐任务生成成功");
		} catch (RuntimeException e) {
			result.put("success", false);
			result.put("message", e.getMessage());
		}

		return result;
	}

	/**
	 * 验证推荐结果
	 */
	@PostMapping("/verify")
	public Map<String, Object> verifyRecommends() {
		verifyService.verifyAllUnDrawedTasks();

		Map<String, Object> result = new HashMap<>();
		result.put("success", true);
		result.put("message", "推荐验证完成");

		return result;
	}

	/**
	 * 查询用户的推荐任务列表
	 */
	@GetMapping("/user-list")
	public List<DltRecommendTaskEntity> getUserRecommends(
			@RequestParam Long userId,
			@RequestParam(defaultValue = "10") Integer limit) {

		QueryWrapper<DltRecommendTaskEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("user_id", userId)
				.eq("is_deleted", 0)
				.orderByDesc("recommend_time")
				.last("LIMIT " + limit);

		return recommendTaskService.list(wrapper);
	}

	/**
	 * 查询用户的推荐准确率统计
	 */
	@GetMapping("/user-statistics")
	public Map<String, Object> getUserStatistics(@RequestParam Long userId) {
		QueryWrapper<DltRecommendTaskEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("user_id", userId)
				.eq("is_drawed", true)
				.eq("is_deleted", 0);

		List<DltRecommendTaskEntity> tasks = recommendTaskService.list(wrapper);

		Map<String, Object> stats = new HashMap<>();
		stats.put("userId", userId);
		stats.put("totalTasks", tasks.size());

		int prizeTasks = 0;
		Map<String, Integer> prizeLevelCount = new HashMap<>();

		for (DltRecommendTaskEntity task : tasks) {
			String[] levels = {task.getPrizeLevel1(), task.getPrizeLevel2(),
					task.getPrizeLevel3(), task.getPrizeLevel4()};

			for (String level : levels) {
				if (level != null && !level.equals("未中奖")) {
					prizeTasks++;
					prizeLevelCount.put(level, prizeLevelCount.getOrDefault(level, 0) + 1);
				}
			}
		}

		stats.put("prizeTasks", prizeTasks);
		stats.put("prizeRate", tasks.size() > 0 ? (prizeTasks * 100.0 / (tasks.size() * 4)) : 0);
		stats.put("prizeLevelCount", prizeLevelCount);

		return stats;
	}

	/**
	 * 检查用户是否已生成推荐
	 */
	@GetMapping("/check")
	public Map<String, Object> checkUserRecommend(
			@RequestParam Long userId,
			@RequestParam String drawNum) {

		boolean hasRecommend = recommendTaskService.hasRecommendForDrawNum(userId, drawNum);

		Map<String, Object> result = new HashMap<>();
		result.put("userId", userId);
		result.put("drawNum", drawNum);
		result.put("hasRecommend", hasRecommend);

		if (hasRecommend) {
			DltRecommendTaskEntity task = recommendTaskService.getUserRecommendForDrawNum(userId, drawNum);
			result.put("recommendTask", task);
		}

		return result;
	}
}