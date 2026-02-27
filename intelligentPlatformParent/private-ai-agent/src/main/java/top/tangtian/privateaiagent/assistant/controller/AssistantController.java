package top.tangtian.privateaiagent.assistant.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import top.tangtian.privateaiagent.assistant.controller.param.*;
import top.tangtian.privateaiagent.assistant.entity.ConversationSession;
import top.tangtian.privateaiagent.assistant.entity.UserKnowledge;
import top.tangtian.privateaiagent.assistant.service.ConversationService;
import top.tangtian.privateaiagent.assistant.service.KnowledgeBaseService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: ai-platform
 * @description: AI 助手 REST API 控制器
 * @author: tangtian
 * @create: 2026-02-11 16:38
 **/
@Slf4j
@RestController
@RequestMapping("/api/v1/assistant")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AssistantController {

    private final ConversationService conversationService;
    private final KnowledgeBaseService knowledgeBaseService;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    // ==================== 会话管理 ====================

    /**
     * 创建新会话
     */
    @PostMapping("/sessions")
    public ResponseEntity<SessionResponse> createSession(@RequestBody CreateSessionRequest request) {
        String sessionId = conversationService.createSession(request.getUserId());
        return ResponseEntity.ok(new SessionResponse(sessionId, "会话创建成功"));
    }

    /**
     * 获取用户的所有活跃会话
     */
    @GetMapping("/sessions/{userId}")
    public ResponseEntity<List<ConversationSession>> getUserSessions(@PathVariable String userId) {
        List<ConversationSession> sessions = conversationService.getUserActiveSessions(userId);
        return ResponseEntity.ok(sessions);
    }

    /**
     * 结束会话
     */
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<String> endSession(@PathVariable String sessionId) {
        conversationService.endSession(sessionId);
        return ResponseEntity.ok("会话已结束");
    }

    // ==================== 对话功能 ====================

    /**
     * 发送消息(同步)
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            String response = conversationService.chat(
                    request.getUserId(),
                    request.getSessionId(),
                    request.getMessage()
            );

            return ResponseEntity.ok(new ChatResponse(response, true, null));

        } catch (Exception e) {
            log.error("对话失败", e);
            return ResponseEntity.ok(new ChatResponse(null, false, e.getMessage()));
        }
    }

    /**
     * 发送消息(流式)
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(@RequestBody ChatRequest request) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        executorService.execute(() -> {
            try {
                conversationService.chatStream(
                        request.getUserId(),
                        request.getSessionId(),
                        request.getMessage(),
                        new ConversationService.StreamResponseHandler() {
                            @Override
                            public void onChunk(String chunk) {
                                try {
                                    emitter.send(SseEmitter.event()
                                            .name("message")
                                            .data(chunk));
                                } catch (IOException e) {
                                    log.error("发送流式数据失败", e);
                                    emitter.completeWithError(e);
                                }
                            }

                            @Override
                            public void onComplete(String fullResponse) {
                                try {
                                    emitter.send(SseEmitter.event()
                                            .name("done")
                                            .data("[DONE]"));
                                    emitter.complete();
                                } catch (IOException e) {
                                    log.error("完成流式响应失败", e);
                                }
                            }

                            @Override
                            public void onError(Throwable error) {
                                log.error("流式对话出错", error);
                                emitter.completeWithError(error);
                            }
                        }
                );
            } catch (Exception e) {
                log.error("流式对话异常", e);
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    // ==================== 知识库管理 ====================

    /**
     * 添加文本知识
     */
    @PostMapping("/knowledge/text")
    public ResponseEntity<String> addTextKnowledge(@RequestBody AddKnowledgeRequest request) {
        try {
            knowledgeBaseService.addKnowledge(
                    request.getUserId(),
                    request.getTitle(),
                    new Document(request.getContent()),
                    request.getSource(),
                    request.getCategory(),
                    request.getMetadata()
            );
            return ResponseEntity.ok("知识添加成功");
        } catch (Exception e) {
            log.error("添加知识失败", e);
            return ResponseEntity.badRequest().body("添加知识失败: " + e.getMessage());
        }
    }

    /**
     * 批量添加知识
     */
    @PostMapping("/knowledge/batch")
    public ResponseEntity<String> addKnowledgeBatch(@RequestBody BatchKnowledgeRequest request) {
        try {
            knowledgeBaseService.addKnowledgeBatch(request.getUserId(), request.getKnowledgeList());
            return ResponseEntity.ok("批量添加成功");
        } catch (Exception e) {
            log.error("批量添加知识失败", e);
            return ResponseEntity.badRequest().body("批量添加失败: " + e.getMessage());
        }
    }

    /**
     * 上传文件到知识库
     */
    @PostMapping("/knowledge/file")
    public ResponseEntity<String> uploadKnowledgeFile(
            @RequestParam("userId") String userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", required = false) String category) {
        try {
            knowledgeBaseService.addKnowledgeFromFile(
                    userId,
                    file.getResource(),
                    category,
                    Map.of("filename", file.getOriginalFilename())
            );

            return ResponseEntity.ok("文件上传成功");
        } catch (Exception e) {
            log.error("上传文件失败", e);
            return ResponseEntity.badRequest().body("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户知识库列表
     */
    @GetMapping("/knowledge/{userId}")
    public ResponseEntity<List<UserKnowledge>> getUserKnowledge(@PathVariable String userId) {
        List<UserKnowledge> knowledge = knowledgeBaseService.getUserKnowledge(userId);
        return ResponseEntity.ok(knowledge);
    }


    /**
     * 按类别获取知识
     */
    @GetMapping("/knowledge/{userId}/category/{category}")
    public ResponseEntity<List<UserKnowledge>> getUserKnowledgeByCategory(
            @PathVariable String userId,
            @PathVariable String category) {
        List<UserKnowledge> knowledge = knowledgeBaseService.getUserKnowledgeByCategory(userId, category);
        return ResponseEntity.ok(knowledge);
    }

    /**
     * 删除知识
     */
    @DeleteMapping("/knowledge/{userId}/{knowledgeId}")
    public ResponseEntity<String> deleteKnowledge(
            @PathVariable String userId,
            @PathVariable String knowledgeId) {
        try {
            knowledgeBaseService.deleteKnowledge(userId, knowledgeId);
            return ResponseEntity.ok("知识删除成功");
        } catch (Exception e) {
            log.error("删除知识失败", e);
            return ResponseEntity.badRequest().body("删除失败: " + e.getMessage());
        }
    }

    /**
     * 清空用户知识库
     */
    @DeleteMapping("/knowledge/{userId}/clear")
    public ResponseEntity<String> clearUserKnowledge(@PathVariable String userId) {
        try {
            knowledgeBaseService.clearUserKnowledge(userId);
            return ResponseEntity.ok("知识库已清空");
        } catch (Exception e) {
            log.error("清空知识库失败", e);
            return ResponseEntity.badRequest().body("清空失败: " + e.getMessage());
        }
    }

}
