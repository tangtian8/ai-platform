package top.tangtian.privateaiagent.assistant.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import top.tangtian.privateaiagent.assistant.entity.UserKnowledge;
import top.tangtian.privateaiagent.assistant.repository.UserKnowledgeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: ai-platform
 * @description: 知识库管理服务
 * @author: tangtian
 * @create: 2026-02-11 09:49
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeBaseService {

    private final UserKnowledgeRepository userKnowledgeRepository;
    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;

    @Value("${agent.retrieval.top-k:5}")
    private int topK;

    @Value("${agent.retrieval.similarity-threshold:0.7}")
    private double similarityThreshold;

    /**
     * 添加文本知识
     */
    @Transactional
    public void addKnowledge(String userId, String title, Document content,
                             String source, String category, Map<String, Object> metadata) {
        try {
            // 分割长文本
            TokenTextSplitter splitter = new TokenTextSplitter(500, 100, 5, 1000, true);
            List<Document> chunks = splitter.split(content);

            for (int i = 0; i < chunks.size(); i++) {
                Document chunk = chunks.get(i);

                // 生成 embedding
                float[] embedding = embeddingModel.embed(chunk);

                // 保存到数据库
                UserKnowledge knowledge = UserKnowledge.builder()
                        .userId(userId)
                        .title(title + (chunks.size() > 1 ? " (part " + (i + 1) + ")" : ""))
                        .content(chunk.getFormattedContent())
                        .source(source)
                        .category(category)
                        .embedding(new PGvector(embedding))
                        .build();

                userKnowledgeRepository.save(knowledge);

                // 同时添加到向量存储
                Document doc = new Document(chunk.getFormattedContent(), Map.of(
                        "userId", userId,
                        "title", title,
                        "source", source != null ? source : "",
                        "category", category != null ? category : ""
                ));
                vectorStore.add(List.of(doc));
            }

            log.info("用户 {} 添加知识: {} (分割为 {} 块)", userId, title, chunks.size());

        } catch (Exception e) {
            log.error("添加知识时出错: userId={}, title={}", userId, title, e);
            throw new RuntimeException("添加知识失败", e);
        }
    }

    /**
     * 从文件添加知识
     */
    @Transactional
    public void addKnowledgeFromFile(String userId, Resource resource,
                                     String category, Map<String, Object> metadata) {
        try {
            TextReader textReader = new TextReader(resource);
            List<Document> documents = textReader.get();

            for (Document doc : documents) {
                addKnowledge(userId,
                        resource.getFilename(),
                        doc,
                        resource.getFilename(),
                        category,
                        metadata);
            }

            log.info("用户 {} 从文件添加知识: {}", userId, resource.getFilename());

        } catch (Exception e) {
            log.error("从文件添加知识时出错: userId={}, file={}", userId, resource.getFilename(), e);
            throw new RuntimeException("从文件添加知识失败", e);
        }
    }

    /**
     * 批量添加知识
     */
    @Transactional
    public void addKnowledgeBatch(String userId, List<Map<String, String>> knowledgeList) {
        for (Map<String, String> item : knowledgeList) {
            addKnowledge(
                    userId,
                    item.get("title"),
                    new Document(item.get("content")),
                    item.get("source"),
                    item.get("category"),
                    null
            );
        }
    }

    /**
     * 检索相关知识
     */
    public List<UserKnowledge> retrieveRelevantKnowledge(String userId, String query) {
        try {
            // 生成查询的 embedding
            float[] queryEmbedding = embeddingModel.embed(query);
            String embeddingStr = vectorToString(queryEmbedding);

            // 从数据库检索相似文档
            List<UserKnowledge> results = userKnowledgeRepository
                    .findSimilarDocumentsAboveThreshold(userId, embeddingStr, similarityThreshold, topK);

            log.debug("为用户 {} 检索到 {} 条相关知识", userId, results.size());
            return results;

        } catch (Exception e) {
            log.error("检索知识时出错: userId={}, query={}", userId, query, e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取知识库上下文文本
     */
    public String getKnowledgeContext(String userId, String query) {
        List<UserKnowledge> relevantDocs = retrieveRelevantKnowledge(userId, query);

        if (relevantDocs.isEmpty()) {
            return "";
        }

        return relevantDocs.stream()
                .map(doc -> String.format("[来源: %s]\n%s",
                        doc.getTitle() != null ? doc.getTitle() : "未知",
                        doc.getContent()))
                .collect(Collectors.joining("\n\n---\n\n"));
    }

    /**
     * 获取用户所有知识
     */
    public List<UserKnowledge> getUserKnowledge(String userId) {
        return userKnowledgeRepository.findByUserId(userId);
    }

    /**
     * 按类别获取知识
     */
    public List<UserKnowledge> getUserKnowledgeByCategory(String userId, String category) {
        return userKnowledgeRepository.findByUserIdAndCategory(userId, category);
    }

    /**
     * 删除知识
     */
    @Transactional
    public void deleteKnowledge(String userId, String knowledgeId) {
        userKnowledgeRepository.deleteById(java.util.UUID.fromString(knowledgeId));
        log.info("用户 {} 删除知识: {}", userId, knowledgeId);
    }

    /**
     * 清空用户知识库
     */
    @Transactional
    public void clearUserKnowledge(String userId) {
        List<UserKnowledge> allKnowledge = userKnowledgeRepository.findByUserId(userId);
        userKnowledgeRepository.deleteAll(allKnowledge);
        log.info("清空用户 {} 的知识库,共删除 {} 条", userId, allKnowledge.size());
    }

    /**
     * 将向量转换为 PostgreSQL 格式字符串
     */
    private String vectorToString(float[] vector) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vector.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(vector[i]);
        }
        sb.append("]");
        return sb.toString();
    }
}