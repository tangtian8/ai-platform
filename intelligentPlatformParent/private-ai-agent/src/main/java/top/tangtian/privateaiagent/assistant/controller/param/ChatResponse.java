package top.tangtian.privateaiagent.assistant.controller.param;

import lombok.Data;

/**
 * @program: ai-platform
 * @description:
 * @author: tangtian
 * @create: 2026-02-11 16:42
 **/
@Data
public class ChatResponse {
    private String response;
    private boolean success;
    private String error;

    public ChatResponse(String response, boolean success, String error) {
        this.response = response;
        this.success = success;
        this.error = error;
    }
}
