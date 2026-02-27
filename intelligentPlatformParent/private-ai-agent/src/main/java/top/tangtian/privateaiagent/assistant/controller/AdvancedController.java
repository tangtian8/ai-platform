package top.tangtian.privateaiagent.assistant.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.tangtian.privateaiagent.assistant.chain.ToolChain;
import top.tangtian.privateaiagent.assistant.chain.ToolChainEngine;
import top.tangtian.privateaiagent.assistant.controller.param.CreatePromptChainRequest;
import top.tangtian.privateaiagent.assistant.controller.param.CreatePromptRequest;
import top.tangtian.privateaiagent.assistant.controller.param.WarmUpRequest;
import top.tangtian.privateaiagent.assistant.factory.PromptFactory;
import top.tangtian.privateaiagent.assistant.mcp.MCPLifecycleManager;
import top.tangtian.privateaiagent.assistant.mcp.ModelInfo;
import top.tangtian.privateaiagent.assistant.service.AdvancedConversationService;
import top.tangtian.privateaiagent.assistant.service.vo.AdvancedChatRequest;
import top.tangtian.privateaiagent.assistant.service.vo.AdvancedChatResponse;
import top.tangtian.privateaiagent.assistant.tools.ToolDefinition;
import top.tangtian.privateaiagent.assistant.tools.ToolRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: ai-platform
 * @description: 高级功能 API 控制器
 * @author: tangtian
 * @create: 2026-02-11 16:33
 **/
