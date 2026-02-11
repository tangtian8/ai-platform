package top.tangtian.privateaiagent.assistant.mcp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


/**
 * @program: ai-platform
 * @description: MCP (Model Context Protocol) 生命周期管理器  * 负责模型的加载、卸载、缓存、版本管理等
 * @author: tangtian
 * @create: 2026-02-11 10:18
 **/
@Slf4j
@Service
public class MCPLifecycleManager {

    // 模型实例缓存
    private final Map<String, ModelInstance> modelCache = new ConcurrentHashMap<>();

    // 模型元数据
    private final Map<String, ModelMetadata> modelMetadata = new ConcurrentHashMap<>();

    // 加载锁（避免重复加载）
    private final Map<String, ReentrantLock> loadingLocks = new ConcurrentHashMap<>();

    // 配置
    private final MCPConfig config = new MCPConfig();

    /**
     * 获取或加载模型
     */
    public synchronized ModelInstance getOrLoadModel(String modelId, ModelType type) {
        log.debug("获取模型: {} ({})", modelId, type);

        // 1. 检查缓存
        ModelInstance cached = modelCache.get(modelId);
        if (cached != null && cached.getStatus() == ModelStatus.LOADED) {
            cached.updateLastUsed();
            log.debug("使用缓存的模型: {}", modelId);
            return cached;
        }

        // 2. 加载模型
        ReentrantLock lock = loadingLocks.computeIfAbsent(modelId, k -> new ReentrantLock());
        lock.lock();
        try {
            // 再次检查（双重检查锁）
            cached = modelCache.get(modelId);
            if (cached != null && cached.getStatus() == ModelStatus.LOADED) {
                cached.updateLastUsed();
                return cached;
            }

            // 检查缓存容量
            ensureCacheCapacity();

            // 执行加载
            ModelInstance instance = loadModel(modelId, type);
            modelCache.put(modelId, instance);

            log.info("模型加载完成: {} ({})", modelId, type);
            return instance;

        } finally {
            lock.unlock();
        }
    }

