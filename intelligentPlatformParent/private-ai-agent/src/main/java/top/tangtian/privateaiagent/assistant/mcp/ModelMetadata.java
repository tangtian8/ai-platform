package top.tangtian.privateaiagent.assistant.mcp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: ai-platform
 * @description: 模型元数据
 * @author: tangtian
 * @create: 2026-02-11 10:20
 **/
@Data
@Builder
@AllArgsConstructor
public class ModelMetadata {
    private String modelId;
    private String name;
    private ModelType type;
    private String version;
    private String provider;
    private String description;
    private Map<String, Object> config;
    private Map<String, String> capabilities;
    private int contextWindow;
    private int embeddingDimension;
    private boolean supportsFunctionCalling;
    private List<String> supportedLanguages;

    public ModelMetadata() {
        this.config = new HashMap<>();
        this.capabilities = new HashMap<>();
        this.supportedLanguages = new ArrayList<>();
    }
}
