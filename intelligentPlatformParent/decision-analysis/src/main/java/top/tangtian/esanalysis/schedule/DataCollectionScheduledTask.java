package top.tangtian.esanalysis.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.tangtian.esanalysis.service.DataCollectorService;

import java.util.List;

/**
 * @author tangtian
 * @date 2025-12-01 09:55
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataCollectionScheduledTask{

	private final DataCollectorService collectorService;

	// 从配置文件读取需要采集的组织ID列表
	@Value("${collector.organization-ids:4498}")
	private String organizationIdsStr;

	@Value("${collector.batch-size:100}")
	private int batchSize;

	@Value("${collector.enabled:true}")
	private boolean collectorEnabled;

	/**
	 * 每小时采集一次数据
	 * 可根据需要调整cron表达式
	 */
//	@Scheduled(cron = "${collector.cron:0 0 * * * ?}")
	public void scheduledCollection() {
		if (!collectorEnabled) {
			log.debug("定时采集任务已禁用");
			return;
		}

		try {
			log.info("开始执行定时数据采集任务");

			List<Integer> orgIds = parseOrganizationIds(organizationIdsStr);

			if (orgIds.isEmpty()) {
				log.warn("未配置组织ID，跳过采集");
				return;
			}

			collectorService.collectByOrganizations(orgIds, batchSize);

			log.info("定时采集任务执行完成");

		} catch (Exception e) {
			log.error("定时采集任务执行失败", e);
		}
	}

	/**
	 * 每天凌晨2点执行全量采集
	 */
//	@Scheduled(cron = "${collector.full-sync-cron:0 0 2 * * ?}")
	public void fullSyncCollection() {
		if (!collectorEnabled) {
			return;
		}

		try {
			log.info("开始执行全量数据同步");

			List<Integer> orgIds = parseOrganizationIds(organizationIdsStr);
			collectorService.collectByOrganizations(orgIds, batchSize);

			log.info("全量数据同步完成");

		} catch (Exception e) {
			log.error("全量数据同步失败", e);
		}
	}

	private List<Integer> parseOrganizationIds(String idsStr) {
		try {
			return List.of(idsStr.split(","))
					.stream()
					.map(String::trim)
					.map(Integer::parseInt)
					.toList();
		} catch (Exception e) {
			log.error("解析组织ID失败: {}", idsStr, e);
			return List.of();
		}
	}
}
