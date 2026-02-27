package top.tangtian.privateaiagent.assistant.controller.param;

import lombok.Data;
import top.tangtian.privateaiagent.assistant.mcp.ModelType;

/**
 * @program: ai-platform
 * @description:
 * @author: tangtian
 * @create: 2026-02-11 16:35
 **/
@Data
public class WarmUpRequest {
    private String modelId;
    private ModelType modelType;
}
