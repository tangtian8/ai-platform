package top.tangtian.privateaiagent.assistant.mcp;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @program: ai-platform
 * @description: 模型实例
 * @author: tangtian
 * @create: 2026-02-11 10:19
 **/
@Data
@Builder
public class ModelInstance {
    private String modelId;
    private ModelType type;
    private ModelMetadata metadata;
    private Object model;  // 实际的模型对象
    private ModelStatus status;
    private LocalDateTime loadTime;
    private LocalDateTime lastUsed;
    private LocalDateTime unloadTime;
    private long loadDurationMs;
    private long usageCount;
    private String errorMessage;

    public void updateLastUsed() {
        this.lastUsed = LocalDateTime.now();
        this.usageCount++;
    }
}
