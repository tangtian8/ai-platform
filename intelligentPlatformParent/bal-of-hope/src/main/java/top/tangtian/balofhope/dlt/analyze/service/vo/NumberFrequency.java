package top.tangtian.balofhope.dlt.analyze.service.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author tangtian
 * @date 2026-01-23 09:29
 */
@Data
public class NumberFrequency {
	private Integer number;           // 号码
	private Integer count;            // 出现次数
	private BigDecimal percentage;    // 出现百分比
}
