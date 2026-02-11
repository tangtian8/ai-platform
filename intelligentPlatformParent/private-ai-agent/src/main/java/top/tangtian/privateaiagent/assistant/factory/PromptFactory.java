package top.tangtian.privateaiagent.assistant.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: ai-platform
 * @description: 提示词工厂  * 基于工厂模式和策略模式，提供灵活的提示词生成能力
 * @author: tangtian
 * @create: 2026-02-11 10:16
 **/
@Slf4j
@Component
public class PromptFactory {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");

    // 提示词模板缓存
    private final Map<String, PromptTemplate> templateCache = new ConcurrentHashMap<>();

    // 提示词构建器注册表
    private final Map<PromptType, PromptBuilder> builderRegistry = new ConcurrentHashMap<>();

    public PromptFactory() {
        // 注册默认构建器
        registerDefaultBuilders();
    }

    /**
     * 注册默认的提示词构建器
     */
    private void registerDefaultBuilders() {
        builderRegistry.put(PromptType.SYSTEM, new SystemPromptBuilder());
        builderRegistry.put(PromptType.KNOWLEDGE_ENHANCED, new KnowledgeEnhancedPromptBuilder());
        builderRegistry.put(PromptType.MEMORY_COMPRESSION, new MemoryCompressionPromptBuilder());
        builderRegistry.put(PromptType.QUERY_OPTIMIZATION, new QueryOptimizationPromptBuilder());
        builderRegistry.put(PromptType.FUNCTION_CALLING, new FunctionCallingPromptBuilder());
        builderRegistry.put(PromptType.TOOL_CHAIN, new ToolChainPromptBuilder());
        builderRegistry.put(PromptType.ERROR_HANDLING, new ErrorHandlingPromptBuilder());
        builderRegistry.put(PromptType.INTENT_RECOGNITION, new IntentRecognitionPromptBuilder());
    }

    /**
     * 注册自定义提示词构建器
     */
    public void registerBuilder(PromptType type, PromptBuilder builder) {
        builderRegistry.put(type, builder);
        log.info("注册提示词构建器: {}", type);
    }

    /**
     * 创建提示词
     */
    public String createPrompt(PromptType type, PromptContext context) {
        PromptBuilder builder = builderRegistry.get(type);
        if (builder == null) {
            log.warn("未找到提示词构建器: {}, 使用默认构建器", type);
            builder = new DefaultPromptBuilder();
        }

        return builder.build(context);
    }

    /**
     * 批量创建提示词
     */
    public Map<PromptType, String> createPrompts(PromptContext context, PromptType... types) {
        Map<PromptType, String> prompts = new HashMap<>();
        for (PromptType type : types) {
            prompts.put(type, createPrompt(type, context));
        }
        return prompts;
    }

    /**
     * 创建提示词链（组合多个提示词）
     */
    public String createPromptChain(PromptContext context, PromptType... types) {
        StringBuilder chain = new StringBuilder();
        for (PromptType type : types) {
            String prompt = createPrompt(type, context);
            if (!prompt.isEmpty()) {
                chain.append(prompt).append("\n\n");
            }
        }
        return chain.toString().trim();
    }

    /**
     * 从模板创建提示词
     */
    public String createFromTemplate(String templateId, Map<String, Object> variables) {
        PromptTemplate template = templateCache.get(templateId);
        if (template == null) {
            log.warn("未找到提示词模板: {}", templateId);
            return "";
        }

        return template.render(variables);
    }

    /**
     * 注册提示词模板
     */
    public void registerTemplate(String templateId, PromptTemplate template) {
        templateCache.put(templateId, template);
        log.info("注册提示词模板: {}", templateId);
    }

    // ==================== 提示词类型 ====================

    public enum PromptType {
        SYSTEM,                 // 系统提示词
        KNOWLEDGE_ENHANCED,     // 知识库增强
        MEMORY_COMPRESSION,     // 记忆压缩
        QUERY_OPTIMIZATION,     // 查询优化
        FUNCTION_CALLING,       // 函数调用
        TOOL_CHAIN,            // 工具链
        ERROR_HANDLING,        // 错误处理
        INTENT_RECOGNITION,    // 意图识别
        CUSTOM                 // 自定义
    }

    // ==================== 提示词上下文 ====================

    public static class PromptContext {
        private final Map<String, Object> data = new HashMap<>();

        public PromptContext set(String key, Object value) {
            data.put(key, value);
            return this;
        }

        public <T> T get(String key, Class<T> type) {
            Object value = data.get(key);
            return type.cast(value);
        }

        public String getString(String key) {
            return (String) data.get(key);
        }

        public Integer getInt(String key) {
            return (Integer) data.get(key);
        }

        public Boolean getBoolean(String key) {
            return (Boolean) data.get(key);
        }

        public Map<String, Object> getAll() {
            return new HashMap<>(data);
        }
    }

    // ==================== 提示词构建器接口 ====================

    public interface PromptBuilder {
        String build(PromptContext context);
    }

    // ==================== 提示词模板 ====================

    public static class PromptTemplate {
        private final String template;

        public PromptTemplate(String template) {
            this.template = template;
        }

