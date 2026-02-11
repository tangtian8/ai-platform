package top.tangtian.privateaiagent.assistant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @program: ai-platform
 * @description: MemorySummary
 * @author: tangtian
 * @create: 2026-02-10 17:14
 **/
@Entity
@Table(name = "memory_summaries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemorySummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "summary", nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(name = "start_message_id")
    private Long startMessageId;

    @Column(name = "end_message_id")
    private Long endMessageId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

