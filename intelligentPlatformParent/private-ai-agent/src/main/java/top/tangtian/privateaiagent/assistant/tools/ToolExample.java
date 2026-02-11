package top.tangtian.privateaiagent.assistant.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @program: ai-platform
 * @description: 工具实例
 * @author: tangtian
 * @create: 2026-02-10 15:59
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToolExample {
    private String description;
    private Map<String, Object> parameters;
    private String expectedResult;
}
