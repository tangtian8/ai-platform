package top.tangtian.balofhope.dlt.storage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.tangtian.balofhope.dlt.colletcion.model.resp.PrizeLevel;
import top.tangtian.balofhope.dlt.storage.entity.DltPrizeLevelEntity;
import top.tangtian.balofhope.dlt.storage.mapper.DltPrizeLevelMapper;
import top.tangtian.balofhope.dlt.storage.service.IDltPrizeLevelService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tangtian
 * @date 2026-01-21 16:44
 */
@Service
public class DltPrizeLevelServiceImpl
		extends ServiceImpl<DltPrizeLevelMapper, DltPrizeLevelEntity>
		implements IDltPrizeLevelService {

	@Override
	@Transactional
	public boolean savePrizeLevels(String drawNum, List<PrizeLevel> prizeLevels) {
		try {
			// 先删除该期号的旧数据（如果存在）
			baseMapper.deleteByDrawNum(drawNum);

			// 批量保存新数据
			List<DltPrizeLevelEntity> entities = new ArrayList<>();
			for (PrizeLevel prizeLevel : prizeLevels) {
				DltPrizeLevelEntity entity = new DltPrizeLevelEntity();
				entity.setLotteryDrawNum(drawNum);
				entity.setPrizeLevel(prizeLevel.getPrizeLevel());
				entity.setStakeCount(prizeLevel.getStakeCount());
				entity.setStakeAmount(prizeLevel.getStakeAmount());
				entity.setStakeAmountFormat(prizeLevel.getStakeAmountFormat());
				entity.setTotalPrizeamount(prizeLevel.getTotalPrizeamount());
				entity.setSort(prizeLevel.getSort());
				entity.setAwardType(prizeLevel.getAwardType());
				entity.setGroupCode(prizeLevel.getGroup());
				entities.add(entity);
			}

			return this.saveBatch(entities);

		} catch (Exception e) {
			throw new RuntimeException("保存奖项数据失败: " + e.getMessage(), e);
		}
	}

	@Override
	public List<DltPrizeLevelEntity> getByDrawNum(String drawNum) {
		return baseMapper.findByDrawNum(drawNum);
	}
}