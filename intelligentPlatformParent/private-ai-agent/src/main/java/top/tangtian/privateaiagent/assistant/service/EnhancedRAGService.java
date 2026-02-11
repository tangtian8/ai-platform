package top.tangtian.privateaiagent.assistant.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import top.tangtian.privateaiagent.assistant.entity.UserKnowledge;
import top.tangtian.privateaiagent.assistant.service.vo.RerankedDocument;
import top.tangtian.privateaiagent.assistant.service.vo.RetrievalResult;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: ai-platform
 * @description: RAG服务
 * @author: tangtian
 * @create: 2026-02-11 09:36
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class EnhancedRAGService {

    private final KnowledgeBaseService knowledgeBaseService;
    private final ChatClient chatClient;
    private final PromptService promptService;

    @Value("${agent.retrieval.top-k:5}")
    private int topK;

    @Value("${agent.retrieval.rerank:true}")
    private boolean enableRerank;

    @Value("${agent.retrieval.query-expansion:true}")
    private boolean enableQueryExpansion;

    /**
     * 增强的知识检索
     * 包含: 查询扩展、重排序、去重等
     */
    public RetrievalResult retrieveEnhanced(String userId, String query) {
        try {
            // 1. 查询扩展 (可选)
            String expandedQuery = enableQueryExpansion ? expandQuery(query) : query;
            log.debug("原始查询: {} | 扩展查询: {}", query, expandedQuery);

            // 2. 向量检索
            List<UserKnowledge> rawResults = knowledgeBaseService.retrieveRelevantKnowledge(
                    userId,
                    expandedQuery
            );

            if (rawResults.isEmpty()) {
                return RetrievalResult.empty();
            }

            // 3. 重排序 (可选)
            List<RerankedDocument> rerankedDocs = enableRerank ?
                    rerankDocuments(query, rawResults) :
                    rawResults.stream()
                            .map(doc -> new RerankedDocument(doc, 1.0))
                            .collect(Collectors.toList());

            // 4. 去重和过滤
            List<RerankedDocument> filteredDocs = deduplicateAndFilter(rerankedDocs);

            // 5. 构建上下文
            String context = buildContext(filteredDocs);

            return new RetrievalResult(
                    filteredDocs.stream()
                            .map(RerankedDocument::getDocument)
                            .collect(Collectors.toList()),
                    context,
                    true
            );

        } catch (Exception e) {
            log.error("增强检索失败: userId={}, query={}", userId, query, e);
            return RetrievalResult.empty();
        }
    }

    /**
     * 查询扩展
     * 使用 LLM 扩展查询，提高召回率
     */
    private String expandQuery(String query) {
        try {
            String prompt = promptService.getQueryOptimizationPrompt(query);
            String expandedQuery = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content()
                    .trim();

            return expandedQuery.isEmpty() ? query : expandedQuery;
        } catch (Exception e) {
            log.warn("查询扩展失败，使用原始查询: {}", e.getMessage());
            return query;
        }
    }

    /**
     * 重排序文档
     * 使用语义相似度对检索结果重新排序
     */
    private List<RerankedDocument> rerankDocuments(String query, List<UserKnowledge> documents) {
        // 简化版本: 基于标题和内容的关键词匹配
        return documents.stream()
                .map(doc -> {
                    double score = calculateRelevanceScore(query, doc);
                    return new RerankedDocument(doc, score);
                })
                .sorted(Comparator.comparingDouble(RerankedDocument::getScore).reversed())
                .limit(topK)
                .collect(Collectors.toList());
    }

    /**
     * 计算相关性分数
     */
    private double calculateRelevanceScore(String query, UserKnowledge doc) {
        String queryLower = query.toLowerCase();
        String content = (doc.getTitle() + " " + doc.getContent()).toLowerCase();

        // 简单的关键词匹配评分
        String[] queryWords = queryLower.split("\\s+");
        long matchCount = Arrays.stream(queryWords)
                .filter(content::contains)
                .count();

        return (double) matchCount / queryWords.length;
    }

    /**
     * 去重和过滤
     */
    private List<RerankedDocument> deduplicateAndFilter(List<RerankedDocument> documents) {
        // 基于内容相似度去重
        Set<String> seenContent = new HashSet<>();
        List<RerankedDocument> filtered = new ArrayList<>();

        for (RerankedDocument doc : documents) {
            String contentSignature = getContentSignature(doc.getDocument().getContent());
            if (!seenContent.contains(contentSignature)) {
                seenContent.add(contentSignature);
                filtered.add(doc);
            }
        }

        return filtered;
    }

    /**
     * 获取内容签名 (用于去重)
     */
    private String getContentSignature(String content) {
        // 简化版: 取前100个字符作为签名
        return content.length() > 100 ?
                content.substring(0, 100) : content;
    }

    /**
     * 构建格式化的上下文
     */
    private String buildContext(List<RerankedDocument> documents) {
        if (documents.isEmpty()) {
            return "";
        }

        StringBuilder context = new StringBuilder();

        for (int i = 0; i < documents.size(); i++) {
            RerankedDocument doc = documents.get(i);
            UserKnowledge knowledge = doc.getDocument();

            context.append(String.format("""
                    ### 文档 %d: %s
                    **来源**: %s
                    **分类**: %s
                    **相关度**: %.2f
                    
                    **内容**:
                    %s
                    
                    ---
                    
                    """,
                    i + 1,
                    knowledge.getTitle() != null ? knowledge.getTitle() : "未命名",
                    knowledge.getSource() != null ? knowledge.getSource() : "用户上传",
                    knowledge.getCategory() != null ? knowledge.getCategory() : "未分类",
                    doc.getScore(),
                    knowledge.getContent()
            ));
        }

        return context.toString();
    }
}

