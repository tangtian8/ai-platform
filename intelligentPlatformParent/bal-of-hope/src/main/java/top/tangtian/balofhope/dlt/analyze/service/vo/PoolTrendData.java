package top.tangtian.balofhope.dlt.analyze.service.vo;
import lombok.Data;

import java.math.BigDecimal;
/**
 * @author tangtian
 * @date 2026-01-23 09:30
 */
/** 奖池趋势数据 */
@Data
public class PoolTrendData {
	private String drawNum;           // 期号
	private String drawTime;          // 开奖时间
	private BigDecimal poolBalance;   // 奖池余额
}
