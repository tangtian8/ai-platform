package top.tangtian.privateaiagent.assistant.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: ai-platform
 * @description: 提示词管理服务
 *  * 提供优化的、场景化的提示词模板
 * @author: tangtian
 * @create: 2026-02-11 10:05
 **/
@Slf4j
@Service
public class PromptService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");

    /**
     * 核心系统提示词
     * 定义助手的角色、能力和行为准则
     */
    public String getSystemPrompt() {
        String currentTime = LocalDateTime.now().format(DATE_FORMATTER);

        return String.format("""
            # 角色定义
            你是一个高度个性化的 AI 智能助手，拥有以下核心能力：
            
            ## 核心能力
            1. **长期记忆**: 能够记住与用户的所有对话历史，即使跨越数百轮对话
            2. **个人知识库**: 能够访问和理解用户上传的个人资料、文档和笔记
            3. **上下文理解**: 深度理解对话上下文，提供连贯、相关的回答
            4. **精准引用**: 在引用知识库内容时，会明确标注来源
            
            ## 行为准则
            1. **个性化**: 基于用户的历史对话和个人资料，提供量身定制的回答
            2. **准确性**: 
               - 引用知识库内容时，必须准确并标注来源
               - 不确定时，诚实地表达不确定性
               - 避免编造不存在的信息
            3. **连贯性**: 保持对话的连贯性，记住之前讨论的内容
            4. **有用性**: 
               - 主动提供有价值的建议和洞察
               - 在适当时候提出澄清问题
               - 提供具体、可操作的回答
            5. **友好专业**: 保持友好、专业的交流风格
            
            ## 回答格式要求
            1. **引用格式**: 当引用知识库内容时，使用以下格式：
               > 📚 根据您的资料《[文档标题]》: [引用内容]
            
            2. **不确定表达**: 当不确定时，使用以下表达：
               - "根据我的理解..."
               - "从您之前提供的信息来看..."
               - "我不太确定，但..."
            
            3. **结构化回答**: 对于复杂问题，使用清晰的结构：
               - 简明扼要的总结
               - 分点详细说明
               - 必要时提供示例
            
            ## 当前上下文
            - 当前时间: %s
            - 对话模式: 长期记忆模式（支持 1000+ 轮对话）
            
            ## 特别提示
            - 你拥有用户的完整对话历史和个人知识库访问权限
            - 充分利用这些信息，提供真正个性化的帮助
            - 在回答中体现对用户的了解和关注
            """, currentTime);
    }

    /**
     * 带知识库上下文的系统提示词
     */
    public String getSystemPromptWithKnowledge(String knowledgeContext) {
        if (knowledgeContext == null || knowledgeContext.trim().isEmpty()) {
            return getSystemPrompt();
        }

        return getSystemPrompt() + "\n\n" + formatKnowledgeContext(knowledgeContext);
    }

    /**
     * 格式化知识库上下文
     */
    private String formatKnowledgeContext(String knowledgeContext) {
        return String.format("""
            ## 📚 相关知识库资料
            
            以下是从用户个人知识库中检索到的相关资料，请在回答中充分利用这些信息：
            
            %s
            
            **重要提示**:
            - 引用以上资料时，请明确标注来源文档
            - 这些是用户的个人资料，具有高度相关性和权威性
            - 优先使用知识库内容，其次才是你的通用知识
            """, knowledgeContext);
    }

    /**
     * 记忆压缩提示词
     * 用于生成对话摘要
     */
    public String getMemoryCompressionPrompt(String conversationHistory) {
        return String.format("""
            # 任务: 对话记忆压缩
            
            请将以下对话历史压缩成简洁的摘要，要求：
            
            ## 压缩原则
            1. **保留关键信息**:
               - 用户的个人信息（姓名、偏好、背景等）
               - 重要的事实和数据
               - 关键的决策和结论
               - 用户明确表达的需求和目标
            
            2. **删除冗余内容**:
               - 寒暄和礼貌用语
               - 重复的信息
               - 不重要的细节
            
            3. **结构化组织**:
               - 按主题分类
               - 使用简洁的语言
               - 保持时间顺序（如果重要）
            
            ## 输出格式
            以第三人称视角，用 200-300 字总结对话要点。
            
            ## 对话历史
            ```
            %s
            ```
            
            ## 摘要（请直接输出摘要内容，不要有任何前缀或解释）:
            """, conversationHistory);
    }

    /**
     * RAG 检索优化提示词
     * 用于优化用户查询，提高检索效果
     */
    public String getQueryOptimizationPrompt(String userQuery) {
        return String.format("""
            # 任务: 查询优化
            
            用户查询: "%s"
            
            请将上述查询改写为更适合向量检索的形式：
            
            ## 优化要求
            1. 提取核心关键词
            2. 扩展相关同义词
            3. 添加语义上下文
            4. 去除冗余词汇
            
            ## 输出格式
            只输出优化后的查询文本，不要有任何解释。
            
            优化查询:
            """, userQuery);
    }

    /**
     * 文档总结提示词
     * 用于总结上传的文档
     */
    public String getDocumentSummaryPrompt(String documentContent, String documentTitle) {
        return String.format("""
            # 任务: 文档摘要生成
            
            文档标题: %s
            
            请为以下文档生成一个结构化的摘要：
            
            ## 摘要要求
            1. **主题提取**: 用一句话概括文档主题
            2. **关键要点**: 列出 3-5 个关键要点
            3. **重要信息**: 提取关键数据、名称、日期等
            4. **应用场景**: 说明这份资料可能在哪些场景下有用
            
            ## 文档内容
            ```
            %s
            ```
            
            ## 摘要（使用结构化格式输出）:
            """, documentTitle, documentContent);
    }

    /**
     * 对话意图识别提示词
     */
    public String getIntentRecognitionPrompt(String userMessage) {
        return String.format("""
            # 任务: 识别用户意图
            
            用户消息: "%s"
            
            请识别用户的主要意图类型，从以下选项中选择一个：
            
            1. QUESTION - 提问或寻求信息
            2. TASK - 请求执行任务或操作
            3. CHAT - 闲聊或社交对话
            4. FEEDBACK - 提供反馈或评价
            5. CLARIFICATION - 澄清或补充说明
            6. KNOWLEDGE_ADD - 添加或更新知识
            
            只输出意图类型（如: QUESTION），不要有任何解释。
            
            意图:
            """, userMessage);
    }

    /**
     * 生成对话开场白
     */
    public String getWelcomeMessage(String userName, boolean hasKnowledge, int conversationCount) {
        if (userName != null && !userName.isEmpty()) {
            if (hasKnowledge) {
                return String.format("""
                    你好 %s！👋
                    
                    我已经准备好了，可以访问您的个人知识库和我们之前的对话历史。
                    无论是查找资料、回顾讨论，还是开启新的话题，我都随时为您服务！
                    
                    %s
                    """, userName,
                        conversationCount > 0 ?
                                String.format("我们已经进行了 %d 轮对话，我会记住所有重要的内容。", conversationCount) :
                                "这是我们的第一次对话，我期待了解更多关于您的信息！");
            }
        }

        return """
            你好！我是你的个人 AI 助手。
            
            我的特点：
            • 📝 拥有长期记忆，能记住我们的所有对话
            • 📚 可以访问你上传的个人知识库
            • 🎯 提供个性化的、针对性的帮助
            
            请随时告诉我你需要什么帮助！
            """;
    }

    /**
     * 生成知识库建议
     */
    public String getKnowledgeBaseSuggestion(List<String> categories) {
        if (categories.isEmpty()) {
            return """
                💡 **知识库建议**
                
                您的知识库目前是空的。建议上传以下类型的资料：
                • 个人简历和工作经历
                • 学习笔记和技术文档
                • 项目资料和总结
                • 常用的参考资料
                
                这样我就能更好地为您提供个性化的帮助！
                """;
        }

        String categoryList = categories.stream()
                .map(c -> "• " + c)
                .collect(Collectors.joining("\n"));

        return String.format("""
            📚 **您的知识库**
            
            当前已有以下分类的资料：
            %s
            
            我会在回答问题时充分利用这些资料。
            """, categoryList);
    }

    /**
     * 错误处理提示词
     */
    public String getErrorHandlingPrompt(String errorType) {
        return switch (errorType.toUpperCase()) {
            case "NO_KNOWLEDGE_FOUND" -> """
                在您的知识库中没有找到相关资料。
                
                我会基于通用知识来回答您的问题。如果您有相关资料，
                可以上传到知识库中，这样我就能提供更准确的答案。
                """;

            case "CONTEXT_TOO_LONG" -> """
                对话历史较长，我会重点关注最近的讨论内容。
                
                如果需要回顾更早的对话，请明确告诉我。
                """;

            case "MEMORY_COMPRESSION" -> """
                为了保持高效运行，我已经压缩了部分历史对话。
                
                重要信息都已保留在摘要中，不会影响我对您的了解。
                """;

            default -> "抱歉，遇到了一些问题。让我们继续吧！";
        };
    }

    /**
     * 多轮对话引导
     */
    public String getFollowUpPrompt(String topic) {
        return String.format("""
            关于 "%s" 这个话题，您还想了解：
            
            1. 更多细节和背景信息
            2. 相关的实践案例
            3. 具体的应用方法
            4. 其他相关主题
            
            或者您有其他问题？
            """, topic);
    }
}
