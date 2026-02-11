package top.tangtian.privateaiagent.assistant.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * @program: ai-platform
 * @description: MessageHistory
 * @author: tangtian
 * @create: 2026-02-10 17:15
 **/
@Entity
@Table(name = "message_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "role", nullable = false, length = 50)
    private String role;  // user, assistant, system

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "token_count")
    private Integer tokenCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private JsonNode metadata;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

