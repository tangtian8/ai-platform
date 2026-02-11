package top.tangtian.privateaiagent.assistant.tools;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * @program: ai-platform
 * @description: 工具定义
 * @author: tangtian
 * @create: 2026-02-10 15:55
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolDefinition {
    /**
     * 工具名称（唯一标识）
     */
    private String name;

    /**
     * 工具描述
     */
    private String description;

    /**
     * 工具分类
     */
    private String category;

    /**
     * 参数定义（JSON Schema）
     */
    private Map<String, Object> parametersSchema;

    /**
     * 是否需要用户确认
     */
    private boolean requiresConfirmation;

    /**
     * 工具执行器
     */
    private ToolExecutor executor;

    /**
     * 标签（用于搜索和分类）
     */
    private Set<String> tags;

    /**
     * 示例用法
     */
    private List<ToolExample> examples;

    /**
     * 工具版本
     */
    private String version;

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 元数据
     */
    private Map<String, Object> metadata;

    public ToolDefinition(String name, String description, String category,
                          Map<String, Object> parametersSchema, ToolExecutor executor) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.parametersSchema = parametersSchema;
        this.executor = executor;
        this.enabled = true;
        this.requiresConfirmation = false;
        this.tags = new HashSet<>();
        this.examples = new ArrayList<>();
        this.metadata = new HashMap<>();
    }
}
