package top.tangtian.balofhope.dlt.analyze.service.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author tangtian
 * 销售额趋势数据
 * @date 2026-01-23 09:31
 */
@Data
public class SalesTrendData {
	private String drawNum;           // 期号
	private String drawTime;          // 开奖时间
	private BigDecimal salesAmount;   // 销售额
}
