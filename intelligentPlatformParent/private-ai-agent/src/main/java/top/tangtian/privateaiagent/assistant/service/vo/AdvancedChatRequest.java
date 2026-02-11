package top.tangtian.privateaiagent.assistant.service.vo;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @program: ai-platform
 * @description: 对话请求
 * @author: tangtian
 * @create: 2026-02-11 09:14
 **/
@Data
@Builder
public class AdvancedChatRequest {
    private String userId;
    private String sessionId;
    private String message;
    private Map<String, Object> context;
    private boolean enableTools;
    private boolean enableMemory;
}
