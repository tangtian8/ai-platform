package top.tangtian.balofhope.dlt.model;

import java.time.LocalDateTime;

/**
 * @author tangtian
 * @date 2026-01-21 16:30
 */
public class CrawlResult {
	/** 是否成功 */
	private boolean success;

	/** 开始时间 */
	private LocalDateTime startTime;

	/** 结束时间 */
	private LocalDateTime endTime;

	/** 耗时（秒） */
	private long durationSeconds;

	/** 总页数 */
	private int totalPages;

	/** 总记录数 */
	private int totalRecords;

	/** 爬取数量 */
	private int crawledCount;

	/** 保存数量 */
	private int savedCount;

	/** 失败页数 */
	private int failedPages;

	/** 错误信息 */
	private String errorMessage;

	public void calculateDuration() {
		if (startTime != null && endTime != null) {
			this.durationSeconds = java.time.Duration.between(startTime, endTime).getSeconds();
		}
	}

	public void incrementFailedPages() {
		this.failedPages++;
	}

	@Override
	public String toString() {
		return String.format(
				"CrawlResult{success=%s, totalPages=%d, totalRecords=%d, crawledCount=%d, " +
						"savedCount=%d, failedPages=%d, duration=%ds, error='%s'}",
				success, totalPages, totalRecords, crawledCount, savedCount,
				failedPages, durationSeconds, errorMessage);
	}

	// Getters and Setters
	public boolean isSuccess() { return success; }
	public void setSuccess(boolean success) { this.success = success; }
	public LocalDateTime getStartTime() { return startTime; }
	public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
	public LocalDateTime getEndTime() { return endTime; }
	public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
	public long getDurationSeconds() { return durationSeconds; }
	public int getTotalPages() { return totalPages; }
	public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
	public int getTotalRecords() { return totalRecords; }
	public void setTotalRecords(int totalRecords) { this.totalRecords = totalRecords; }
	public int getCrawledCount() { return crawledCount; }
	public void setCrawledCount(int crawledCount) { this.crawledCount = crawledCount; }
	public int getSavedCount() { return savedCount; }
	public void setSavedCount(int savedCount) { this.savedCount = savedCount; }
	public int getFailedPages() { return failedPages; }
	public String getErrorMessage() { return errorMessage; }
	public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
