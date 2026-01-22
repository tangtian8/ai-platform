package top.tangtian.balofhope.dlt.colletcion.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.tangtian.balofhope.dlt.colletcion.DltCollection;
import top.tangtian.balofhope.dlt.model.CrawlResult;
import top.tangtian.balofhope.dlt.model.resp.DltApiResponse;
import top.tangtian.balofhope.dlt.model.resp.DltLotteryDraw;
import top.tangtian.balofhope.dlt.storage.service.IDltLotteryDrawService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author tangtian
 * https://webapi.sporttery.cn/gateway/lottery/getHistoryPageListV1.qry?
 * gameNo=85&provinceId=0&pageSize=30&isVerify=1&pageNo=1&startTerm=26007&endTerm=26009
 * @date 2026-01-21 13:19
 */
@Service
public class DltCollectionImpl implements DltCollection {
	private static final Logger logger = LoggerFactory.getLogger(DltCollectionImpl.class);
	/** 请求间隔时间（毫秒），避免请求过快 */
	private static final long REQUEST_INTERVAL = 1000;

	/** 每页大小 */
	private static final int PAGE_SIZE = 30;

	/** 游戏编号 - 超级大乐透 */
	private static final String GAME_NO = "85";


	/** 大乐透API地址 */
	private static final String DLT_API_URL = "https://webapi.sporttery.cn/gateway/lottery/getHistoryPageListV1.qry";


	@Autowired
	private IDltLotteryDrawService iDltLotteryDrawService;


	/**
	 * 爬取全部历史数据
	 * 从第1页开始，一直爬取到最后一页
	 */
	public CrawlResult crawlAllHistoryData(String startTerm, String endTerm) {
		logger.info("========== 开始爬取大乐透全部历史数据 ==========");

		CrawlResult result = new CrawlResult();
		result.setStartTime(LocalDateTime.now());

		List<DltLotteryDraw> allDraws = new ArrayList<>();
		int currentPage = 1;
		int totalPages = 1;
		boolean hasMoreData = true;

		try {
			// 第一次请求获取总页数
			DltApiResponse firstResponse = fetchDataByPage(currentPage);

			if (firstResponse == null || !firstResponse.getSuccess()) {
				result.setSuccess(false);
				result.setErrorMessage("首次请求失败");
				logger.error("爬取失败：首次请求失败");
				return result;
			}

			// 获取总页数
			totalPages = firstResponse.getValue().getPages();
			int totalRecords = firstResponse.getValue().getTotal();

			logger.info("总页数: {}, 总记录数: {}", totalPages, totalRecords);
			result.setTotalPages(totalPages);
			result.setTotalRecords(totalRecords);

			// 添加第一页数据
			allDraws.addAll(firstResponse.getValue().getList());
			logger.info("第 1/{} 页爬取成功，获取 {} 条记录",
					totalPages, firstResponse.getValue().getList().size());

			// 爬取剩余页面
			for (currentPage = 2; currentPage <= totalPages && hasMoreData; currentPage++) {
				try {
					// 延迟，避免请求过快
					Thread.sleep(REQUEST_INTERVAL);

					DltApiResponse response = fetchDataByTermRange(currentPage,startTerm,endTerm);

					if (response != null && response.getSuccess()) {
						List<DltLotteryDraw> pageDraws = response.getValue().getList();

						if (pageDraws != null && !pageDraws.isEmpty()) {
							allDraws.addAll(pageDraws);
							logger.info("第 {}/{} 页爬取成功，获取 {} 条记录",
									currentPage, totalPages, pageDraws.size());
						} else {
							hasMoreData = false;
							logger.warn("第 {} 页无数据，停止爬取", currentPage);
						}
					} else {
						logger.error("第 {} 页爬取失败", currentPage);
						result.incrementFailedPages();
					}

				} catch (Exception e) {
					logger.error("爬取第 {} 页时发生异常", currentPage, e);
					result.incrementFailedPages();
				}
			}

			// 保存到数据库
			if (!allDraws.isEmpty()) {
				logger.info("开始保存数据到数据库，共 {} 条记录", allDraws.size());
				int savedCount = saveDrawsToDatabase(allDraws);
				result.setSavedCount(savedCount);
				logger.info("数据保存成功，实际保存 {} 条记录", savedCount);
			}

			result.setSuccess(true);
			result.setCrawledCount(allDraws.size());

		} catch (Exception e) {
			logger.error("爬取全部数据时发生异常", e);
			result.setSuccess(false);
			result.setErrorMessage(e.getMessage());
		} finally {
			result.setEndTime(LocalDateTime.now());
			result.calculateDuration();
			logger.info("========== 全量爬取完成 ==========");
			logger.info("爬取结果: {}", result);
		}

		return result;
	}

