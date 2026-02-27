package top.tangtian.privateaiagent.assistant.controller.param;

import lombok.Data;

/**
 * @program: ai-platform
 * @description:
 * @author: tangtian
 * @create: 2026-02-11 16:39
 **/
@Data
public class SessionResponse {
    private String sessionId;
    private String message;

    public SessionResponse(String sessionId, String message) {
        this.sessionId = sessionId;
        this.message = message;
    }
}
