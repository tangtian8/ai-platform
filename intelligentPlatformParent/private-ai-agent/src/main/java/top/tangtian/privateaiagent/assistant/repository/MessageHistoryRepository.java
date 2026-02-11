package top.tangtian.privateaiagent.assistant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import top.tangtian.privateaiagent.assistant.entity.MessageHistory;

import java.util.List;

/**
 * @program: ai-platform
 * @description: MessageHistoryRepository
 * @author: tangtian
 * @create: 2026-02-10 17:21
 **/
@Repository
public interface MessageHistoryRepository extends JpaRepository<MessageHistory, Long> {

    List<MessageHistory> findBySessionIdOrderByCreatedAtDesc(String sessionId, Pageable pageable);

    List<MessageHistory> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    @Query("SELECT COUNT(m) FROM MessageHistory m WHERE m.sessionId = :sessionId")
    long countBySessionId(String sessionId);

    @Query("SELECT m FROM MessageHistory m WHERE m.sessionId = :sessionId AND m.id > :afterId ORDER BY m.createdAt ASC")
    List<MessageHistory> findBySessionIdAfterMessageId(String sessionId, Long afterId);

    void deleteBySessionId(String sessionId);
}
