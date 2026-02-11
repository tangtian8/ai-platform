package top.tangtian.privateaiagent.assistant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knuddels.jtokkit.api.ModelType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;
import top.tangtian.privateaiagent.assistant.chain.ChainExecutionResult;
import top.tangtian.privateaiagent.assistant.chain.ToolChain;
import top.tangtian.privateaiagent.assistant.chain.ToolChainEngine;
import top.tangtian.privateaiagent.assistant.factory.PromptFactory;
import top.tangtian.privateaiagent.assistant.mcp.MCPLifecycleManager;
import top.tangtian.privateaiagent.assistant.service.vo.AdvancedChatRequest;
import top.tangtian.privateaiagent.assistant.service.vo.AdvancedChatResponse;
import top.tangtian.privateaiagent.assistant.tools.ToolRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: ai-platform
 * @description: 高级对话服务
 *  * 整合了:
 *  * 1. 对话记忆管理
 *  * 2. 文本向量化
 *  * 3. 提示词工厂
 *  * 4. Tools 调用链
 *  * 5. MCP 模型生命周期
 * @author: tangtian
 * @create: 2026-02-11 09:09
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class AdvancedConversationService {

    private final ChatClient chatClient;
    private final MemoryManagementService memoryService;
    private final EnhancedRAGService ragService;
    private final PromptFactory promptFactory;
    private final ToolRegistry toolRegistry;
    private final ToolChainEngine chainEngine;
    private final MCPLifecycleManager mcpManager;
    private final ObjectMapper objectMapper;

    /**
     * 高级对话 - 支持工具调用
     */
    public AdvancedChatResponse advancedChat(AdvancedChatRequest request) {
        log.info("高级对话开始: session={}, user={}", request.getSessionId(), request.getUserId());

        try {
            // 1. 记忆管理 - 保存用户消息
            memoryService.saveMessage(request.getSessionId(), "user", request.getMessage());

            // 2. 意图识别
            String intent = recognizeIntent(request.getMessage());
            log.debug("识别意图: {}", intent);

            // 3. 根据意图决定处理策略
            String response = switch (intent) {
                case "TOOL_USE" -> handleToolUse(request);
                case "KNOWLEDGE_QUERY" -> handleKnowledgeQuery(request);
                case "COMPLEX_TASK" -> handleComplexTask(request);
                default -> handleNormalChat(request);
            };

            // 4. 保存助手回复
            memoryService.saveMessage(request.getSessionId(), "assistant", response);

            // 5. 构建响应
            return AdvancedChatResponse.builder()
                    .success(true)
                    .response(response)
                    .intent(intent)
                    .sessionId(request.getSessionId())
                    .build();

        } catch (Exception e) {
            log.error("高级对话失败", e);
            return AdvancedChatResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build();
        }
    }

    /**
     * 识别用户意图
     */
    private String recognizeIntent(String message) {
        try {
            // 使用提示词工厂创建意图识别提示词
            PromptFactory.PromptContext context = new PromptFactory.PromptContext()
                    .set("user_message", message);

            String prompt = promptFactory.createPrompt(
                    PromptFactory.PromptType.INTENT_RECOGNITION,
                    context
            );

            String intent = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content()
                    .trim();

            return intent;
        } catch (Exception e) {
            log.warn("意图识别失败，使用默认意图", e);
            return "QUESTION";
        }
    }

    /**
     * 处理工具使用请求
     */
    private String handleToolUse(AdvancedChatRequest request) {
        log.info("处理工具使用请求");

        // 1. 获取可用工具列表
        String toolsSchema = buildToolsSchema();

        // 2. 让 LLM 决定调用哪些工具
        PromptFactory.PromptContext context = new PromptFactory.PromptContext()
                .set("user_request", request.getMessage())
                .set("available_tools", toolsSchema);

        String toolCallPrompt = promptFactory.createPrompt(
                PromptFactory.PromptType.FUNCTION_CALLING,
                context
        );

        String toolCallPlan = chatClient.prompt()
                .user(toolCallPrompt)
                .call()
                .content();

        // 3. 解析工具调用计划并执行
        // 简化实现：这里应该解析 JSON 并调用相应工具
        log.debug("工具调用计划: {}", toolCallPlan);

        // 4. 使用工具结果生成最终回复
        return generateResponseWithToolResults(request, toolCallPlan);
    }

    /**
     * 处理知识库查询
     */
    private String handleKnowledgeQuery(AdvancedChatRequest request) {
        log.info("处理知识库查询");

        // 1. 使用增强 RAG 检索
        EnhancedRAGService.RetrievalResult retrieval = ragService.retrieveEnhanced(
                request.getUserId(),
                request.getMessage()
        );

        // 2. 构建带知识库的提示词
        PromptFactory.PromptContext context = new PromptFactory.PromptContext()
                .set("knowledge_context", retrieval.getFormattedContext());

        String systemPrompt = promptFactory.createPrompt(
                PromptFactory.PromptType.KNOWLEDGE_ENHANCED,
                context
        );

        // 3. 获取对话历史
        List<Message> messages = memoryService.getContextMessages(
                request.getSessionId(),
                systemPrompt
        );

        // 4. 调用 LLM 生成回复
        return chatClient.prompt()
                .messages(messages)
                .call()
                .content();
    }

    /**
     * 处理复杂任务（需要工具链）
     */
    private String handleComplexTask(AdvancedChatRequest request) {
        log.info("处理复杂任务");

        // 1. 生成工具链执行计划
        String toolsSchema = buildToolsSchema();

        PromptFactory.PromptContext context = new PromptFactory.PromptContext()
                .set("task_description", request.getMessage())
                .set("available_tools", toolsSchema);

        String chainPlan = promptFactory.createPrompt(
                PromptFactory.PromptType.TOOL_CHAIN,
                context
        );

        String planJson = chatClient.prompt()
                .user(chainPlan)
                .call()
                .content();

        // 2. 解析并构建工具链
        ToolChain chain = parseAndBuildChain(planJson, request);

        // 3. 执行工具链
        var chainResult = chainEngine.executeChain(chain);

        // 4. 基于执行结果生成回复
        return generateResponseFromChainResult(request, chainResult);
    }

    /**
     * 处理普通对话
     */
    private String handleNormalChat(AdvancedChatRequest request) {
        log.info("处理普通对话");

        // 使用标准对话流程
        String knowledgeContext = ragService.retrieveEnhanced(
                request.getUserId(),
                request.getMessage()
        ).getFormattedContext();

        PromptFactory.PromptContext context = new PromptFactory.PromptContext()
                .set("knowledge_context", knowledgeContext);

        String systemPrompt = promptFactory.createPromptChain(
                context,
                PromptFactory.PromptType.SYSTEM,
                PromptFactory.PromptType.KNOWLEDGE_ENHANCED
        );

        List<Message> messages = memoryService.getContextMessages(
                request.getSessionId(),
                systemPrompt
        );

        return chatClient.prompt()
                .messages(messages)
                .call()
                .content();
    }

    /**
     * 构建工具模式字符串
     */
    private String buildToolsSchema() {
        List<Map<String, Object>> schemas = toolRegistry.getToolsSchema();
        try {
            return objectMapper.writeValueAsString(schemas);
        } catch (Exception e) {
            log.error("构建工具模式失败", e);
            return "[]";
        }
    }

    /**
     * 使用工具结果生成回复
     */
    private String generateResponseWithToolResults(AdvancedChatRequest request, String toolResults) {
        String prompt = String.format("""
                用户请求: %s
                
                工具执行结果: %s
                
                请基于工具执行结果，生成自然、友好的回复。
                """, request.getMessage(), toolResults);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    /**
     * 解析并构建工具链
     */
    private ToolChain parseAndBuildChain(String planJson, AdvancedChatRequest request) {
        // 简化实现：实际应解析 JSON 构建完整的工具链
        ToolChain chain = ToolChain.builder()
                .name("complex_task_chain")
                .description("处理复杂任务的工具链")
                .build();

        // 添加步骤...

        return chain;
    }

    /**
     * 从工具链结果生成回复
     */
    private String generateResponseFromChainResult(AdvancedChatRequest request,
                                                   ChainExecutionResult result) {
        String prompt = String.format("""
                用户请求: %s
                
                任务执行%s
                
                执行结果: %s
                
                请生成友好的总结回复。
                """,
                request.getMessage(),
                result.isSuccess() ? "成功" : "失败",
                result.getFinalContext()
        );

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    /**
     * 预热系统（加载常用模型和工具）
     */
    public void warmUp() {
        log.info("系统预热开始...");

        // 预热模型
        mcpManager.warmUpModels(Map.of(
                "deepseek-chat", ModelType.CHAT,
                "deepseek-embedding", ModelType.EMBEDDING
        ));

        log.info("系统预热完成");
    }

    /**
     * 获取系统状态
     */
    public Map<String, Object> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("tools", toolRegistry.getStatistics());
        status.put("models", mcpManager.getStatistics());
        return status;
    }
}