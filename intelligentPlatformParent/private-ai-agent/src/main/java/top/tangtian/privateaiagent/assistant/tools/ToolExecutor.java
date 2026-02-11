package top.tangtian.privateaiagent.assistant.tools;

import java.util.Map;

/**
 * @program: ai-platform
 * @description: 工具执行器接口
 * @author: tangtian
 * @create: 2026-02-10 15:57
 **/
public interface ToolExecutor {
    /**
     * 执行工具
     * @param parameters 工具参数
     * @return 执行结果
     */
    ToolResult execute(Map<String, Object> parameters) throws Exception;
}