    /**
     * 加载模型
     */
    private ModelInstance loadModel(String modelId, ModelType type) {
        log.info("开始加载模型: {} ({})", modelId, type);

        ModelMetadata metadata = modelMetadata.get(modelId);
        if (metadata == null) {
            metadata = createDefaultMetadata(modelId, type);
            modelMetadata.put(modelId, metadata);
        }

        ModelInstance instance = ModelInstance.builder()
                .modelId(modelId)
                .type(type)
                .metadata(metadata)
                .status(ModelStatus.LOADING)
                .loadTime(LocalDateTime.now())
                .lastUsed(LocalDateTime.now())
                .usageCount(0)
                .build();

        try {
            // 根据类型加载不同的模型
            Object model = switch (type) {
                case CHAT -> loadChatModel(metadata);
                case EMBEDDING -> loadEmbeddingModel(metadata);
                case VISION -> loadVisionModel(metadata);
                case AUDIO -> loadAudioModel(metadata);
                default -> throw new IllegalStateException("Unexpected value: " + type);
            };

            instance.setModel(model);
            instance.setStatus(ModelStatus.LOADED);
            instance.setLoadDurationMs(System.currentTimeMillis() -
                    instance.getLoadTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

            log.info("模型加载成功: {} - 耗时 {}ms", modelId, instance.getLoadDurationMs());

        } catch (Exception e) {
            log.error("模型加载失败: {}", modelId, e);
            instance.setStatus(ModelStatus.ERROR);
            instance.setErrorMessage(e.getMessage());
            throw new RuntimeException("模型加载失败: " + modelId, e);
        }

        return instance;
    }

    /**
     * 卸载模型
     */
    public void unloadModel(String modelId) {
        log.info("卸载模型: {}", modelId);

        ModelInstance instance = modelCache.remove(modelId);
        if (instance != null) {
            instance.setStatus(ModelStatus.UNLOADED);
            instance.setUnloadTime(LocalDateTime.now());

            // 执行清理逻辑
            cleanupModel(instance);
        }
    }

    /**
     * 预热模型（提前加载）
     */
    public void warmUpModel(String modelId, ModelType type) {
        log.info("预热模型: {} ({})", modelId, type);
        try {
            getOrLoadModel(modelId, type);
        } catch (Exception e) {
            log.error("模型预热失败: {}", modelId, e);
        }
    }

    /**
     * 批量预热
     */
    public void warmUpModels(Map<String, ModelType> models) {
        for (Map.Entry<String, ModelType> entry : models.entrySet()) {
            warmUpModel(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 注册模型元数据
     */
    public void registerModelMetadata(String modelId, ModelMetadata metadata) {
        modelMetadata.put(modelId, metadata);
        log.info("注册模型元数据: {}", modelId);
    }

    /**
     * 获取模型信息
     */
    public ModelInfo getModelInfo(String modelId) {
        ModelInstance instance = modelCache.get(modelId);
        ModelMetadata metadata = modelMetadata.get(modelId);

        return ModelInfo.builder()
                .modelId(modelId)
                .status(instance != null ? instance.getStatus() : ModelStatus.NOT_LOADED)
                .metadata(metadata)
                .instance(instance)
                .build();
    }

    /**
     * 获取所有模型信息
     */
    public List<ModelInfo> getAllModelsInfo() {
        List<ModelInfo> infos = new ArrayList<>();

        Set<String> allModelIds = new HashSet<>();
        allModelIds.addAll(modelCache.keySet());
        allModelIds.addAll(modelMetadata.keySet());

        for (String modelId : allModelIds) {
            infos.add(getModelInfo(modelId));
        }

        return infos;
    }

    /**
     * 切换模型版本
     */
    public void switchModelVersion(String modelId, String newVersion) {
        log.info("切换模型版本: {} -> {}", modelId, newVersion);

        // 卸载旧版本
        unloadModel(modelId);

        // 更新元数据
        ModelMetadata metadata = modelMetadata.get(modelId);
        if (metadata != null) {
            metadata.setVersion(newVersion);
        }

        // 重新加载（延迟加载，下次使用时自动加载新版本）
    }

    /**
     * 确保缓存容量
     */
    private void ensureCacheCapacity() {
        if (modelCache.size() >= config.getMaxCachedModels()) {
            log.info("缓存已满，执行 LRU 清理");
            evictLeastRecentlyUsed();
        }
    }

    /**
     * LRU 淘汰策略
     */
    private void evictLeastRecentlyUsed() {
        ModelInstance lruInstance = modelCache.values().stream()
                .min(Comparator.comparing(ModelInstance::getLastUsed))
                .orElse(null);

        if (lruInstance != null) {
            log.info("淘汰最久未使用的模型: {}", lruInstance.getModelId());
            unloadModel(lruInstance.getModelId());
        }
    }

    /**
     * 定期清理过期模型
     */
    @Scheduled(fixedRate = 300000) // 5分钟
    public void cleanupExpiredModels() {
        log.debug("执行定期清理");

        LocalDateTime now = LocalDateTime.now();
        List<String> toRemove = new ArrayList<>();

        for (Map.Entry<String, ModelInstance> entry : modelCache.entrySet()) {
            ModelInstance instance = entry.getValue();
            long idleMinutes = Duration.between(instance.getLastUsed(), now).toMinutes();

            if (idleMinutes > config.getModelIdleTimeoutMinutes()) {
                log.info("模型闲置超时，卸载: {} (闲置 {} 分钟)",
                        entry.getKey(), idleMinutes);
                toRemove.add(entry.getKey());
            }
        }

        toRemove.forEach(this::unloadModel);
    }

    /**
     * 获取统计信息
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_models", modelMetadata.size());
        stats.put("loaded_models", modelCache.size());
        stats.put("cache_capacity", config.getMaxCachedModels());

        Map<ModelStatus, Long> statusCounts = modelCache.values().stream()
                .collect(Collectors.groupingBy(
                        ModelInstance::getStatus,
                        Collectors.counting()
                ));
        stats.put("status_distribution", statusCounts);

        long totalUsage = modelCache.values().stream()
                .mapToLong(ModelInstance::getUsageCount)
                .sum();
        stats.put("total_usage_count", totalUsage);

        return stats;
    }

    // ==================== 私有辅助方法 ====================

    private ModelMetadata createDefaultMetadata(String modelId, ModelType type) {
        return ModelMetadata.builder()
                .modelId(modelId)
                .type(type)
                .version("1.0")
                .provider("default")
                .build();
    }

    private Object loadChatModel(ModelMetadata metadata) {
        // 实际实现中应该根据 metadata 加载相应的模型
        // 这里返回一个占位对象
        return new Object(); // 替换为实际的 ChatModel
    }

    private Object loadEmbeddingModel(ModelMetadata metadata) {
        return new Object(); // 替换为实际的 EmbeddingModel
    }

    private Object loadVisionModel(ModelMetadata metadata) {
        return new Object();
    }

    private Object loadAudioModel(ModelMetadata metadata) {
        return new Object();
    }

    private void cleanupModel(ModelInstance instance) {
        // 执行模型清理逻辑
        log.debug("清理模型资源: {}", instance.getModelId());
    }
}