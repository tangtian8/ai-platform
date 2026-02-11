package top.tangtian.privateaiagent.assistant.service.vo;

import lombok.Data;
import top.tangtian.privateaiagent.assistant.entity.UserKnowledge;

import java.util.Collections;
import java.util.List;

/**
 * @program: ai-platform
 * @description: 检索结果数据类
 * @author: tangtian
 * @create: 2026-02-11 09:37
 **/
@Data
public class RetrievalResult {
    private final List<UserKnowledge> documents;
    private final String formattedContext;
    private final boolean hasResults;

    public static RetrievalResult empty() {
        return new RetrievalResult(
                Collections.emptyList(),
                "",
                false
        );
    }
}
