package top.tangtian.balofhope.dlt.analyze.service.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author tangtian
 * @date 2026-01-23 09:33
 * 奖项等级统计
 */
@Data
public class PrizeLevelStats {
	private String prizeLevel;        // 奖项等级
	private Integer totalCount;       // 总中奖次数
	private BigDecimal totalAmount;   // 总奖金
}
