package top.tangtian.balofhope.dlt.emailtool;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import top.tangtian.balofhope.BalHopeApplication;
import top.tangtian.balofhope.dlt.schedule.ScheduledCollectionAndRecommend;

/**
 * @program: ai-platform
 * @description: EmailService
 * @author: tangtian
 * @create: 2026-03-12 16:08
 **/
@SpringBootTest(classes = {BalHopeApplication.class},webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("邮件发送测试")
public class EmailServiceTest {
    @Resource
    private EmailService emailService;

    @Value("${spring.mail.username}")
    private String sender;


    @Resource
    ScheduledCollectionAndRecommend scheduledCollectionAndRecommend;

    @Test
    void sendTextEmail() {
        emailService.sendText(
                sender,        // 收件人 = 发件人 = 你自己
                "测试邮件",
                "Hello！"
        );
    }

    @Test
    void sendHtmlText() {
        emailService.sendHtml(
                sender,        // 收件人 = 发件人 = 你自己
                "测试邮件",
                "<!DOCTYPE html>\n" +
                        "<html lang=\"zh\">\n" +
                        "<head><meta charset=\"UTF-8\"/></head>\n" +
                        "<body style=\"margin:0;padding:0;background:#f4f6f9;font-family:Arial,sans-serif\">\n" +
                        "  <div style=\"max-width:560px;margin:40px auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 2px 12px rgba(0,0,0,.08)\">\n" +
                        "    \n" +
                        "    <!-- 头部 -->\n" +
                        "    <div style=\"background:linear-gradient(135deg,#1677ff,#0958d9);padding:28px;text-align:center\">\n" +
                        "      <h2 style=\"color:#fff;margin:0;font-size:20px\">\uD83D\uDCEC 邮件通知</h2>\n" +
                        "    </div>\n" +
                        "\n" +
                        "    <!-- 正文 -->\n" +
                        "    <div style=\"padding:32px\">\n" +
                        "      <p style=\"color:#333;font-size:15px;margin:0 0 16px\">你好！</p>\n" +
                        "      <p style=\"color:#555;font-size:14px;line-height:1.8;margin:0 0 24px\">\n" +
                        "        这是一封来自 <b>Spring Boot 邮件工具</b> 的测试邮件，发送成功 \uD83C\uDF89\n" +
                        "      </p>\n" +
                        "      <!-- 按钮（可选） -->\n" +
                        "      <div style=\"text-align:center\">\n" +
                        "        <a href=\"https://www.qq.com\"\n" +
                        "           style=\"display:inline-block;padding:10px 28px;background:#1677ff;color:#fff;border-radius:6px;text-decoration:none;font-size:14px\">\n" +
                        "          点击访问\n" +
                        "        </a>\n" +
                        "      </div>\n" +
                        "    </div>\n" +
                        "\n" +
                        "    <!-- 底部 -->\n" +
                        "    <div style=\"background:#f9f9f9;padding:16px;text-align:center;border-top:1px solid #eee\">\n" +
                        "      <p style=\"color:#aaa;font-size:12px;margin:0\">此邮件由系统自动发出，请勿回复</p>\n" +
                        "    </div>\n" +
                        "\n" +
                        "  </div>\n" +
                        "</body>\n" +
                        "</html>"
        );
    }

    @Test
    void scheduledCollectionAndRecommendText(){
        scheduledCollectionAndRecommend.collectionAndRecommend();
    }
}
