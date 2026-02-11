package top.tangtian.privateaiagent.assistant.chain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.*;

/**
 * @program: ai-platform
 * @description: 链步骤
 * @author: tangtian
 * @create: 2026-02-10 14:28
 **/
@Data
@Builder
@AllArgsConstructor
class ChainStep {
    private String stepId;
    private String toolName;
    private Map<String, Object> parameters;
    private String outputKey;
    private List<String> dependsOn;
    private String condition;
    private int retryCount;

    public ChainStep() {
        this.stepId = UUID.randomUUID().toString();
        this.parameters = new HashMap<>();
        this.dependsOn = new ArrayList<>();
        this.retryCount = 0;
    }
}