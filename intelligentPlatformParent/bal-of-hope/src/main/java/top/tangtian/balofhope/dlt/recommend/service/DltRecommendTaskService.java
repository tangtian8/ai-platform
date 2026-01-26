package top.tangtian.balofhope.dlt.recommend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.tangtian.balofhope.dlt.recommend.entity.DltRecommendTaskEntity;

/**
 * @author tangtian
 * @date 2026-01-26 18:17
 */
public interface DltRecommendTaskService extends IService<DltRecommendTaskEntity> {
	boolean hasRecommendForDrawNum(Long userId, String drawNum);
	DltRecommendTaskEntity getUserRecommendForDrawNum(Long userId, String drawNum);

}