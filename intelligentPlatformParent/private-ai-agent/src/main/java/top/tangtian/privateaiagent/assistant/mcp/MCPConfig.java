package top.tangtian.privateaiagent.assistant.mcp;

import lombok.Data;

/**
 * @program: ai-platform
 * @description: mcp配置
 * @author: tangtian
 * @create: 2026-02-11 10:24
 **/
@Data
public class MCPConfig {
    private int maxCachedModels = 5;           // 最大缓存模型数
    private int modelIdleTimeoutMinutes = 30;  // 模型闲置超时（分钟）
    private boolean autoWarmUp = false;        // 是否自动预热
    private boolean enableVersionControl = true; // 是否启用版本控制
}
