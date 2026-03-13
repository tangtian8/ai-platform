package top.tangtian.balofhope.dlt.emailtool;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * @program: ai-platform
 * @description: EmailRequest 邮件发送请求（支持纯文本 / HTML / 模板 / 附件）
 * @author: tangtian
 * @create: 2026-03-12 15:51
 **/
public record EmailRequest(

        @NotEmpty(message = "收件人不能为空")
        List<@Email(message = "收件人地址格式不正确") String> to,

        List<@Email String> cc,

        List<@Email String> bcc,

        @NotBlank(message = "邮件主题不能为空")
        String subject,

        /** 纯文本正文（与 htmlBody / templateName 三选一） */
        String textBody,

        /** HTML 正文 */
        String htmlBody,

        /** Thymeleaf 模板名称（对应 resources/templates/email/*.html） */
        String templateName,

        /** 传入模板的变量，key-value 形式 */
        java.util.Map<String, Object> templateVariables,

        /** 附件文件路径列表（服务器本地路径） */
        List<String> attachments
) {
    /** 判断正文类型 */
    public BodyType bodyType() {
        if (templateName != null && !templateName.isBlank()) return BodyType.TEMPLATE;
        if (htmlBody != null && !htmlBody.isBlank())         return BodyType.HTML;
        return BodyType.TEXT;
    }

    public enum BodyType { TEXT, HTML, TEMPLATE }
}

