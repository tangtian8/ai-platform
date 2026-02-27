package top.tangtian.privateaiagent.assistant.controller.param;

import lombok.Data;

/**
 * @program: ai-platform
 * @description:
 * @author: tangtian
 * @create: 2026-02-11 16:40
 **/
@Data
public class ChatRequest {
    private String userId;
    private String sessionId;
    private String message;
}
