package top.tangtian;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import top.tangtian.esanalysis.IntelligentAnalysisApplication;
import top.tangtian.esanalysis.service.ElasticsearchService;

import java.time.LocalDate;
import java.util.Map;

/**
 * @author tangtian
 * @date 2025-12-03 20:05
 */
@SpringBootTest(classes = {IntelligentAnalysisApplication.class},webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("ES操作单元测试")
public class ElasticsearchServiceTest {

	private static final Logger log = LoggerFactory.getLogger(ElasticsearchServiceTest.class);
	@Resource
	private ElasticsearchService esService;

	@Test
	void analyzeDailyData(){
		LocalDate testDate = LocalDate.of(2024, 12, 1);
		LocalDate testDateEnd = LocalDate.of(2025, 12, 1);

		Map<String, Object> stringObjectMap = esService.analyzeDailyData(testDate, testDateEnd);
		log.info("analyzeDailyData:{}",stringObjectMap);
	}

	@Test
	void getHotTopics(){
		Map<String, Object> stringObjectMap = esService.getHotTopics(30);
		log.info("getHotTopics:{}",stringObjectMap);

	}


}
