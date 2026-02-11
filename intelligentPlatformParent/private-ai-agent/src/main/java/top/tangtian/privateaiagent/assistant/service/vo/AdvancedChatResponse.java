package top.tangtian.privateaiagent.assistant.service.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @program: ai-platform
 * @description: 对话响应
 * @author: tangtian
 * @create: 2026-02-11 09:15
 **/
@Data
@Builder
public class AdvancedChatResponse {
    private boolean success;
    private String response;
    private String intent;
    private String sessionId;
    private List<String> toolsUsed;
    private String error;
    private Map<String, Object> metadata;
}
