package top.tangtian.privateaiagent.assistant.controller.param;

import lombok.Data;

import java.util.Map;

/**
 * @program: ai-platform
 * @description:
 * @author: tangtian
 * @create: 2026-02-11 16:42
 **/
@Data
public class AddKnowledgeRequest {
    private String userId;
    private String title;
    private String content;
    private String source;
    private String category;
    private Map<String, Object> metadata;
}
