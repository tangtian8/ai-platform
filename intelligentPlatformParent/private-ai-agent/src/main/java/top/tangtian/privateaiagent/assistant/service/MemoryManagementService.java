package top.tangtian.privateaiagent.assistant.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import top.tangtian.privateaiagent.assistant.entity.MemorySummary;
import top.tangtian.privateaiagent.assistant.entity.MessageHistory;
import top.tangtian.privateaiagent.assistant.repository.MemorySummaryRepository;
import top.tangtian.privateaiagent.assistant.repository.MessageHistoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: ai-platform
 * @description: 记忆管理服务
 *  * 实现多层记忆系统:
 *  * 1. 短期记忆: 最近的 N 轮对话
 *  * 2. 长期记忆: 压缩摘要的历史对话
 *  * 3. 知识库: 用户上传的资料
 * @author: tangtian
 * @create: 2026-02-11 10:02
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class MemoryManagementService {

    private final MessageHistoryRepository messageHistoryRepository;
    private final MemorySummaryRepository memorySummaryRepository;
    private final ChatClient chatClient;
    private final PromptService promptService;

    @Value("${agent.memory.short-term-window:10}")
    private int shortTermWindow;

    @Value("${agent.memory.summary-threshold:50}")
    private int summaryThreshold;

    @Value("${agent.memory.max-history:1000}")
    private int maxHistory;

    @Value("${agent.memory.compression.enabled:true}")
    private boolean compressionEnabled;

    @Value("${agent.memory.compression.interval:100}")
    private int compressionInterval;

    /**
     * 保存消息到历史记录
     */
    @Transactional
    public MessageHistory saveMessage(String sessionId, String role, String content) {
        MessageHistory message = MessageHistory.builder()
                .sessionId(sessionId)
                .role(role)
                .content(content)
                .build();

        MessageHistory saved = messageHistoryRepository.save(message);

        // 检查是否需要压缩
        long messageCount = messageHistoryRepository.countBySessionId(sessionId);
        if (compressionEnabled && messageCount > 0 && messageCount % compressionInterval == 0) {
            compressMemory(sessionId);
        }

        return saved;
    }

    /**
     * 获取构建上下文的消息列表
     * 包含: 系统提示 + 长期记忆摘要 + 短期记忆
     */
    public List<Message> getContextMessages(String sessionId, String systemPrompt) {
        List<Message> messages = new ArrayList<>();

        // 1. 添加系统提示
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            messages.add(new SystemMessage(systemPrompt));
        }

        // 2. 添加长期记忆摘要
        List<MemorySummary> summaries = memorySummaryRepository.findBySessionIdOrderByCreatedAtDesc(sessionId);
        if (!summaries.isEmpty()) {
            String summarizedMemory = summaries.stream()
                    .map(MemorySummary::getSummary)
                    .collect(Collectors.joining("\n\n"));
            messages.add(new SystemMessage("历史对话摘要:\n" + summarizedMemory));
        }

        // 3. 添加短期记忆(最近的对话)
        List<MessageHistory> recentMessages = messageHistoryRepository
                .findBySessionIdOrderByCreatedAtDesc(sessionId, PageRequest.of(0, shortTermWindow));

        // 反转顺序,使其按时间正序
        for (int i = recentMessages.size() - 1; i >= 0; i--) {
            MessageHistory msg = recentMessages.get(i);
            messages.add(convertToMessage(msg));
        }

        return messages;
    }

    /**
     * 压缩记忆 - 将旧的对话压缩成摘要
     */
    @Transactional
    public void compressMemory(String sessionId) {
        try {
            log.info("开始压缩会话 {} 的记忆", sessionId);

            // 获取需要压缩的消息(排除最近的短期记忆)
            List<MessageHistory> allMessages = messageHistoryRepository
                    .findBySessionIdOrderByCreatedAtAsc(sessionId);

            if (allMessages.size() <= shortTermWindow) {
                return; // 消息太少,无需压缩
            }

            // 已有的摘要
            List<MemorySummary> existingSummaries = memorySummaryRepository
                    .findBySessionIdOrderByCreatedAtDesc(sessionId);

            Long lastSummarizedId = existingSummaries.isEmpty() ?
                    0L : existingSummaries.get(0).getEndMessageId();

            // 找出需要压缩的消息
            List<MessageHistory> toCompress = allMessages.stream()
                    .filter(msg -> msg.getId() > lastSummarizedId)
                    .limit(allMessages.size() - shortTermWindow)
                    .collect(Collectors.toList());

            if (toCompress.size() < summaryThreshold) {
                return; // 待压缩消息太少
            }

            // 使用 AI 生成摘要
            String conversationText = toCompress.stream()
                    .map(msg -> msg.getRole() + ": " + msg.getContent())
                    .collect(Collectors.joining("\n"));

            String summaryPrompt = promptService.getMemoryCompressionPrompt(conversationText);

            String summary = chatClient.prompt()
                    .user(summaryPrompt)
                    .call()
                    .content();

            // 保存摘要
            MemorySummary memorySummary = MemorySummary.builder()
                    .sessionId(sessionId)
                    .summary(summary)
                    .startMessageId(toCompress.get(0).getId())
                    .endMessageId(toCompress.get(toCompress.size() - 1).getId())
                    .build();

            memorySummaryRepository.save(memorySummary);

            log.info("会话 {} 记忆压缩完成,压缩了 {} 条消息", sessionId, toCompress.size());

        } catch (Exception e) {
            log.error("压缩会话 {} 的记忆时出错", sessionId, e);
        }
    }

    /**
     * 获取会话的消息总数
     */
    public long getMessageCount(String sessionId) {
        return messageHistoryRepository.countBySessionId(sessionId);
    }

    /**
     * 清理旧会话(保留最近的 maxHistory 条)
     */
    @Transactional
    public void cleanupOldMessages(String sessionId) {
        long count = messageHistoryRepository.countBySessionId(sessionId);
        if (count > maxHistory) {
            // 这里可以实现删除最旧的消息,保留最近的
            log.info("会话 {} 的消息数 {} 超过限制 {},需要清理", sessionId, count, maxHistory);
            // 实现清理逻辑...
        }
    }

    /**
     * 转换 MessageHistory 到 Spring AI Message
     */
    private Message convertToMessage(MessageHistory msg) {
        return switch (msg.getRole().toLowerCase()) {
            case "user" -> new UserMessage(msg.getContent());
            case "assistant" -> new AssistantMessage(msg.getContent());
            case "system" -> new SystemMessage(msg.getContent());
            default -> new UserMessage(msg.getContent());
        };
    }

    /**
     * 删除会话的所有记忆
     */
    @Transactional
    public void deleteSessionMemory(String sessionId) {
        messageHistoryRepository.deleteBySessionId(sessionId);
        memorySummaryRepository.deleteBySessionId(sessionId);
        log.info("已删除会话 {} 的所有记忆", sessionId);
    }
}

