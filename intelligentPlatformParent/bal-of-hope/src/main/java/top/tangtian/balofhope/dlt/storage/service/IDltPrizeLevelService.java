package top.tangtian.balofhope.dlt.storage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.tangtian.balofhope.dlt.colletcion.model.resp.PrizeLevel;
import top.tangtian.balofhope.dlt.storage.entity.DltPrizeLevelEntity;

import java.util.List;

/**
 * @author tangtian
 * @date 2026-01-21 16:43
 */
public interface IDltPrizeLevelService extends IService<DltPrizeLevelEntity> {

	/** 批量保存奖项 */
	boolean savePrizeLevels(String drawNum, List<PrizeLevel> prizeLevels);

	/** 根据期号查询奖项 */
	List<DltPrizeLevelEntity> getByDrawNum(String drawNum);
}
