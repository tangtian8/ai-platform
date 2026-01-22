package top.tangtian.balofhope;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableScheduling
public class BalHopeApplication {
    public static void main(String[] args) {
        SpringApplication.run(BalHopeApplication.class, args);
    }
}