@Slf4j
@RestController
@RequestMapping("/api/v2/advanced")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdvancedController {

    private final AdvancedConversationService advancedService;
    private final ToolRegistry toolRegistry;
    private final ToolChainEngine chainEngine;
    private final MCPLifecycleManager mcpManager;
    private final PromptFactory promptFactory;

    // ==================== 高级对话 ====================

    /**
     * 高级对话（支持工具调用）
     */
    @PostMapping("/chat")
    public ResponseEntity<AdvancedChatResponse> advancedChat(@RequestBody AdvancedChatRequest request) {
        try {
            AdvancedChatResponse response = advancedService.advancedChat(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("高级对话失败", e);
            return ResponseEntity.ok(AdvancedChatResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build());
        }
    }

    // ==================== 工具管理 ====================

    /**
     * 获取所有工具列表
     */
    @GetMapping("/tools")
    public ResponseEntity<List<ToolDefinition>> getAllTools() {
        return ResponseEntity.ok(new ArrayList<>(toolRegistry.getAllTools()));
    }

    /**
     * 按分类获取工具
     */
    @GetMapping("/tools/category/{category}")
    public ResponseEntity<List<ToolDefinition>> getToolsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(toolRegistry.getToolsByCategory(category));
    }

    /**
     * 搜索工具
     */
    @GetMapping("/tools/search")
    public ResponseEntity<List<ToolDefinition>> searchTools(@RequestParam String keyword) {
        return ResponseEntity.ok(toolRegistry.searchTools(keyword));
    }

    /**
     * 获取工具详情
     */
    @GetMapping("/tools/{toolName}")
    public ResponseEntity<ToolDefinition> getToolDetail(@PathVariable String toolName) {
        ToolDefinition tool = toolRegistry.getTool(toolName);
        if (tool == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tool);
    }

    /**
     * 获取工具统计
     */
    @GetMapping("/tools/statistics")
    public ResponseEntity<Map<String, Object>> getToolStatistics() {
        return ResponseEntity.ok(toolRegistry.getStatistics());
    }

    // ==================== 工具链管理 ====================

    /**
     * 执行工具链
     */
    @PostMapping("/chain/execute")
    public ResponseEntity<Object> executeChain(@RequestBody ToolChain chain) {
        try {
            var result = chainEngine.executeChain(chain);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("工具链执行失败", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * 并行执行工具链
     */
    @PostMapping("/chain/execute-parallel")
    public ResponseEntity<Object> executeChainParallel(@RequestBody ToolChain chain) {
        try {
            var result = chainEngine.executeChainParallel(chain);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("并行工具链执行失败", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    // ==================== MCP 模型管理 ====================

    /**
     * 获取所有模型信息
     */
    @GetMapping("/models")
    public ResponseEntity<List<ModelInfo>> getAllModels() {
        return ResponseEntity.ok(mcpManager.getAllModelsInfo());
    }

    /**
     * 获取模型详情
     */
    @GetMapping("/models/{modelId}")
    public ResponseEntity<ModelInfo> getModelInfo(@PathVariable String modelId) {
        return ResponseEntity.ok(mcpManager.getModelInfo(modelId));
    }

    /**
     * 预热模型
     */
    @PostMapping("/models/warmup")
    public ResponseEntity<String> warmUpModel(@RequestBody WarmUpRequest request) {
        try {
            mcpManager.warmUpModel(request.getModelId(), request.getModelType());
            return ResponseEntity.ok("模型预热成功");
        } catch (Exception e) {
            log.error("模型预热失败", e);
            return ResponseEntity.badRequest().body("预热失败: " + e.getMessage());
        }
    }

    /**
     * 卸载模型
     */
    @PostMapping("/models/{modelId}/unload")
    public ResponseEntity<String> unloadModel(@PathVariable String modelId) {
        mcpManager.unloadModel(modelId);
        return ResponseEntity.ok("模型已卸载");
    }

    /**
     * 切换模型版本
     */
    @PostMapping("/models/{modelId}/switch-version")
    public ResponseEntity<String> switchModelVersion(
            @PathVariable String modelId,
            @RequestParam String version) {
        mcpManager.switchModelVersion(modelId, version);
        return ResponseEntity.ok("版本切换成功");
    }

    /**
     * 获取模型统计
     */
    @GetMapping("/models/statistics")
    public ResponseEntity<Map<String, Object>> getModelStatistics() {
        return ResponseEntity.ok(mcpManager.getStatistics());
    }

    // ==================== 提示词管理 ====================

    /**
     * 创建提示词
     */
    @PostMapping("/prompts/create")
    public ResponseEntity<String> createPrompt(@RequestBody CreatePromptRequest request) {
        try {
            PromptFactory.PromptContext context = new PromptFactory.PromptContext();
            request.getContext().forEach(context::set);

            String prompt = promptFactory.createPrompt(request.getType(), context);
            return ResponseEntity.ok(prompt);
        } catch (Exception e) {
            log.error("创建提示词失败", e);
            return ResponseEntity.badRequest().body("创建失败: " + e.getMessage());
        }
    }

    /**
     * 创建提示词链
     */
    @PostMapping("/prompts/create-chain")
    public ResponseEntity<String> createPromptChain(@RequestBody CreatePromptChainRequest request) {
        try {
            PromptFactory.PromptContext context = new PromptFactory.PromptContext();
            request.getContext().forEach(context::set);

            String chain = promptFactory.createPromptChain(
                    context,
                    request.getTypes().toArray(new PromptFactory.PromptType[0])
            );
            return ResponseEntity.ok(chain);
        } catch (Exception e) {
            log.error("创建提示词链失败", e);
            return ResponseEntity.badRequest().body("创建失败: " + e.getMessage());
        }
    }

    // ==================== 系统管理 ====================

    /**
     * 系统预热
     */
    @PostMapping("/system/warmup")
    public ResponseEntity<String> systemWarmUp() {
        advancedService.warmUp();
        return ResponseEntity.ok("系统预热完成");
    }

    /**
     * 获取系统状态
     */
    @GetMapping("/system/status")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        return ResponseEntity.ok(advancedService.getSystemStatus());
    }

    /**
     * 健康检查
     */
    @GetMapping("/system/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("tools_count", toolRegistry.getAllTools().size());
        health.put("models_loaded", mcpManager.getStatistics().get("loaded_models"));
        return ResponseEntity.ok(health);
    }

}