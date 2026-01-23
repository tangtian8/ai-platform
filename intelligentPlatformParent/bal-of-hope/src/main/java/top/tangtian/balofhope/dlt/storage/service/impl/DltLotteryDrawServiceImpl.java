package top.tangtian.balofhope.dlt.storage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.tangtian.balofhope.dlt.colletcion.model.resp.DltLotteryDraw;
import top.tangtian.balofhope.dlt.colletcion.model.resp.PrizeLevel;
import top.tangtian.balofhope.dlt.storage.entity.DltLotteryDrawEntity;
import top.tangtian.balofhope.dlt.storage.entity.DltPrizeLevelEntity;
import top.tangtian.balofhope.dlt.storage.mapper.DltLotteryDrawMapper;
import top.tangtian.balofhope.dlt.storage.mapper.DltPrizeLevelMapper;
import top.tangtian.balofhope.dlt.storage.service.IDltLotteryDrawService;
import top.tangtian.balofhope.dlt.storage.service.IDltPrizeLevelService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tangtian
 * @date 2026-01-21 16:44
 */
@Service
public class DltLotteryDrawServiceImpl
		extends ServiceImpl<DltLotteryDrawMapper, DltLotteryDrawEntity>
		implements IDltLotteryDrawService {

	@Autowired
	private DltPrizeLevelMapper prizeLevelMapper;

	@Autowired
	private IDltPrizeLevelService prizeLevelService;

	@Override
	public String getLatestDrawNum() {
		return baseMapper.findLatestDrawNum();
	}

	@Override
	public DltLotteryDrawEntity getByDrawNum(String drawNum) {
		return baseMapper.findByDrawNum(drawNum);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean saveDrawWithPrizes(DltLotteryDraw draw) {
		try {
			// 1. 转换并保存主表数据
			DltLotteryDrawEntity entity = convertToEntity(draw);
			boolean saved = this.save(entity);

			if (!saved) {
				return false;
			}

			// 2. 保存奖项数据
			if (draw.getPrizeLevelList() != null && !draw.getPrizeLevelList().isEmpty()) {
				prizeLevelService.savePrizeLevels(draw.getLotteryDrawNum(), draw.getPrizeLevelList());
			}

			return true;

		} catch (Exception e) {
			throw new RuntimeException("保存开奖数据失败: " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int saveDrawWithPrizes(List<DltLotteryDraw> draws) {
		if (draws == null || draws.isEmpty()) {
			return 0;
		}

		int successCount = 0;
		List<DltLotteryDrawEntity> drawEntities = new ArrayList<>();
		Map<String, List<PrizeLevel>> prizeLevelMap = new HashMap<>();

		// 1. 转换所有数据
		for (DltLotteryDraw draw : draws) {
			try {
				// 检查是否已存在
				if (existsByDrawNum(draw.getLotteryDrawNum())) {
					continue;
				}

				// 转换主表实体
				DltLotteryDrawEntity entity = convertToEntity(draw);
				drawEntities.add(entity);

				// 收集奖项数据
				if (draw.getPrizeLevelList() != null && !draw.getPrizeLevelList().isEmpty()) {
					prizeLevelMap.put(draw.getLotteryDrawNum(), draw.getPrizeLevelList());
				}

			} catch (Exception e) {
				throw new RuntimeException("转换数据失败: " + draw.getLotteryDrawNum(), e);
			}
		}

		// 2. 批量保存主表数据
		if (!drawEntities.isEmpty()) {
			boolean saved = this.saveBatch(drawEntities, 100); // 每批100条
			if (!saved) {
				throw new RuntimeException("批量保存主表数据失败");
			}
			successCount = drawEntities.size();
		}

		// 3. 批量保存奖项数据
		if (!prizeLevelMap.isEmpty()) {
			List<DltPrizeLevelEntity> allPrizeLevels = new ArrayList<>();

			for (Map.Entry<String, List<PrizeLevel>> entry : prizeLevelMap.entrySet()) {
				String drawNum = entry.getKey();
				List<PrizeLevel> prizeLevels = entry.getValue();

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
					allPrizeLevels.add(entity);
				}
			}

			if (!allPrizeLevels.isEmpty()) {
				boolean saved = prizeLevelService.saveBatch(allPrizeLevels, 200); // 每批200条
				if (!saved) {
					throw new RuntimeException("批量保存奖项数据失败");
				}
			}
		}

		return successCount;
	}

	@Override
	public boolean existsByDrawNum(String drawNum) {
		return baseMapper.findByDrawNum(drawNum) != null;
	}

	/**
	 * 转换API响应对象为数据库实体
	 */
	private DltLotteryDrawEntity convertToEntity(DltLotteryDraw draw) {
		DltLotteryDrawEntity entity = new DltLotteryDrawEntity();
		entity.setLotteryDrawNum(draw.getLotteryDrawNum());
		entity.setLotteryGameName(draw.getLotteryGameName());
		entity.setLotteryGameNum(draw.getLotteryGameNum());
		entity.setLotteryDrawResult(draw.getLotteryDrawResult());
		entity.setLotteryUnsortDrawresult(draw.getLotteryUnsortDrawresult());
		entity.setLotteryDrawTime(draw.getLotteryDrawTime());
		entity.setLotterySaleBeginTime(draw.getLotterySaleBeginTime());
		entity.setLotterySaleEndtime(draw.getLotterySaleEndtime());
		entity.setLotteryPaidBeginTime(draw.getLotteryPaidBeginTime());
		entity.setLotteryPaidEndTime(draw.getLotteryPaidEndTime());
		entity.setPoolBalanceAfterdraw(draw.getPoolBalanceAfterdraw());
		entity.setDrawFlowFund(draw.getDrawFlowFund());
		entity.setTotalSaleAmount(draw.getTotalSaleAmount());
		entity.setLotteryDrawStatus(draw.getLotteryDrawStatus());
		entity.setLotterySuspendedFlag(draw.getLotterySuspendedFlag());
		entity.setDrawPdfUrl(draw.getDrawPdfUrl());
		return entity;
	}
}
