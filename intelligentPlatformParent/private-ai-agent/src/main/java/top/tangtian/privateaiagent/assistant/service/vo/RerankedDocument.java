package top.tangtian.privateaiagent.assistant.service.vo;

import lombok.Data;
import top.tangtian.privateaiagent.assistant.entity.UserKnowledge;

/**
 * @program: ai-platform
 * @description: 重排序文档数据类
 * @author: tangtian
 * @create: 2026-02-11 09:39
 **/
@Data
public class RerankedDocument {
    private final UserKnowledge document;
    private final double score;
}
