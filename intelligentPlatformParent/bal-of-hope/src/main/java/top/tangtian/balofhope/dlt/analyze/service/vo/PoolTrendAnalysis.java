package top.tangtian.balofhope.dlt.analyze.service.vo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
/**
 * @author tangtian
 * @date 2026-01-23 09:31
 * /** 奖池趋势分析 */


@Data
public class PoolTrendAnalysis {
	private List<PoolTrendData> trendData;  // 趋势数据列表
	private BigDecimal maxPool;             // 最高奖池
	private BigDecimal minPool;             // 最低奖池
	private BigDecimal avgPool;             // 平均奖池
	private BigDecimal currentPool;         // 当前奖池
}
