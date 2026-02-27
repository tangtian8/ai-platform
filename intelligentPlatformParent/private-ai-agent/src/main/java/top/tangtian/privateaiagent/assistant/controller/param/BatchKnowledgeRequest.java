package top.tangtian.privateaiagent.assistant.controller.param;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @program: ai-platform
 * @description:
 * @author: tangtian
 * @create: 2026-02-11 16:43
 **/
@Data
public class BatchKnowledgeRequest {
    private String userId;
    private List<Map<String, String>> knowledgeList;
}
