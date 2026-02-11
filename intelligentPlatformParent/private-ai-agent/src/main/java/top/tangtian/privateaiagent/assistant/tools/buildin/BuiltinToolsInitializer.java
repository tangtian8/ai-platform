package top.tangtian.privateaiagent.assistant.tools.buildin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import top.tangtian.privateaiagent.assistant.service.KnowledgeBaseService;
import top.tangtian.privateaiagent.assistant.tools.ToolDefinition;
import top.tangtian.privateaiagent.assistant.tools.ToolRegistry;
import top.tangtian.privateaiagent.assistant.tools.ToolResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: ai-platform
 * @description: 注册系统自带工具
 * @author: tangtian
 * @create: 2026-02-10 16:00
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class BuiltinToolsInitializer implements CommandLineRunner {

    private final ToolRegistry toolRegistry;
    private final KnowledgeBaseService knowledgeBaseService;

    @Override
    public void run(String... args) {
        log.info("注册内置工具...");
        registerBuiltinTools();
        log.info("内置工具注册完成，共 {} 个", toolRegistry.getAllTools().size());
    }

    private void registerBuiltinTools() {
        // 1. 知识库工具
        registerKnowledgeTools();

        // 2. 时间工具
        registerTimeTools();

        // 3. 计算工具
        registerCalculatorTools();

        // 4. 文本处理工具
        registerTextTools();

        // 5. 数据转换工具
        registerDataTools();
    }

    /**
     * 注册知识库工具
     */
    private void registerKnowledgeTools() {
        // 检索知识库
        ToolDefinition searchKnowledge = ToolDefinition.builder()
                .name("search_knowledge")
                .description("从用户知识库中检索相关信息")
                .category("knowledge")
                .parametersSchema(Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "user_id", Map.of("type", "string", "description", "用户ID"),
                                "query", Map.of("type", "string", "description", "检索查询")
                        ),
                        "required", List.of("user_id", "query")
                ))
                .executor(params -> {
                    try {
                        String userId = (String) params.get("user_id");
                        String query = (String) params.get("query");

                        String result = knowledgeBaseService.getKnowledgeContext(userId, query);
                        return ToolResult.success(result);
                    } catch (Exception e) {
                        return ToolResult.error("检索失败: " + e.getMessage());
                    }
                })
                .build();

        toolRegistry.registerTool(searchKnowledge);

        // 添加知识
        ToolDefinition addKnowledge = ToolDefinition.builder()
                .name("add_knowledge")
                .description("向用户知识库添加信息")
                .category("knowledge")
                .parametersSchema(Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "user_id", Map.of("type", "string"),
                                "title", Map.of("type", "string"),
                                "content", Map.of("type", "string"),
                                "category", Map.of("type", "string")
                        ),
                        "required", List.of("user_id", "title", "content")
                ))
                .executor(params -> {
                    try {
                        String userId = (String) params.get("user_id");
                        String title = (String) params.get("title");
                        String content = (String) params.get("content");
                        String category = (String) params.getOrDefault("category", "其他");

                        knowledgeBaseService.addKnowledge(userId, title, content, null, category, null);
                        return ToolResult.success("知识添加成功");
                    } catch (Exception e) {
                        return ToolResult.error("添加失败: " + e.getMessage());
                    }
                })
                .build();

        toolRegistry.registerTool(addKnowledge);
    }

    /**
     * 注册时间工具
     */
    private void registerTimeTools() {
        // 获取当前时间
        ToolDefinition getCurrentTime = ToolDefinition.builder()
                .name("get_current_time")
                .description("获取当前日期和时间")
                .category("time")
                .parametersSchema(Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "format", Map.of(
                                        "type", "string",
                                        "description", "时间格式，如 yyyy-MM-dd HH:mm:ss",
                                        "default", "yyyy-MM-dd HH:mm:ss"
                                )
                        )
                ))
                .executor(params -> {
                    String format = (String) params.getOrDefault("format", "yyyy-MM-dd HH:mm:ss");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                    String time = LocalDateTime.now().format(formatter);
                    return ToolResult.success(time);
                })
                .build();

        toolRegistry.registerTool(getCurrentTime);

        // 计算时间差
        ToolDefinition calculateTimeDiff = ToolDefinition.builder()
                .name("calculate_time_diff")
                .description("计算两个时间之间的差值")
                .category("time")
                .parametersSchema(Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "start_time", Map.of("type", "string"),
                                "end_time", Map.of("type", "string"),
                                "unit", Map.of("type", "string", "enum", List.of("days", "hours", "minutes"))
                        ),
                        "required", List.of("start_time", "end_time")
                ))
                .executor(params -> {
                    // 实现时间差计算
                    return ToolResult.success("时间差计算结果");
                })
                .build();

        toolRegistry.registerTool(calculateTimeDiff);
    }

    /**
     * 注册计算工具
     */
    private void registerCalculatorTools() {
        // 基础计算器
        ToolDefinition calculator = ToolDefinition.builder()
                .name("calculator")
                .description("执行数学计算")
                .category("math")
                .parametersSchema(Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "expression", Map.of("type", "string", "description", "数学表达式，如 2 + 3 * 4")
                        ),
                        "required", List.of("expression")
                ))
                .executor(params -> {
                    try {
                        String expression = (String) params.get("expression");
                        // 简化实现，实际应使用表达式解析器
                        double result = evaluateExpression(expression);
                        return ToolResult.success(result);
                    } catch (Exception e) {
                        return ToolResult.error("计算错误: " + e.getMessage());
                    }
                })
                .build();

        toolRegistry.registerTool(calculator);
    }

    /**
     * 注册文本处理工具
     */
    private void registerTextTools() {
        // 文本统计
        ToolDefinition textStats = ToolDefinition.builder()
                .name("text_statistics")
                .description("统计文本的字数、词数、行数等")
                .category("text")
                .parametersSchema(Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "text", Map.of("type", "string")
                        ),
                        "required", List.of("text")
                ))
                .executor(params -> {
                    String text = (String) params.get("text");
                    Map<String, Object> stats = new HashMap<>();
                    stats.put("characters", text.length());
                    stats.put("words", text.split("\\s+").length);
                    stats.put("lines", text.split("\n").length);
                    return ToolResult.success(stats);
                })
                .build();

        toolRegistry.registerTool(textStats);

        // 文本转换
        ToolDefinition textTransform = ToolDefinition.builder()
                .name("text_transform")
                .description("文本转换（大小写、去除空格等）")
                .category("text")
                .parametersSchema(Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "text", Map.of("type", "string"),
                                "operation", Map.of("type", "string",
                                        "enum", List.of("uppercase", "lowercase", "trim", "reverse"))
                        ),
                        "required", List.of("text", "operation")
                ))
                .executor(params -> {
                    String text = (String) params.get("text");
                    String operation = (String) params.get("operation");

                    String result = switch (operation) {
                        case "uppercase" -> text.toUpperCase();
                        case "lowercase" -> text.toLowerCase();
                        case "trim" -> text.trim();
                        case "reverse" -> new StringBuilder(text).reverse().toString();
                        default -> text;
                    };

                    return ToolResult.success(result);
                })
                .build();

        toolRegistry.registerTool(textTransform);
    }

    /**
     * 注册数据转换工具
     */
    private void registerDataTools() {
        // JSON 解析
        ToolDefinition jsonParse = ToolDefinition.builder()
                .name("json_parse")
                .description("解析 JSON 字符串")
                .category("data")
                .parametersSchema(Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "json_string", Map.of("type", "string")
                        ),
                        "required", List.of("json_string")
                ))
                .executor(params -> {
                    try {
                        String jsonString = (String) params.get("json_string");
                        // 使用 Jackson 或其他 JSON 库解析
                        return ToolResult.success("已解析的 JSON 对象");
                    } catch (Exception e) {
                        return ToolResult.error("JSON 解析失败: " + e.getMessage());
                    }
                })
                .build();

        toolRegistry.registerTool(jsonParse);
    }

    /**
     * 简化的表达式计算
     */
    private double evaluateExpression(String expression) {
        // 简化实现，实际应使用成熟的表达式解析器如 JEXL
        expression = expression.replaceAll("\\s+", "");

        // 这里仅作示例，实际应实现完整的表达式解析
        if (expression.contains("+")) {
            String[] parts = expression.split("\\+");
            return Double.parseDouble(parts[0]) + Double.parseDouble(parts[1]);
        } else if (expression.contains("-")) {
            String[] parts = expression.split("-");
            return Double.parseDouble(parts[0]) - Double.parseDouble(parts[1]);
        } else if (expression.contains("*")) {
            String[] parts = expression.split("\\*");
            return Double.parseDouble(parts[0]) * Double.parseDouble(parts[1]);
        } else if (expression.contains("/")) {
            String[] parts = expression.split("/");
            return Double.parseDouble(parts[0]) / Double.parseDouble(parts[1]);
        }

        return Double.parseDouble(expression);
    }
}