        public String render(Map<String, Object> variables) {
            String result = template;
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                String placeholder = "{" + entry.getKey() + "}";
                result = result.replace(placeholder, String.valueOf(entry.getValue()));
            }
            return result;
        }
    }

    // ==================== 内置构建器实现 ====================

    /**
     * 系统提示词构建器
     */
    private static class SystemPromptBuilder implements PromptBuilder {
        @Override
        public String build(PromptContext context) {
            String role = context.getString("role");
            String capabilities = context.getString("capabilities");
            String guidelines = context.getString("guidelines");

            return String.format("""
                # 角色定义
                你是一个%s
                
                ## 核心能力
                %s
                
                ## 行为准则
                %s
                
                ## 当前上下文
                - 当前时间: %s
                - 对话模式: 长期记忆模式（支持 1000+ 轮对话）
                """,
                    role != null ? role : "AI 智能助手",
                    capabilities != null ? capabilities : "- 长期记忆\n- 知识库访问",
                    guidelines != null ? guidelines : "- 准确性\n- 有用性",
                    LocalDateTime.now().format(DATE_FORMATTER)
            );
        }
    }

    /**
     * 知识库增强提示词构建器
     */
    private static class KnowledgeEnhancedPromptBuilder implements PromptBuilder {
        @Override
        public String build(PromptContext context) {
            String knowledgeContext = context.getString("knowledge_context");
            if (knowledgeContext == null || knowledgeContext.isEmpty()) {
                return "";
            }

            return String.format("""
                ## 📚 相关知识库资料
                
                以下是从用户个人知识库检索的相关资料：
                
                %s
                
                **重要提示**:
                - 引用资料时请明确标注来源
                - 这些是用户个人资料，具有高度相关性
                - 优先使用知识库内容
                """, knowledgeContext);
        }
    }

    /**
     * 记忆压缩提示词构建器
     */
    private static class MemoryCompressionPromptBuilder implements PromptBuilder {
        @Override
        public String build(PromptContext context) {
            String conversationHistory = context.getString("conversation_history");

            return String.format("""
                # 任务: 对话记忆压缩
                
                请将以下对话历史压缩成简洁摘要：
                
                ## 压缩原则
                1. 保留用户个人信息、重要事实和关键决策
                2. 删除寒暄、重复信息和不重要细节
                3. 使用第三人称，200-300字
                
                ## 对话历史
                ```
                %s
                ```
                
                ## 摘要:
                """, conversationHistory);
        }
    }

    /**
     * 查询优化提示词构建器
     */
    private static class QueryOptimizationPromptBuilder implements PromptBuilder {
        @Override
        public String build(PromptContext context) {
            String query = context.getString("query");

            return String.format("""
                # 任务: 查询优化
                
                用户查询: "%s"
                
                请优化为更适合向量检索的形式：
                1. 提取核心关键词
                2. 扩展相关同义词
                3. 添加语义上下文
                
                只输出优化后的查询:
                """, query);
        }
    }

    /**
     * 函数调用提示词构建器
     */
    private static class FunctionCallingPromptBuilder implements PromptBuilder {
        @Override
        public String build(PromptContext context) {
            String availableTools = context.getString("available_tools");
            String userRequest = context.getString("user_request");

            return String.format("""
                # 任务: 函数调用
                
                用户请求: %s
                
                可用工具:
                %s
                
                请分析用户请求，决定需要调用哪些工具，并以 JSON 格式返回调用参数。
                
                输出格式:
                ```json
                {
                  "tool_name": "工具名称",
                  "parameters": {
                    "param1": "value1"
                  }
                }
                ```
                """, userRequest, availableTools);
        }
    }

    /**
     * 工具链提示词构建器
     */
    private static class ToolChainPromptBuilder implements PromptBuilder {
        @Override
        public String build(PromptContext context) {
            String taskDescription = context.getString("task_description");
            String availableTools = context.getString("available_tools");

            return String.format("""
                # 任务: 工具链规划
                
                任务描述: %s
                
                可用工具:
                %s
                
                请规划执行步骤，确定工具调用顺序和依赖关系。
                
                输出格式:
                ```json
                {
                  "steps": [
                    {
                      "step": 1,
                      "tool": "工具名",
                      "description": "步骤描述",
                      "depends_on": []
                    }
                  ]
                }
                ```
                """, taskDescription, availableTools);
        }
    }

    /**
     * 错误处理提示词构建器
     */
    private static class ErrorHandlingPromptBuilder implements PromptBuilder {
        @Override
        public String build(PromptContext context) {
            String errorType = context.getString("error_type");
            String errorMessage = context.getString("error_message");

            return String.format("""
                发生了错误: %s
                
                错误信息: %s
                
                请提供友好的用户提示和可能的解决方案。
                """, errorType, errorMessage);
        }
    }

    /**
     * 意图识别提示词构建器
     */
    private static class IntentRecognitionPromptBuilder implements PromptBuilder {
        @Override
        public String build(PromptContext context) {
            String userMessage = context.getString("user_message");

            return String.format("""
                # 任务: 识别用户意图
                
                用户消息: "%s"
                
                从以下选项中选择最匹配的意图:
                - QUESTION (提问)
                - TASK (执行任务)
                - CHAT (闲聊)
                - TOOL_USE (使用工具)
                - KNOWLEDGE_ADD (添加知识)
                
                只输出意图类型:
                """, userMessage);
        }
    }

    /**
     * 默认提示词构建器
     */
    private static class DefaultPromptBuilder implements PromptBuilder {
        @Override
        public String build(PromptContext context) {
            return context.getString("content");
        }
    }
}
