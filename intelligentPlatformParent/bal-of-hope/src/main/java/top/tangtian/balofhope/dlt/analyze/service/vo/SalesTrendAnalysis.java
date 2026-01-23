package top.tangtian.balofhope.dlt.analyze.service.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author tangtian
 * @date 2026-01-23 09:32
 * 销售额趋势分析
 */
@Data
public class SalesTrendAnalysis {
	private List<SalesTrendData> trendData;  // 趋势数据列表
	private BigDecimal totalSales;           // 总销售额
	private BigDecimal maxSales;             // 最高销售额
	private BigDecimal minSales;             // 最低销售额
	private BigDecimal avgSales;             // 平均销售额
}
