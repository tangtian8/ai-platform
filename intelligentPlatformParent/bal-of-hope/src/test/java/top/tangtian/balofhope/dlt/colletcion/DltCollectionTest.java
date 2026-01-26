package top.tangtian.balofhope.dlt.colletcion;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import top.tangtian.balofhope.BalHopeApplication;

import java.time.LocalDate;
import java.util.Map;

/**
 * @author tangtian
 * @date 2026-01-21 17:52
 */
@SpringBootTest(classes = {BalHopeApplication.class},webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("数据采集单元测试")
public class DltCollectionTest {
	private static final Logger log = LoggerFactory.getLogger(DltCollectionTest.class);
	@Resource
	private DltCollection dltCollection;
	@Test
	void analyzeDailyData(){
		dltCollection.crawlAllHistoryData(null,null);
	}

	@Test
	void getLatestDrawInfoTest(){
		dltCollection.getLatestDrawInfo();
	}
	@Test
	void crawlLatestData(){
		dltCollection.crawlLatestData();
	}
}
