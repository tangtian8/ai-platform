package top.tangtian.balofhope.dlt.analyze.service.vo;
import lombok.Data;

import java.util.List;

/**
 * @author tangtian
 * @date 2026-01-23 09:30
 */
/** 号码频率统计 */
@Data
public class NumberFrequencyStats {
	private String type;                            // 类型：红球/蓝球
	private Integer totalDraws;                     // 总期数
	private List<NumberFrequency> frequencies;      // 所有号码频率列表
	private List<NumberFrequency> hotNumbers;       // 热号列表
	private List<NumberFrequency> coldNumbers;      // 冷号列表
}
