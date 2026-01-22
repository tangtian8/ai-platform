package top.tangtian.balofhope.ssq.storage;

import top.tangtian.balofhope.ssq.model.reponse.LotteryResult;

import java.util.List;

/**
 * @author tangtian
 * @date 2026-01-21 12:58
 */
public interface StorageLottery {
	/**
	 * shuan
	 * @param lotteryResults
	 */
	void storage(List<LotteryResult> lotteryResults);
}
