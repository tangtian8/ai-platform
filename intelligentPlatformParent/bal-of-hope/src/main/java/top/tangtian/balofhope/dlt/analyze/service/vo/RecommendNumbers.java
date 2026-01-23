package top.tangtian.balofhope.dlt.analyze.service.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author tangtian
 * @date 2026-01-23 09:35
 */
@Data
public class RecommendNumbers {
	private List<Integer> hotRedNumbers;        // 热号推荐（红球）
	private List<Integer> omissionRedNumbers;   // 遗漏号推荐（红球）
	private List<Integer> balancedRedNumbers;   // 均衡推荐（红球）
	private List<Integer> recommendBlueNumbers; // 推荐蓝球
	private Date generateTime;                  // 生成时间
}
