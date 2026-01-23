package top.tangtian.balofhope.dlt.analyze.service.vo;

import lombok.Data;

import java.util.List;

/**
 * @author tangtian
 * @date 2026-01-23 09:33
 * 奖项统计分析
 */
@Data
public class PrizeStatisticsAnalysis {
	private List<PrizeLevelStats> prizeStats;  // 奖项统计列表
	private Integer analyzedDraws;             // 分析期数
}
