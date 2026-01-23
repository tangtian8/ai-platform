package top.tangtian.balofhope.dlt.analyze.service.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author tangtian
 * 分析报告摘要实体
 * @date 2026-01-23 09:47
 */
@Data
public class AnalysisReportSummary {
	private String batchNo;
	private String reportType;
	private Integer analyzedPeriods;
	private BigDecimal currentPool;
	private BigDecimal avgSales;
	private Date generateTime;
}
