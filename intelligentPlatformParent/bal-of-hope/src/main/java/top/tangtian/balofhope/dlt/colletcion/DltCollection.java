package top.tangtian.balofhope.dlt.colletcion;

import top.tangtian.balofhope.dlt.colletcion.model.CrawlResult;
import top.tangtian.balofhope.dlt.colletcion.model.resp.DltLotteryDraw;

/**
 * @author tangtian
 * @date 2026-01-21 13:15
 */
public interface DltCollection {

	CrawlResult crawlAllHistoryData(String startTerm, String endTerm);

	CrawlResult crawlLatestData();

	DltLotteryDraw getLatestDrawInfo();
}
