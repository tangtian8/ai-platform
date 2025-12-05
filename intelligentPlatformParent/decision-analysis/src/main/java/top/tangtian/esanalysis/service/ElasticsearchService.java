package top.tangtian.esanalysis.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.json.JsonData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.tangtian.esanalysis.entity.FeedbackEntity;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

/**
 * @author tangtian
 * @date 2025-12-01 09:42
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticsearchService {

	private final ElasticsearchClient esClient;
	private static final String INDEX_NAME = "feedback_data";

	/**
	 * 批量插入数据到ES
	 */
	public boolean bulkInsert(List<FeedbackEntity> feedbacks) {
		try {
			BulkRequest.Builder br = new BulkRequest.Builder();

			for (FeedbackEntity feedback : feedbacks) {
				br.operations(op -> op
						.index(idx -> idx
								.index(INDEX_NAME)
								.id(String.valueOf(feedback.getId()))
								.document(feedback)
						)
				);
			}

			BulkResponse result = esClient.bulk(br.build());

			if (result.errors()) {
				log.error("批量插入存在错误");
				for (BulkResponseItem item : result.items()) {
					if (item.error() != null) {
						log.error("错误: {}", item.error().reason());
					}
				}
				return false;
			}

			log.info("成功插入 {} 条数据", feedbacks.size());
			return true;

		} catch (IOException e) {
			log.error("批量插入失败", e);
			return false;
		}
	}

	/**
	 * 按日期范围统计分析
	 */
	public Map<String, Object> analyzeDailyData(LocalDate startDate, LocalDate endDate) {
		try {
			SearchResponse<FeedbackEntity> response = esClient.search(s -> s
							.index(INDEX_NAME)
							.size(0)
							.query(q -> q
									.range(r -> r
											.field("created_at")
											.gte(JsonData.of(startDate.toString()))
											.lte(JsonData.of(endDate.toString()))
									)
							)
							.aggregations("by_status", a -> a
									.terms(t -> t
											.field("handle_status")
											.size(50)
									)
							)
							.aggregations("by_organization", a -> a
									.terms(t -> t
											.field("organization_name")
											.size(50)
									)
							)
							.aggregations("by_source", a -> a
									.terms(t -> t
											.field("source")
											.size(20)
									)
							)
							// 标题关键词分析（使用 significant_text）
							.aggregations("title_keywords", a -> a
									.significantText(st -> st
											.field("title")
											.size(20)
											.minDocCount(2L)
									)
							)
							// 内容关键词分析（使用 significant_text）
							.aggregations("content_keywords", a -> a
									.significantText(st -> st
											.field("content")
											.size(30)
											.minDocCount(3L)
									)
							)
							.aggregations("daily_trend", a -> a
									.dateHistogram(dh -> dh
											.field("created_at")
											.calendarInterval(CalendarInterval.Day)
									)
							)
							.aggregations("avg_satisfaction", a -> a
									.avg(avg -> avg.field("satisfaction"))
							)
							.aggregations("reply_rate", a -> a
									.filter(f -> f
											.exists(e -> e.field("reply_at"))
									)
							),
					FeedbackEntity.class
			);

			Map<String, Object> result = new HashMap<>();
			result.put("total", response.hits().total().value());
			result.put("statusDistribution", parseTermsAggregation(response.aggregations().get("by_status")));
			result.put("organizationDistribution", parseTermsAggregation(response.aggregations().get("by_organization")));
			result.put("sourceDistribution", parseTermsAggregation(response.aggregations().get("by_source")));

			// 解析标题关键词
			result.put("titleKeywords", parseSignificantTextAggregation(response.aggregations().get("title_keywords")));

			// 解析内容关键词
			result.put("contentKeywords", parseSignificantTextAggregation(response.aggregations().get("content_keywords")));

			result.put("dailyTrend", parseDateHistogram(response.aggregations().get("daily_trend")));

			Aggregate avgSat = response.aggregations().get("avg_satisfaction");
			result.put("avgSatisfaction", avgSat.avg().value());

			Aggregate replyRate = response.aggregations().get("reply_rate");
			long repliedCount = replyRate.filter().docCount();
			result.put("replyCount", repliedCount);
			result.put("replyRate", String.format("%.2f%%",
					(double) repliedCount / response.hits().total().value() * 100));

			return result;

		} catch (IOException e) {
			log.error("数据分析失败", e);
			return Collections.emptyMap();
		}
	}

	/**
	 * 获取热点问题分析
	 */
	public Map<String, Object> getHotTopics(int days) {
		try {
			LocalDate endDate = LocalDate.now();
			LocalDate startDate = endDate.minusDays(days);

			SearchResponse<FeedbackEntity> response = esClient.search(s -> s
							.index(INDEX_NAME)
							.size(0)
							.query(q -> q
									.range(r -> r
											.field("created_at")
											.gte(JsonData.of(startDate.toString()))
									)
							)
							.aggregations("hot_keywords", a -> a
									.significantText(st -> st
											.field("content")
											.size(20)
									)
							)
							.aggregations("top_tags", a -> a
									.terms(t -> t
											.field("tags")
											.size(20)
									)
							),
					FeedbackEntity.class
			);

			Map<String, Object> result = new HashMap<>();
			result.put("keywords", response.aggregations().get("hot_keywords"));
			result.put("tags", parseTermsAggregation(response.aggregations().get("top_tags")));

			return result;

		} catch (IOException e) {
			log.error("热点分析失败", e);
			return Collections.emptyMap();
		}
	}

	private Map<String, Long> parseTermsAggregation(Aggregate agg) {
		Map<String, Long> result = new LinkedHashMap<>();
		if (agg != null && agg.isSterms()) {
			for (StringTermsBucket bucket : agg.sterms().buckets().array()) {
				result.put(bucket.key().stringValue(), bucket.docCount());
			}
		}
		return result;
	}

	private List<Map<String, Object>> parseDateHistogram(Aggregate agg) {
		List<Map<String, Object>> result = new ArrayList<>();
		if (agg != null && agg.isDateHistogram()) {
			for (DateHistogramBucket bucket : agg.dateHistogram().buckets().array()) {
				Map<String, Object> item = new HashMap<>();
				item.put("date", bucket.keyAsString());
				item.put("count", bucket.docCount());
				result.add(item);
			}
		}
		return result;
	}

	/**
	 * 解析 SignificantText 聚合结果（关键词分析）
	 */
	private Map<String, Long> parseSignificantTextAggregation(Aggregate agg) {
		Map<String, Long> result = new LinkedHashMap<>();
		if (agg != null) {
			for (SignificantStringTermsBucket bucket : agg.sigsterms().buckets().array()) {
				result.put(bucket.key().toString(), bucket.docCount());
			}
		}
		return result;
	}
}
