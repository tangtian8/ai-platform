package top.tangtian.privateaiagent.assistant.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: ai-platform
 * @description: 工具注册表
 * @author: tangtian
 * @create: 2026-02-10 15:54
 **/
@Slf4j
@Component
public class ToolRegistry {

    // 工具存储
    private final Map<String, ToolDefinition> tools = new ConcurrentHashMap<>();

    // 工具分类索引
    private final Map<String, Set<String>> categoryIndex = new ConcurrentHashMap<>();

    /**
     * 注册工具
     */
    public void registerTool(ToolDefinition tool) {
        tools.put(tool.getName(), tool);

        // 更新分类索引
        categoryIndex.computeIfAbsent(tool.getCategory(), k -> new HashSet<>())
                .add(tool.getName());

        log.info("注册工具: {} ({})", tool.getName(), tool.getCategory());
    }

    /**
     * 批量注册工具
     */
    public void registerTools(List<ToolDefinition> toolList) {
        toolList.forEach(this::registerTool);
    }

    /**
     * 获取工具
     */
    public ToolDefinition getTool(String name) {
        return tools.get(name);
    }

    /**
     * 获取所有工具
     */
    public Collection<ToolDefinition> getAllTools() {
        return tools.values();
    }

    /**
     * 按分类获取工具
     */
    public List<ToolDefinition> getToolsByCategory(String category) {
        Set<String> toolNames = categoryIndex.get(category);
        if (toolNames == null) {
            return Collections.emptyList();
        }

        List<ToolDefinition> result = new ArrayList<>();
        for (String name : toolNames) {
            ToolDefinition tool = tools.get(name);
            if (tool != null) {
                result.add(tool);
            }
        }
        return result;
    }

    /**
     * 搜索工具（按名称或描述）
     */
    public List<ToolDefinition> searchTools(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        List<ToolDefinition> results = new ArrayList<>();

        for (ToolDefinition tool : tools.values()) {
            if (tool.getName().toLowerCase().contains(lowerKeyword) ||
                    tool.getDescription().toLowerCase().contains(lowerKeyword)) {
                results.add(tool);
            }
        }

        return results;
    }

    /**
     * 检查工具是否存在
     */
    public boolean hasTool(String name) {
        return tools.containsKey(name);
    }

    /**
     * 注销工具
     */
    public void unregisterTool(String name) {
        ToolDefinition tool = tools.remove(name);
        if (tool != null) {
            Set<String> categoryTools = categoryIndex.get(tool.getCategory());
            if (categoryTools != null) {
                categoryTools.remove(name);
            }
            log.info("注销工具: {}", name);
        }
    }

    /**
     * 获取工具的 JSON Schema (用于 LLM Function Calling)
     */
    public List<Map<String, Object>> getToolsSchema() {
        List<Map<String, Object>> schemas = new ArrayList<>();

        for (ToolDefinition tool : tools.values()) {
            Map<String, Object> schema = new HashMap<>();
            schema.put("type", "function");

            Map<String, Object> function = new HashMap<>();
            function.put("name", tool.getName());
            function.put("description", tool.getDescription());
            function.put("parameters", tool.getParametersSchema());

            schema.put("function", function);
            schemas.add(schema);
        }

        return schemas;
    }

    /**
     * 获取所有分类
     */
    public Set<String> getCategories() {
        return new HashSet<>(categoryIndex.keySet());
    }

    /**
     * 获取统计信息
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_tools", tools.size());
        stats.put("categories", categoryIndex.size());

        Map<String, Integer> categoryCounts = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : categoryIndex.entrySet()) {
            categoryCounts.put(entry.getKey(), entry.getValue().size());
        }
        stats.put("tools_by_category", categoryCounts);

        return stats;
    }
}
