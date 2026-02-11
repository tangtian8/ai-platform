package top.tangtian.privateaiagent.assistant.config;


import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Value;

/**
 * @program: ai-platform
 * @description: Embedding 模型配置
 * Embedding 模型配置
 * 支持多种 embedding 模型:
 * 1. DeepSeek Embedding API (推荐,精度高)
 * 2. 本地 Transformers 模型 (离线可用,速度快)
 * 3. OpenAI Embedding (备选)
 * @author: tangtian
 * @create: 2026-02-10 16:19
 **/
@Configuration
public class EmbeddingConfig {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    @Value("${spring.ai.embedding.provider:deepseek}")
    private String embeddingProvider;

    /**
     * DeepSeek Embedding 模型
     * 优点: 精度高,支持中文,与 Chat 模型配套
     * 维度: 1024
     */
    @Bean(name = "deepseekEmbeddingModel")
    public EmbeddingModel deepseekEmbeddingModel() {
        OpenAiApi embeddingApi = new OpenAiApi(baseUrl, apiKey);
        OpenAiEmbeddingModel model = new OpenAiEmbeddingModel(embeddingApi);
        // DeepSeek 使用 text-embedding-v2 模型
        return model;
    }

    /**
     * 本地 Transformers Embedding 模型
     * 优点: 离线可用,速度快,免费
     * 推荐模型: paraphrase-multilingual-MiniLM-L12-v2 (支持多语言)
     * 维度: 384
     */
    @Bean(name = "localEmbeddingModel")
    public EmbeddingModel localEmbeddingModel() {
        return new TransformersEmbeddingModel();
    }

    /**
     * 主 Embedding 模型 (根据配置选择)
     */
    @Bean
    @Primary
    public EmbeddingModel embeddingModel() {
        return switch (embeddingProvider.toLowerCase()) {
            case "deepseek" -> deepseekEmbeddingModel();
            case "local" -> localEmbeddingModel();
            default -> deepseekEmbeddingModel();
        };
    }

    /**
     * 获取 embedding 维度
     */
    @Bean
    public int embeddingDimension() {
        return switch (embeddingProvider.toLowerCase()) {
            case "deepseek" -> 1024;  // DeepSeek embedding 维度
            case "local" -> 384;       // 本地模型维度
            default -> 1024;
        };
    }
}

