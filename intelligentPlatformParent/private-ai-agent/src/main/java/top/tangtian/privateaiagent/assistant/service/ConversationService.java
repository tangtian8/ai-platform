package top.tangtian.privateaiagent.assistant.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;
import top.tangtian.privateaiagent.assistant.entity.ConversationSession;
import top.tangtian.privateaiagent.assistant.repository.ConversationSessionRepository;

import java.util.List;
import java.util.UUID;

/**
 * @program: ai-platform
 * @description: 对话服务
 *  * 整合记忆管理和知识库检索
 * @author: tangtian
 * @create: 2026-02-11 09:13
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ChatClient chatClient;
    private final ConversationSessionRepository sessionRepository;
    private final MemoryManagementService memoryService;
    private final KnowledgeBaseService knowledgeBaseService;
    private final PromptService promptService;

    /**
     * 创建新会话
     */
    @Transactional
    public String createSession(String userId) {
        String sessionId = UUID.randomUUID().toString();

        ConversationSession session = ConversationSession.builder()
                .userId(userId)
                .sessionId(sessionId)
                .isActive(true)
                .build();

        sessionRepository.save(session);

        log.info("创建新会话: userId={}, sessionId={}", userId, sessionId);
        return sessionId;
    }

    /**
     * 发送消息并获取回复
     */
    @Transactional
    public String chat(String userId, String sessionId, String userMessage) {
        try {
            // 1. 验证会话
            ConversationSession session = sessionRepository.findBySessionId(sessionId)
                    .orElseThrow(() -> new RuntimeException("会话不存在: " + sessionId));

            if (!session.getUserId().equals(userId)) {
                throw new RuntimeException("会话不属于当前用户");
            }

            // 2. 保存用户消息
            memoryService.saveMessage(sessionId, "user", userMessage);

            // 3. 从知识库检索相关内容
            String knowledgeContext = knowledgeBaseService.getKnowledgeContext(userId, userMessage);

            // 4. 构建优化的系统提示词
            String systemPrompt = promptService.getSystemPromptWithKnowledge(knowledgeContext);

            // 5. 获取对话上下文(包含历史记忆)
            List<Message> contextMessages = memoryService.getContextMessages(sessionId, systemPrompt);

            // 6. 调用 AI 生成回复
            String assistantResponse = chatClient.prompt()
                    .messages(contextMessages)
                    .call()
                    .content();

            // 7. 保存助手回复
            memoryService.saveMessage(sessionId, "assistant", assistantResponse);

            // 8. 更新会话时间
            session.setIsActive(true);
            sessionRepository.save(session);

            log.info("会话 {} 完成一轮对话", sessionId);

            return assistantResponse;

        } catch (Exception e) {
            log.error("对话出错: sessionId={}", sessionId, e);
            throw new RuntimeException("对话失败: " + e.getMessage(), e);
        }
    }

    /**
     * 流式对话
     */
    public void chatStream(String userId, String sessionId, String userMessage,
                           StreamResponseHandler handler) {
        try {
            // 1. 验证会话
            ConversationSession session = sessionRepository.findBySessionId(sessionId)
                    .orElseThrow(() -> new RuntimeException("会话不存在: " + sessionId));

            if (!session.getUserId().equals(userId)) {
                throw new RuntimeException("会话不属于当前用户");
            }

            // 2. 保存用户消息
            memoryService.saveMessage(sessionId, "user", userMessage);

            // 3. 从知识库检索相关内容
            String knowledgeContext = knowledgeBaseService.getKnowledgeContext(userId, userMessage);

            // 4. 构建优化的系统提示词
            String systemPrompt = promptService.getSystemPromptWithKnowledge(knowledgeContext);

            // 5. 获取对话上下文
            List<Message> contextMessages = memoryService.getContextMessages(sessionId, systemPrompt);

            // 6. 流式调用
            StringBuilder fullResponse = new StringBuilder();
            chatClient.prompt()
                    .messages(contextMessages)
                    .stream()
                    .content()
                    .doOnNext(chunk -> {
                        fullResponse.append(chunk);
                        handler.onChunk(chunk);
                    })
                    .doOnComplete(() -> {
                        // 保存完整回复
                        memoryService.saveMessage(sessionId, "assistant", fullResponse.toString());
                        handler.onComplete(fullResponse.toString());
                    })
                    .doOnError(handler::onError)
                    .subscribe();

        } catch (Exception e) {
            log.error("流式对话出错: sessionId={}", sessionId, e);
            handler.onError(e);
        }
    }

    /**
     * 获取会话信息
     */
    public ConversationSession getSession(String sessionId) {
        return sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("会话不存在: " + sessionId));
    }

    /**
     * 获取用户的所有活跃会话
     */
    public List<ConversationSession> getUserActiveSessions(String userId) {
        return sessionRepository.findByUserIdAndIsActiveTrue(userId);
    }

    /**
     * 结束会话
     */
    @Transactional
    public void endSession(String sessionId) {
        ConversationSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("会话不存在: " + sessionId));

        session.setIsActive(false);
        sessionRepository.save(session);

        log.info("会话已结束: {}", sessionId);
    }

    /**
     * 删除会话
     */
    @Transactional
    public void deleteSession(String sessionId) {
        memoryService.deleteSessionMemory(sessionId);
        sessionRepository.findBySessionId(sessionId)
                .ifPresent(sessionRepository::delete);

        log.info("会话已删除: {}", sessionId);
    }

    /**
     * 流式响应处理器接口
     */
    public interface StreamResponseHandler {
        void onChunk(String chunk);
        void onComplete(String fullResponse);
        void onError(Throwable error);
    }
}