	private int saveDrawsToDatabase(List<DltLotteryDraw> allDraws) {
		return iDltLotteryDrawService.saveDrawWithPrizes(allDraws);
	}

	/**
	 * 爬取最新数据（增量更新）
	 * 只爬取数据库中不存在的新开奖数据
	 */
	@Transactional
	@Override
	public CrawlResult crawlLatestData() {
		logger.info("========== 开始爬取最新开奖数据 ==========");

		CrawlResult result = new CrawlResult();
		result.setStartTime(LocalDateTime.now());

		try {
			// 1. 获取数据库中最新的期号
			String latestTermInDb = iDltLotteryDrawService.getLatestDrawNum();
			logger.info("数据库中最新期号: {}", latestTermInDb);

			// 2. 获取第一页数据（最新数据）
			DltApiResponse response = fetchDataByPage(1);

			if (response == null || !response.getSuccess()) {
				result.setSuccess(false);
				result.setErrorMessage("获取最新数据失败");
				logger.error("爬取失败：获取最新数据失败");
				return result;
			}

			List<DltLotteryDraw> newDraws = new ArrayList<>();
			List<DltLotteryDraw> firstPageDraws = response.getValue().getList();

			if (firstPageDraws == null || firstPageDraws.isEmpty()) {
				result.setSuccess(true);
				result.setCrawledCount(0);
				logger.info("没有新的开奖数据");
				return result;
			}

			// 3. 筛选出新数据
			for (DltLotteryDraw draw : firstPageDraws) {
				String drawNum = draw.getLotteryDrawNum();

				// 如果数据库为空，或者期号大于数据库最新期号，则为新数据
				if (latestTermInDb == null || drawNum.compareTo(latestTermInDb) > 0) {
					newDraws.add(draw);
					logger.info("发现新开奖数据: 期号={}, 开奖号码={}",
							drawNum, draw.getLotteryDrawResult());
				} else {
					// 遇到已存在的数据，停止
					logger.info("遇到已存在期号 {}，停止爬取", drawNum);
					break;
				}
			}

			// 4. 保存新数据
			if (!newDraws.isEmpty()) {
				logger.info("发现 {} 条新数据，开始保存", newDraws.size());
				int savedCount = saveDrawsToDatabase(newDraws);
				result.setSavedCount(savedCount);
				result.setCrawledCount(newDraws.size());
				logger.info("新数据保存成功，保存 {} 条", savedCount);
			} else {
				logger.info("没有新的开奖数据需要保存");
				result.setCrawledCount(0);
			}

			result.setSuccess(true);

		} catch (Exception e) {
			logger.error("爬取最新数据时发生异常", e);
			result.setSuccess(false);
			result.setErrorMessage(e.getMessage());
		} finally {
			result.setEndTime(LocalDateTime.now());
			result.calculateDuration();

			logger.info("========== 增量爬取完成 ==========");
			logger.info("爬取结果: {}", result);
		}

		return result;
	}


	/**
	 * 获取单期最新数据
	 */
	@Override
	public DltLotteryDraw getLatestDrawInfo() {
		logger.info("获取最新一期开奖信息");

		try {
			DltApiResponse response = fetchDataByPage(1);

			if (response != null && response.getSuccess()) {
				List<DltLotteryDraw> draws = response.getValue().getList();
				if (draws != null && !draws.isEmpty()) {
					DltLotteryDraw latest = draws.get(0);
					logger.info("最新开奖: 期号={}, 号码={}, 时间={}",
							latest.getLotteryDrawNum(),
							latest.getLotteryDrawResult(),
							latest.getLotteryDrawTime());
					return latest;
				}
			}
		} catch (Exception e) {
			logger.error("获取最新开奖信息失败", e);
		}

		return null;
	}

