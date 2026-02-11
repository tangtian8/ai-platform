package top.tangtian.privateaiagent.assistant.chain;

/**
 * @program: ai-platform
 * @description: 工具调用链引擎
 * @author: tangtian
 * @create: 2026-02-10 13:54
 **/

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import top.tangtian.privateaiagent.assistant.tools.ToolDefinition;
import top.tangtian.privateaiagent.assistant.tools.ToolRegistry;
import top.tangtian.privateaiagent.assistant.tools.ToolResult;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * 工具调用链引擎
 * 负责编排和执行工具调用链
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ToolChainEngine {

    private final ToolRegistry toolRegistry;
    private final ChatClient chatClient;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    /**
     * 执行工具链
     */
    public ChainExecutionResult executeChain(ToolChain chain) {
        log.info("开始执行工具链: {}", chain.getName());

        ChainExecutionResult result = ChainExecutionResult.builder()
                .chainId(chain.getId())
                .chainName(chain.getName())
                .startTime(LocalDateTime.now())
                .build();

        List<StepExecutionResult> stepResults = new ArrayList<>();
        Map<String, Object> context = new HashMap<>(chain.getInitialContext());

        try {
            for (ChainStep step : chain.getSteps()) {
                StepExecutionResult stepResult = executeStep(step, context);
                stepResults.add(stepResult);

                if (!stepResult.isSuccess()) {
                    result.setSuccess(false);
                    result.setError("步骤 " + step.getStepId() + " 执行失败: " + stepResult.getError());
                    break;
                }

                // 将步骤结果添加到上下文
                if (step.getOutputKey() != null) {
                    context.put(step.getOutputKey(), stepResult.getResult());
                }

                // 检查是否满足条件跳转
                if (step.getCondition() != null && !evaluateCondition(step.getCondition(), context)) {
                    log.info("步骤 {} 条件不满足，跳过后续步骤", step.getStepId());
                    break;
                }
            }

            result.setSuccess(stepResults.stream().allMatch(StepExecutionResult::isSuccess));
            result.setFinalContext(context);

        } catch (Exception e) {
            log.error("工具链执行异常", e);
            result.setSuccess(false);
            result.setError(e.getMessage());
        } finally {
            result.setEndTime(LocalDateTime.now());
            result.setStepResults(stepResults);
        }

        log.info("工具链执行完成: {} - {}", chain.getName(),
                result.isSuccess() ? "成功" : "失败");

        return result;
    }

    /**
     * 并行执行工具链
     */
    public ChainExecutionResult executeChainParallel(ToolChain chain) {
        log.info("开始并行执行工具链: {}", chain.getName());

        ChainExecutionResult result = ChainExecutionResult.builder()
                .chainId(chain.getId())
                .chainName(chain.getName())
                .startTime(LocalDateTime.now())
                .build();

        try {
            // 构建依赖图
            Map<String, Set<String>> dependencies = buildDependencyGraph(chain);

            // 拓扑排序，确定执行层级
            List<List<ChainStep>> executionLayers = topologicalSort(chain.getSteps(), dependencies);

            Map<String, Object> context = new HashMap<>(chain.getInitialContext());
            List<StepExecutionResult> allResults = new ArrayList<>();

            // 按层级并行执行
            for (List<ChainStep> layer : executionLayers) {
                List<Future<StepExecutionResult>> futures = new ArrayList<>();

                for (ChainStep step : layer) {
                    futures.add(executorService.submit(() -> executeStep(step, context)));
                }

                // 等待当前层级完成
                for (int i = 0; i < futures.size(); i++) {
                    StepExecutionResult stepResult = futures.get(i).get();
                    allResults.add(stepResult);

                    ChainStep step = layer.get(i);
                    if (step.getOutputKey() != null && stepResult.isSuccess()) {
                        synchronized (context) {
                            context.put(step.getOutputKey(), stepResult.getResult());
                        }
                    }
                }
            }

            result.setSuccess(allResults.stream().allMatch(StepExecutionResult::isSuccess));
            result.setStepResults(allResults);
            result.setFinalContext(context);

        } catch (Exception e) {
            log.error("并行工具链执行异常", e);
            result.setSuccess(false);
            result.setError(e.getMessage());
        } finally {
            result.setEndTime(LocalDateTime.now());
        }

        return result;
    }

    /**
     * 执行单个步骤
     */
    private StepExecutionResult executeStep(ChainStep step, Map<String, Object> context) {
        log.debug("执行步骤: {} - {}", step.getStepId(), step.getToolName());

        long startTime = System.currentTimeMillis();
        StepExecutionResult result = StepExecutionResult.builder()
                .stepId(step.getStepId())
                .toolName(step.getToolName())
                .startTime(LocalDateTime.now())
                .build();

        try {
            // 获取工具定义
            ToolDefinition tool = toolRegistry.getTool(step.getToolName());
            if (tool == null) {
                throw new IllegalArgumentException("工具不存在: " + step.getToolName());
            }

            // 准备参数（从上下文中提取）
            Map<String, Object> parameters = prepareParameters(step.getParameters(), context);

            // 执行工具
            ToolResult toolResult = tool.getExecutor().execute(parameters);

            result.setSuccess(toolResult.isSuccess());
            result.setResult(toolResult.getData());
            result.setError(toolResult.getError());

        } catch (Exception e) {
            log.error("步骤执行失败: {}", step.getStepId(), e);
            result.setSuccess(false);
            result.setError(e.getMessage());
        } finally {
            result.setEndTime(LocalDateTime.now());
            result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        }

        return result;
    }

    /**
     * 准备参数（支持从上下文引用）
     */
    private Map<String, Object> prepareParameters(Map<String, Object> paramTemplate,
                                                  Map<String, Object> context) {
        Map<String, Object> parameters = new HashMap<>();

        for (Map.Entry<String, Object> entry : paramTemplate.entrySet()) {
            Object value = entry.getValue();

            // 支持 ${context.key} 语法引用上下文
            if (value instanceof String) {
                String strValue = (String) value;
                if (strValue.startsWith("${") && strValue.endsWith("}")) {
                    String contextKey = strValue.substring(2, strValue.length() - 1);
                    value = context.get(contextKey);
                }
            }

            parameters.put(entry.getKey(), value);
        }

        return parameters;
    }

    /**
     * 评估条件表达式
     */
    private boolean evaluateCondition(String condition, Map<String, Object> context) {
        // 简化实现：支持简单的条件判断
        // 实际项目中可以使用 JEXL、SpEL 等表达式引擎

        if (condition.contains("==")) {
            String[] parts = condition.split("==");
            String leftKey = parts[0].trim();
            String rightValue = parts[1].trim().replace("'", "");

            Object leftValue = context.get(leftKey);
            return Objects.equals(String.valueOf(leftValue), rightValue);
        }

        // 默认返回 true
        return true;
    }

    /**
     * 构建依赖图
     */
    private Map<String, Set<String>> buildDependencyGraph(ToolChain chain) {
        Map<String, Set<String>> graph = new HashMap<>();

        for (ChainStep step : chain.getSteps()) {
            graph.put(step.getStepId(), new HashSet<>(step.getDependsOn()));
        }

        return graph;
    }

    /**
     * 拓扑排序（用于并行执行）
     */
    private List<List<ChainStep>> topologicalSort(List<ChainStep> steps,
                                                  Map<String, Set<String>> dependencies) {
        List<List<ChainStep>> layers = new ArrayList<>();
        Set<String> processed = new HashSet<>();
        Map<String, ChainStep> stepMap = steps.stream()
                .collect(Collectors.toMap(ChainStep::getStepId, s -> s));

        while (processed.size() < steps.size()) {
            List<ChainStep> currentLayer = new ArrayList<>();

            for (ChainStep step : steps) {
                if (processed.contains(step.getStepId())) {
                    continue;
                }

                // 检查依赖是否都已处理
                Set<String> deps = dependencies.get(step.getStepId());
                if (deps == null || processed.containsAll(deps)) {
                    currentLayer.add(step);
                }
            }

            if (currentLayer.isEmpty()) {
                throw new IllegalStateException("检测到循环依赖");
            }

            layers.add(currentLayer);
            currentLayer.forEach(s -> processed.add(s.getStepId()));
        }

        return layers;
    }

    /**
     * 创建工具链构建器
     */
    public ToolChainBuilder builder(String name) {
        return new ToolChainBuilder(name);
    }
    /**
     * 工具链构建器
     */
    class ToolChainBuilder {
        private final ToolChain chain;

        public ToolChainBuilder(String name) {
            this.chain = new ToolChain();
            this.chain.setName(name);
        }

        public ToolChainBuilder description(String description) {
            chain.setDescription(description);
            return this;
        }

        public ToolChainBuilder addStep(String toolName, Map<String, Object> parameters) {
            ChainStep step = new ChainStep();
            step.setToolName(toolName);
            step.setParameters(parameters);
            chain.getSteps().add(step);
            return this;
        }

        public ToolChainBuilder addStep(ChainStep step) {
            chain.getSteps().add(step);
            return this;
        }

        public ToolChainBuilder initialContext(Map<String, Object> context) {
            chain.setInitialContext(context);
            return this;
        }

        public ToolChainBuilder executionMode(ChainExecutionMode mode) {
            chain.setExecutionMode(mode);
            return this;
        }

        public ToolChain build() {
            return chain;
        }
    }
}
