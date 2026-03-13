package top.tangtian.balofhope.dlt.emailtool;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
 * @program: ai-platform
 * @description: EmailService
 * @author: tangtian
 * @create: 2026-03-12 15:48
 **/
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String           fromAddress;

    public EmailService(JavaMailSender mailSender,
                        @Value("${spring.mail.username}") String fromAddress) {
        this.mailSender      = mailSender;
        this.fromAddress     = fromAddress;
    }

    // ==================== 公共 API ====================

    /**
     * 同步发送（通用入口）
     */
    public void send(EmailRequest request) {
        try {
            MimeMessage mime = buildMimeMessage(request);
            mailSender.send(mime);
            log.info("✉️  邮件发送成功 → to={} subject={}", request.to(), request.subject());
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("邮件构建失败: " + e.getMessage(), e);
        }
    }

    /**
     * 异步发送（@Async，立即返回 Future）
     */
    @Async
    public CompletableFuture<Void> sendAsync(EmailRequest request) {
        try {
            send(request);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * 快捷：纯文本
     */
    public void sendText(String to, String subject, String text) {
        send(new EmailRequest(
                List.of(to), null, null, subject,
                text, null, null, null, null
        ));
    }

    /**
     * 快捷：HTML
     */
    public void sendHtml(String to, String subject, String html) {
        send(new EmailRequest(
                List.of(to), null, null, subject,
                null, html, null, null, null
        ));
    }


    // ==================== 私有构建逻辑 ====================

    private MimeMessage buildMimeMessage(EmailRequest req)
            throws MessagingException, UnsupportedEncodingException {

        MimeMessage mime = mailSender.createMimeMessage();
        boolean hasAttachment = req.attachments() != null && !req.attachments().isEmpty();

        MimeMessageHelper helper = new MimeMessageHelper(mime, hasAttachment, "UTF-8");

        // 发件人（携带昵称）
        helper.setFrom(new InternetAddress(fromAddress, "唐甜的彩票预测", "UTF-8"));

        // 收件人
        helper.setTo(req.to().toArray(String[]::new));

        // 抄送 / 密送
        if (req.cc() != null && !req.cc().isEmpty()) {
            helper.setCc(req.cc().toArray(String[]::new));
        }
        if (req.bcc() != null && !req.bcc().isEmpty()) {
            helper.setBcc(req.bcc().toArray(String[]::new));
        }

        helper.setSubject(req.subject());

        // 正文：TEXT / HTML / TEMPLATE
        switch (req.bodyType()) {
            case TEXT -> helper.setText(
                    req.textBody() != null ? req.textBody() : "", false);

            case HTML -> helper.setText(
                    req.htmlBody(), true);
        }

        // 附件
        if (hasAttachment) {
            for (String filePath : req.attachments()) {
                Path path = Path.of(filePath);
                if (!Files.exists(path)) {
                    log.warn("⚠️  附件不存在，已跳过: {}", filePath);
                    continue;
                }
                helper.addAttachment(path.getFileName().toString(),
                        new FileSystemResource(new File(filePath)));
            }
        }

        return mime;
    }
}
