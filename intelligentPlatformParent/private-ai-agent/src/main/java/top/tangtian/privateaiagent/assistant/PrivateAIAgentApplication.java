package top.tangtian.privateaiagent.assistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
public class PrivateAIAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(PrivateAIAgentApplication.class, args);
    }
}
