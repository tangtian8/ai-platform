package top.tangtian.esanalysis.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import top.tangtian.esanalysis.entity.FeedbackEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tangtian
 * @date 2025-12-01 09:53
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataCollectorService {

	private final RestTemplate restTemplate;
	private final ElasticsearchService esService;
	private final ObjectMapper objectMapper;
	private final CollectionProgressMonitor progressMonitor;

	private static final String API_URL = "https://wz-api.chuanbaoguancha.cn/api/v1/thread/page";

	/**
	 * 采集单页数据
	 */
	public List<FeedbackEntity> fetchPageData(int page, int size, Integer organizationId) {
		try {
			// 构建请求体
			Map<String, Object> requestBody = new HashMap<>();
			requestBody.put("sort_id", null);
			requestBody.put("field_id", null);
			requestBody.put("reply_status", "");
			requestBody.put("assign_organization_id", organizationId);
			requestBody.put("page", page);
			requestBody.put("size", size);
			requestBody.put("need_total", true);

			// 设置请求头
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Accept", "*/*");
			headers.set("Host", "wz-api.chuanbaoguancha.cn");
			headers.set("Connection", "keep-alive");

			HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

			// 发送请求
			ResponseEntity<String> response = restTemplate.exchange(
					API_URL,
					HttpMethod.POST,
					request,
					String.class
			);

			// 解析响应
			if (response.getStatusCode() == HttpStatus.OK) {
				return parseResponse(response.getBody());
			} else {
				log.error("API请求失败，状态码: {}", response.getStatusCode());
				return new ArrayList<>();
			}

		} catch (Exception e) {
			log.error("采集数据失败，页码: {}", page, e);
			return new ArrayList<>();
		}
	}

	/**
	 * 解析API响应
	 */
	private List<FeedbackEntity> parseResponse(String responseBody) {
		try {
			JsonNode root = objectMapper.readTree(responseBody);

			// 检查响应码
			int code = root.path("code").asInt(-1);
			if (code != 0) {
				log.error("API返回错误码: {}", code);
				return new ArrayList<>();
			}

			// 解析嵌套的data结构: data.data
			JsonNode records = root.path("data").path("data");

			if (!records.isArray()) {
				log.warn("响应中没有找到数据数组");
				return new ArrayList<>();
			}

			List<FeedbackEntity> entities = new ArrayList<>();
			for (JsonNode node : records) {
				try {
					FeedbackEntity entity = objectMapper.treeToValue(node, FeedbackEntity.class);
					entities.add(entity);
				} catch (Exception e) {
					log.warn("解析单条记录失败，跳过: {}", e.getMessage());
					// 继续处理其他记录
				}
			}

			log.info("成功解析 {} 条数据", entities.size());
			return entities;

		} catch (Exception e) {
			log.error("解析响应数据失败", e);
			return new ArrayList<>();
		}
	}

	/**
	 * 从响应中获取总数和总页数
	 */
	private Map<String, Integer> parsePageInfo(String responseBody) {
		try {
			JsonNode root = objectMapper.readTree(responseBody);
			JsonNode data = root.path("data");

			int total = data.path("total").asInt(0);
			int totalPage = data.path("total_page").asInt(0);

			return Map.of(
					"total", total,
					"totalPage", totalPage
			);
		} catch (Exception e) {
			log.error("解析分页信息失败", e);
			return Map.of("total", 0, "totalPage", 0);
		}
	}

	/**
	 * 全量采集数据（优化版 - 支持大数据量）
	 */
	public void collectAllData(Integer organizationId, int batchSize) {
		int currentPage = 1;
		int totalCollected = 0;
		int totalRecords = 0;
		int totalPages = 0;

		log.info("开始采集数据，组织ID: {}", organizationId);

		// 首次请求获取总数信息
		try {
			String firstPageResponse = fetchPageRawResponse(1, batchSize, organizationId);
			Map<String, Integer> pageInfo = parsePageInfo(firstPageResponse);
			totalRecords = pageInfo.get("total");
			totalPages = pageInfo.get("totalPage");

			log.info("数据总量: {} 条，总页数: {} 页，每页: {} 条",
					totalRecords, totalPages, batchSize);

			// 解析第一页数据
			List<FeedbackEntity> firstPageData = parseResponse(firstPageResponse);
			if (!firstPageData.isEmpty()) {
				esService.bulkInsert(firstPageData);
				totalCollected += firstPageData.size();
				log.info("第 1 页插入成功，本页 {} 条，进度: {}/{} ({:.1f}%)",
						firstPageData.size(), totalCollected, totalRecords,
						(double) totalCollected / totalRecords * 100);
			}

			currentPage = 2;

		} catch (Exception e) {
			log.error("获取数据信息失败", e);
			return;
		}

		// 采集剩余页面
		while (currentPage <= totalPages) {
			try {
				log.info("正在采集第 {}/{} 页...", currentPage, totalPages);

				List<FeedbackEntity> pageData = fetchPageData(currentPage, batchSize, organizationId);

				if (pageData.isEmpty()) {
					log.warn("第 {} 页没有数据，跳过", currentPage);
				} else {
					boolean success = esService.bulkInsert(pageData);

					if (success) {
						totalCollected += pageData.size();
						double progress = (double) totalCollected / totalRecords * 100;
						log.info("第 {} 页插入成功，本页 {} 条，累计 {} 条，进度: {:.1f}%",
								currentPage, pageData.size(), totalCollected, progress);
					} else {
						log.error("第 {} 页插入失败，尝试继续", currentPage);
					}
				}

				currentPage++;

				// 添加延迟，避免请求过快（每10页增加延迟）
				if (currentPage % 10 == 0) {
					Thread.sleep(1000); // 每10页休息1秒
				} else {
					Thread.sleep(300); // 正常页面间隔300ms
				}

			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				log.warn("采集被中断，已采集 {} 条数据", totalCollected);
				break;
			} catch (Exception e) {
				log.error("第 {} 页采集失败: {}", currentPage, e.getMessage());
				currentPage++;
				// 失败后继续采集下一页
			}
		}

		log.info("数据采集完成！计划采集: {} 条，实际采集: {} 条，成功率: {:.1f}%",
				totalRecords, totalCollected,
				(double) totalCollected / totalRecords * 100);
	}

	/**
	 * 获取原始响应字符串（用于解析分页信息）
	 */
	private String fetchPageRawResponse(int page, int size, Integer organizationId) throws Exception {
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("sort_id", null);
		requestBody.put("field_id", null);
		requestBody.put("reply_status", "");
		requestBody.put("assign_organization_id", organizationId);
		requestBody.put("page", page);
		requestBody.put("size", size);
		requestBody.put("need_total", true);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept", "*/*");
		headers.set("Host", "wz-api.chuanbaoguancha.cn");
		headers.set("Connection", "keep-alive");

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

		ResponseEntity<String> response = restTemplate.exchange(
				API_URL,
				HttpMethod.POST,
				request,
				String.class
		);

		if (response.getStatusCode() != HttpStatus.OK) {
			throw new RuntimeException("API请求失败: " + response.getStatusCode());
		}

		return response.getBody();
	}

	/**
	 * 增量采集 - 只采集指定时间之后的数据
	 */
	public void collectIncrementalData(Integer organizationId, String lastCollectTime) {
		// TODO: 根据API是否支持时间筛选参数来实现
		// 如果API支持，可以在请求体中添加时间过滤条件
		log.info("增量采集功能待实现，需要API支持时间筛选");
	}

	/**
	 * 按组织批量采集
	 */
	public void collectByOrganizations(List<Integer> organizationIds, int batchSize) {
		for (Integer orgId : organizationIds) {
			log.info("开始采集组织 {} 的数据", orgId);
			collectAllData(orgId, batchSize);

			// 组织之间添加延迟
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
	}
}
