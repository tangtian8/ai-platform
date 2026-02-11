package top.tangtian.privateaiagent.assistant.chain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @program: ai-platform
 * @description: 步骤执行结果
 * @author: tangtian
 * @create: 2026-02-10 15:52
 **/
@Data
@Builder
class StepExecutionResult {
    private String stepId;
    private String toolName;
    private boolean success;
    private Object result;
    private String error;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long executionTimeMs;
}
