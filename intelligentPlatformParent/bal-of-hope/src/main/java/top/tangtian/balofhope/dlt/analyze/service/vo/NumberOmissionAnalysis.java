package top.tangtian.balofhope.dlt.analyze.service.vo;

import lombok.Data;

import java.util.List;

/**
 * @author tangtian
 * @date 2026-01-23 09:35
 * 号码遗漏分析
 */
@Data
public class NumberOmissionAnalysis {
	private String type;                        // 类型：红球/蓝球
	private List<NumberOmission> omissions;     // 所有号码遗漏列表
	private List<NumberOmission> topOmissions;  // TOP遗漏号码
}
