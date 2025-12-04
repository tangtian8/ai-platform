package top.tangtian.esanalysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author tangtian
 * @date 2025-12-01 09:38
 */
@SpringBootApplication
@EnableScheduling
public class IntelligentAnalysisApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntelligentAnalysisApplication.class, args);
	}
}