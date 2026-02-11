package top.tangtian.privateaiagent.assistant.chain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.*;

/**
 * @program: ai-platform
 * @description: 工具链定义
 * @author: tangtian
 * @create: 2026-02-10 13:59
 **/
@Data
@Builder
@AllArgsConstructor
public class ToolChain {
    private String id;
    private String name;
    private String description;
    private List<ChainStep> steps;
    private Map<String, Object> initialContext;
    private ChainExecutionMode executionMode;

    public ToolChain() {
        this.id = UUID.randomUUID().toString();
        this.steps = new ArrayList<>();
        this.initialContext = new HashMap<>();
        this.executionMode = ChainExecutionMode.SEQUENTIAL;
    }
}
