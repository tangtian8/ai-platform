package top.tangtian.privateaiagent.assistant.tools;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @program: ai-platform
 * @description: 工具执行结果
 * @author: tangtian
 * @create: 2026-02-10 15:58
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolResult {
    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 结果数据
     */
    private Object data;

    /**
     * 错误信息
     */
    private String error;

    /**
     * 执行耗时（毫秒）
     */
    private long executionTime;

    /**
     * 元数据
     */
    private Map<String, Object> metadata;

    public static ToolResult success(Object data) {
        return ToolResult.builder()
                .success(true)
                .data(data)
                .build();
    }

    public static ToolResult error(String error) {
        return ToolResult.builder()
                .success(false)
                .error(error)
                .build();
    }
}
