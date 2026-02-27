package top.tangtian.privateaiagent.assistant.controller.param;

import lombok.Data;
import top.tangtian.privateaiagent.assistant.factory.PromptFactory;

import java.util.List;
import java.util.Map;

/**
 * @program: ai-platform
 * @description:
 * @author: tangtian
 * @create: 2026-02-11 16:36
 **/
@Data
public class CreatePromptChainRequest {
    private List<PromptFactory.PromptType> types;
    private Map<String, Object> context;
}