	/** User-Agent池 */
	private static final List<String> USER_AGENTS = Arrays.asList(
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36",
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36",
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:122.0) Gecko/20100101 Firefox/122.0",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2 Safari/605.1.15",
			"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36"
	);
	/**
	 * 按页码爬取数据
	 */
	private DltApiResponse fetchDataByPage(int pageNo) {
		return fetchDataByTermRange(pageNo,null,null);
	}

	public String buildURL(String baseUrl, Map<String, String> params) {
		if (params == null || params.isEmpty()) {
			return baseUrl;
		}

		StringBuilder urlBuilder = new StringBuilder(baseUrl);
		boolean firstParam = !baseUrl.contains("?");

		for (Map.Entry<String, String> entry : params.entrySet()) {
			if (firstParam) {
				urlBuilder.append("?");
				firstParam = false;
			} else {
				urlBuilder.append("&");
			}

			String encodedKey = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8);
			String encodedValue = URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8);
			urlBuilder.append(encodedKey).append("=").append(encodedValue);
		}

		return urlBuilder.toString();
	}

	/**
	 * 随机获取User-Agent
	 */
	private String getRandomUserAgent() {
		int index = ThreadLocalRandom.current().nextInt(USER_AGENTS.size());
		return USER_AGENTS.get(index);
	}

	/**
	 * 按期号范围爬取数据
	 */
	private DltApiResponse fetchDataByTermRange(Integer pageNo,String startTerm, String endTerm) {
		int maxRetries = 3;
		int attempt = 0;

		while (attempt < maxRetries) {
			attempt++;

			try {
				// 第一次快速尝试，失败后逐渐增加延迟
				if (attempt > 1) {
					long delay = getRandomDelay(
							1000 * attempt,
							3000 * attempt
					);
					logger.info("智能延迟 {} 毫秒", delay);
					Thread.sleep(delay);
				}

				Map<String,String> mapParam = new HashMap<>();
				mapParam.put("gameNo",GAME_NO);
				mapParam.put("provinceId","0");
				mapParam.put("pageSize",String.valueOf(PAGE_SIZE));
				mapParam.put("isVerify","1");
				if (null != pageNo){
					mapParam.put("pageNo",String.valueOf(pageNo));
				}
				if (null != startTerm){
					mapParam.put("startTerm",startTerm);
				}
				if (null != endTerm){
					mapParam.put("endTerm",endTerm);
				}
				String url = buildURL(DLT_API_URL,mapParam);
				logger.info("请求url:{}",url);
				HttpResponse response = HttpRequest.get(url)
						.header("Accept", "application/json, text/javascript, */*; q=0.01")
						.header("Accept-Encoding", "gzip, deflate, br, zstd")
						.header("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
						.header("Origin", "https://static.sporttery.cn")  // 关键！不是lottery.gov.cn
						.header("Referer", "https://static.sporttery.cn/") // 关键！必须带斜杠
						.header("Sec-Ch-Ua", "\"Google Chrome\";v=\"143\", \"Chromium\";v=\"143\", \"Not A(Brand)\";v=\"24\"")
						.header("Sec-Ch-Ua-Mobile", "?0")
						.header("Sec-Ch-Ua-Platform", "\"macOS\"") // 或 "\"Windows\""
						.header("Sec-Fetch-Dest", "empty")
						.header("Sec-Fetch-Mode", "cors")
						.header("Sec-Fetch-Site", "same-site") // 关键！不是cross-site
						.header("User-Agent", getRandomUserAgent())
						.header("Priority", "u=1, i")
						// 随机超时时间
						.timeout(20000 + ThreadLocalRandom.current().nextInt(10000))
						.execute();

				if (response.isOk()) {
					String body = response.body();

					// 检查是否被拦截
					if (body.contains("安全策略") || body.contains("拦截")) {
						logger.warn("检测到安全拦截，准备重试");
						continue;
					}

					logger.info("智能爬取成功，尝试次数: {}", attempt);
					return JSONUtil.toBean(body, DltApiResponse.class);
				}

			} catch (Exception e) {
				logger.error("智能爬取异常，尝试次数: {}", attempt, e);
			}
		}
		return null;
	}

	/**
	 * 获取随机延迟时间（毫秒）
	 */
	private long getRandomDelay(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max);
	}
}
