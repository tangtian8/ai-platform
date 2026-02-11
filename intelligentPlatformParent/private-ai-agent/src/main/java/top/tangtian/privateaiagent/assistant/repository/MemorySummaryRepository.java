package top.tangtian.privateaiagent.assistant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.tangtian.privateaiagent.assistant.entity.MemorySummary;

import java.util.List;

/**
 * @program: ai-platform
 * @description: MemorySummaryRepository
 * @author: tangtian
 * @create: 2026-02-10 17:21
 **/
@Repository
public interface MemorySummaryRepository extends JpaRepository<MemorySummary, Long> {

    List<MemorySummary> findBySessionIdOrderByCreatedAtDesc(String sessionId);

    void deleteBySessionId(String sessionId);
}

