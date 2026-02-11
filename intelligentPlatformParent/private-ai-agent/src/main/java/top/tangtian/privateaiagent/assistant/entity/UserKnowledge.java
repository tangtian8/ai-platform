package top.tangtian.privateaiagent.assistant.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.pgvector.PGvector;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @program: ai-platform
 * @description: UserKnowledge
 * @author: tangtian
 * @create: 2026-02-10 17:16
 **/
@Entity
@Table(name = "user_knowledge")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserKnowledge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "title", length = 500)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "source")
    private String source;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "embedding", columnDefinition = "vector(384)")
    private PGvector embedding;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private JsonNode metadata;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
