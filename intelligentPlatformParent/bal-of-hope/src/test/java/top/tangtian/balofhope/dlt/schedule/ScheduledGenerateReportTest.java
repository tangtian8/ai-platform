package top.tangtian.balofhope.dlt.schedule;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import top.tangtian.balofhope.BalHopeApplication;

/**
 * @author tangtian
 * @date 2026-01-24 09:06
 */
@SpringBootTest(classes = {BalHopeApplication.class},webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("数据分析单元测试")
public class ScheduledGenerateReportTest {
	private static final Logger log = LoggerFactory.getLogger(ScheduledGenerateReportTest.class);
	@Resource
	private ScheduledGenerateReport scheduledGenerateReport;
	@Test
	void scheduledDailyAnalysisDailyTest(){
		scheduledGenerateReport.executeAnalysisTask(100, "一百");
	}
	@Test
	void scheduledDailyAnalysisWeekTest(){
		scheduledGenerateReport.executeAnalysisTask(500, "五百");
	}
	@Test
	void scheduledDailyAnalysisMONTHLYTest(){
		scheduledGenerateReport.executeAnalysisTask(1000, "一千");
	}
	@Test
	void scheduledDailyAnalysisREALTIMETest(){
		scheduledGenerateReport.executeAnalysisTask(10*10000, "所有");
	}

}
