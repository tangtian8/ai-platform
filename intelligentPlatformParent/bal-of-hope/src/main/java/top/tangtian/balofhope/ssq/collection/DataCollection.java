package top.tangtian.balofhope.ssq.collection;

import top.tangtian.balofhope.ssq.model.reponse.LotteryResponse;
import top.tangtian.balofhope.ssq.model.request.LotteryRequest;
/**
 * @author tangtian
 * @date 2026-01-21 12:35
 */
public interface DataCollection {
	/**
	 * 分页查询双色球数据
	 * @return LotteryResponse
	 */
	LotteryResponse findALLByPage(LotteryRequest lotteryRequest);
}
