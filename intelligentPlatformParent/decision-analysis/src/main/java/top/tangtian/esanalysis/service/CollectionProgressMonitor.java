package top.tangtian.esanalysis.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tangtian
 * @date 2025-12-01 09:54
 */
@Slf4j
@Component
public class CollectionProgressMonitor {

	private final Map<String, CollectionProgress> progressMap = new ConcurrentHashMap<>();

	/**
	 * 开始一个新的采集任务
	 */
	public void startCollection(String taskId, int totalRecords, int totalPages) {
		CollectionProgress progress = new CollectionProgress();
		progress.setTaskId(taskId);
		progress.setTotalRecords(totalRecords);
		progress.setTotalPages(totalPages);
		progress.setStartTime(LocalDateTime.now());
		progress.setStatus("RUNNING");

		progressMap.put(taskId, progress);
		log.info("任务 {} 开始：总记录数 {}, 总页数 {}", taskId, totalRecords, totalPages);
	}

	/**
	 * 更新采集进度
	 */
	public void updateProgress(String taskId, int currentPage, int recordsCollected) {
		CollectionProgress progress = progressMap.get(taskId);
		if (progress != null) {
			progress.setCurrentPage(currentPage);
			progress.setRecordsCollected(recordsCollected);
			progress.setLastUpdateTime(LocalDateTime.now());

			// 计算速度和预计剩余时间
			Duration elapsed = Duration.between(progress.getStartTime(), LocalDateTime.now());
			long elapsedSeconds = elapsed.getSeconds();

			if (elapsedSeconds > 0) {
				double recordsPerSecond = (double) recordsCollected / elapsedSeconds;
				progress.setCollectionSpeed(recordsPerSecond);

				int remaining = progress.getTotalRecords() - recordsCollected;
				if (recordsPerSecond > 0) {
					long estimatedSeconds = (long) (remaining / recordsPerSecond);
					progress.setEstimatedRemainingTime(Duration.ofSeconds(estimatedSeconds));
				}
			}
		}
	}

	/**
	 * 记录失败的页面
	 */
	public void recordFailure(String taskId, int page, String reason) {
		CollectionProgress progress = progressMap.get(taskId);
		if (progress != null) {
			progress.incrementFailedPages();
			progress.addFailedPage(page, reason);
			log.warn("任务 {} 第 {} 页失败: {}", taskId, page, reason);
		}
	}

	/**
	 * 完成采集任务
	 */
	public void completeCollection(String taskId, boolean success) {
		CollectionProgress progress = progressMap.get(taskId);
		if (progress != null) {
			progress.setStatus(success ? "COMPLETED" : "FAILED");
			progress.setEndTime(LocalDateTime.now());

			Duration totalTime = Duration.between(progress.getStartTime(), progress.getEndTime());
			progress.setTotalDuration(totalTime);

			log.info("任务 {} 完成：状态 {}, 采集 {}/{} 条, 耗时 {} 秒, 失败页数 {}",
					taskId,
					progress.getStatus(),
					progress.getRecordsCollected(),
					progress.getTotalRecords(),
					totalTime.getSeconds(),
					progress.getFailedPages());
		}
	}

	/**
	 * 获取任务进度
	 */
	public CollectionProgress getProgress(String taskId) {
		return progressMap.get(taskId);
	}

	/**
	 * 获取所有任务
	 */
	public Map<String, CollectionProgress> getAllProgress() {
		return new ConcurrentHashMap<>(progressMap);
	}

	/**
	 * 清理已完成的任务
	 */
	public void cleanupCompleted() {
		progressMap.entrySet().removeIf(entry ->
				"COMPLETED".equals(entry.getValue().getStatus()) ||
						"FAILED".equals(entry.getValue().getStatus())
		);
	}

	@Data
	public static class CollectionProgress {
		private String taskId;
		private int totalRecords;
		private int totalPages;
		private int currentPage;
		private int recordsCollected;
		private int failedPages;
		private Map<Integer, String> failedPageDetails = new ConcurrentHashMap<>();
		private LocalDateTime startTime;
		private LocalDateTime endTime;
		private LocalDateTime lastUpdateTime;
		private Duration totalDuration;
		private Duration estimatedRemainingTime;
		private double collectionSpeed; // 记录/秒
		private String status; // RUNNING, COMPLETED, FAILED

		public void incrementFailedPages() {
			this.failedPages++;
		}

		public void addFailedPage(int page, String reason) {
			this.failedPageDetails.put(page, reason);
		}

		public double getProgressPercentage() {
			if (totalRecords == 0) return 0;
			return (double) recordsCollected / totalRecords * 100;
		}

		public String getFormattedSpeed() {
			if (collectionSpeed < 1) {
				return String.format("%.2f 条/秒", collectionSpeed);
			}
			return String.format("%.0f 条/秒", collectionSpeed);
		}

		public String getFormattedRemainingTime() {
			if (estimatedRemainingTime == null) return "计算中...";
			long minutes = estimatedRemainingTime.toMinutes();
			if (minutes < 1) {
				return estimatedRemainingTime.getSeconds() + " 秒";
			}
			return minutes + " 分钟";
		}
	}
}
