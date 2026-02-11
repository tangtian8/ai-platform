package top.tangtian.privateaiagent.assistant.mcp;

import lombok.Builder;
import lombok.Data;

/**
 * @program: ai-platform
 * @description: 模型信息
 * @author: tangtian
 * @create: 2026-02-11 10:22
 **/
@Data
@Builder
public class ModelInfo {
    private String modelId;
    private ModelStatus status;
    private ModelMetadata metadata;
    private ModelInstance instance;
}
