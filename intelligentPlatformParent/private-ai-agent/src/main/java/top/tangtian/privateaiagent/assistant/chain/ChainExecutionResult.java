package top.tangtian.privateaiagent.assistant.chain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @program: ai-platform
 * @description: 执行链路结果
 * @author: tangtian
 * @create: 2026-02-10 15:52
 **/
@Data
@Builder
public class ChainExecutionResult {
    private String chainId;
    private String chainName;
    private boolean success;
    private String error;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<StepExecutionResult> stepResults;
    private Map<String, Object> finalContext;

    public long getTotalExecutionTimeMs() {
        if (startTime != null && endTime != null) {
            return java.time.Duration.between(startTime, endTime).toMillis();
        }
        return 0;
    }
}
