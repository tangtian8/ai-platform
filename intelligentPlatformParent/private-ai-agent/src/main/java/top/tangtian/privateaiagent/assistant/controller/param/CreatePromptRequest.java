package top.tangtian.privateaiagent.assistant.controller.param;

import lombok.Data;
import top.tangtian.privateaiagent.assistant.factory.PromptFactory;

import java.util.Map;

/**
 * @program: ai-platform
 * @description:
 * @author: tangtian
 * @create: 2026-02-11 16:35
 **/
@Data
public class CreatePromptRequest {
    private PromptFactory.PromptType type;
    private Map<String, Object> context;
}
