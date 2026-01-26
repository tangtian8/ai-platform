package top.tangtian.balofhope.dlt.recommend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.tangtian.balofhope.dlt.recommend.entity.DltRecommendTaskEntity;
import top.tangtian.balofhope.dlt.recommend.mapper.DltRecommendTaskMapper;
import top.tangtian.balofhope.dlt.recommend.service.DltRecommendTaskService;

/**
 * ============================================================
 * 3. Service 接口和实现
 * ============================================================
 */
@Service
public class DltRecommendTaskServiceImpl extends ServiceImpl<DltRecommendTaskMapper, DltRecommendTaskEntity> implements DltRecommendTaskService {

	/**
	 * 检查用户是否已为该期号生成推荐
	 */
	@Override
	public boolean hasRecommendForDrawNum(Long userId, String drawNum) {
		QueryWrapper<DltRecommendTaskEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("user_id", userId)
				.eq("target_draw_num", drawNum)
				.eq("is_deleted", 0);

		return count(wrapper) > 0;
	}

	/**
	 * 获取用户的推荐任务
	 */
	@Override
	public DltRecommendTaskEntity getUserRecommendForDrawNum(Long userId, String drawNum) {
		QueryWrapper<DltRecommendTaskEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("user_id", userId)
				.eq("target_draw_num", drawNum)
				.eq("is_deleted", 0);

		return getOne(wrapper);
	}
}
