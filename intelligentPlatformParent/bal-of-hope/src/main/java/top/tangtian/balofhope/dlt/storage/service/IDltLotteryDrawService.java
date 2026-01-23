package top.tangtian.balofhope.dlt.storage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.tangtian.balofhope.dlt.colletcion.model.resp.DltLotteryDraw;
import top.tangtian.balofhope.dlt.storage.entity.DltLotteryDrawEntity;

import java.util.List;

/**
 * @author tangtian
 * @date 2026-01-21 16:43
 */
public interface IDltLotteryDrawService extends IService<DltLotteryDrawEntity> {

	/** 获取最新期号 */
	String getLatestDrawNum();

	/** 根据期号查询 */
	DltLotteryDrawEntity getByDrawNum(String drawNum);

	/** 保存完整的开奖数据（包括奖项） */
	boolean saveDrawWithPrizes(DltLotteryDraw draw);

	/** 批量保存完整的开奖数据（包括奖项） */
	int saveDrawWithPrizes(List<DltLotteryDraw> draws);

	/** 检查期号是否存在 */
	boolean existsByDrawNum(String drawNum);
}
