package top.tangtian.privateaiagent.assistant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import top.tangtian.privateaiagent.assistant.entity.UserKnowledge;

import java.util.List;
import java.util.UUID;

/**
 * @program: ai-platform
 * @description: UserKnowledgeRepository
 * @author: tangtian
 * @create: 2026-02-10 17:22
 **/
@Repository
public interface UserKnowledgeRepository extends JpaRepository<UserKnowledge, UUID> {

    List<UserKnowledge> findByUserId(String userId);

    List<UserKnowledge> findByUserIdAndCategory(String userId, String category);

    @Query(value = "SELECT * FROM user_knowledge " +
            "WHERE user_id = :userId " +
            "ORDER BY embedding <=> CAST(:embedding AS vector) " +
            "LIMIT :limit", nativeQuery = true)
    List<UserKnowledge> findSimilarDocuments(
            @Param("userId") String userId,
            @Param("embedding") String embedding,
            @Param("limit") int limitspring

    );

    @Query(value = "SELECT * FROM user_knowledge " +
            "WHERE user_id = :userId " +
            "AND 1 - (embedding <=> CAST(:embedding AS vector)) > :threshold " +
            "ORDER BY embedding <=> CAST(:embedding AS vector) " +
            "LIMIT :limit", nativeQuery = true)
    List<UserKnowledge> findSimilarDocumentsAboveThreshold(
            @Param("userId") String userId,
            @Param("embedding") String embedding,
            @Param("threshold") double threshold,
            @Param("limit") int limit
    );
}
