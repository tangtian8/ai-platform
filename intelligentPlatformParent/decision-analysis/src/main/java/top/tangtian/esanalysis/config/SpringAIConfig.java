package top.tangtian.esanalysis.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author tangtian
 * @date 2025-12-02 19:55
 */
@Configuration
public class SpringAIConfig {

	/**
	 * 在 Spring AI 1.0.0-M6 中，使用 ChatClient.Builder 来构建 ChatClient
	 * Spring Boot 自动配置会提供 ChatModel，我们基于它创建 ChatClient
	 */
	@Bean
	public ChatClient chatClient(ChatClient.Builder builder) {
		return builder.build();
	}
}
