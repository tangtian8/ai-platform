package top.tangtian.privateaiagent.assistant.mcp;

/**
 * @program: ai-platform
 * @description: 模型状态
 * @author: tangtian
 * @create: 2026-02-11 10:22
 **/
public enum ModelStatus {
    NOT_LOADED,  // 未加载
    LOADING,     // 加载中
    LOADED,      // 已加载
    ERROR,       // 错误
    UNLOADED     // 已卸载
}
