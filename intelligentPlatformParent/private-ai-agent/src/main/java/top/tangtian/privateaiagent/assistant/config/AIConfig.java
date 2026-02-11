package top.tangtian.privateaiagent.assistant.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;


/**
 * @program: ai-platform
 * @description: spring AI 配置框架
 * @author: tangtian
 * @create: 2026-02-10 16:11
 **/
@Configuration
public class AIConfig {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    /**
     * 配置 DeepSeek Chat 模型
     */
    @Bean
    public OpenAiChatModel openAiChatModel() {
        OpenAiApi openAiApi = new OpenAiApi(baseUrl, apiKey);
        return new OpenAiChatModel(openAiApi);
    }

    /**
     * 配置 ChatClient
     */
    @Bean
    public ChatClient chatClient(OpenAiChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }

    /**
     * 配置 Embedding 模型
     * 注意: DeepSeek 也提供 embedding API,可以使用
     * 或者使用本地 Transformer 模型
     */
    @Bean
    public EmbeddingModel embeddingModel() {
        // 使用 DeepSeek Embedding API
        OpenAiApi embeddingApi = new OpenAiApi(baseUrl, apiKey);
        return new OpenAiEmbeddingModel(embeddingApi);

        // 或者使用本地模型:
        // return new TransformersEmbeddingModel();
    }

    /**
     * 配置 PGVector 存储
     */
    @Bean
    public VectorStore vectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        return new PgVectorStore(jdbcTemplate, embeddingModel);
    }
}
