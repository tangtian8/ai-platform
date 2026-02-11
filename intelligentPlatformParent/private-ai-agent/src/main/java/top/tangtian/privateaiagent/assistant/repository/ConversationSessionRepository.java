package top.tangtian.privateaiagent.assistant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.tangtian.privateaiagent.assistant.entity.ConversationSession;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @program: ai-platform
 * @description: ConversationSessionRepository
 * @author: tangtian
 * @create: 2026-02-10 17:19
 **/
@Repository
public interface ConversationSessionRepository extends JpaRepository<ConversationSession, UUID> {

    Optional<ConversationSession> findBySessionId(String sessionId);

    List<ConversationSession> findByUserIdAndIsActiveTrue(String userId);

    List<ConversationSession> findByUserId(String userId);
}
