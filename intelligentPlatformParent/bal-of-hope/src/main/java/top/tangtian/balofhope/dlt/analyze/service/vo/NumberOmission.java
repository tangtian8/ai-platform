package top.tangtian.balofhope.dlt.analyze.service.vo;

import lombok.Data;

/**
 * @author tangtian
 * @date 2026-01-23 09:34
 * 号码遗漏
 */
@Data
public class NumberOmission {
	private Integer number;           // 号码
	private Integer currentOmission;  // 当前遗漏值
	private Integer maxOmission;      // 最大遗漏值
}
