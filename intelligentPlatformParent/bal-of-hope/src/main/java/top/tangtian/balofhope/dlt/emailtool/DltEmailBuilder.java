package top.tangtian.balofhope.dlt.emailtool;

import top.tangtian.balofhope.dlt.colletcion.model.CrawlResult;
import top.tangtian.balofhope.dlt.recommend.entity.DltRecommendTaskEntity;
import top.tangtian.balofhope.dlt.recommend.model.UserStatistics;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @program: ai-platform
 * @description: DltEmailBuilder   大乐透推荐任务邮件 HTML 构建器
 *   用法：
 *    String html = DltEmailBuilder.build(crawlResult, taskList);
 *    emailService.sendHtml(sender, "大乐透推荐通知", html);
 * @author: tangtian
 * @create: 2026-03-12 17:10
 **/
public class DltEmailBuilder {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String build(CrawlResult crawl, List<DltRecommendTaskEntity> tasks, UserStatistics stats) {
        StringBuilder sb = new StringBuilder();

        // ========== 页面框架 ==========
        sb.append("<!DOCTYPE html><html lang='zh'><head><meta charset='UTF-8'/></head>");
        sb.append("<body style='margin:0;padding:0;background:#f0f2f5;font-family:Arial,Microsoft YaHei,sans-serif'>");
        sb.append("<div style='max-width:700px;margin:32px auto'>");

        // ========== 顶部标题栏 ==========
        sb.append("<div style='background:linear-gradient(135deg,#e63946,#c1121f);padding:28px 32px;border-radius:12px 12px 0 0;text-align:center'>");
        sb.append("<h1 style='color:#fff;margin:0;font-size:22px;letter-spacing:2px'>&#127921; 大乐透推荐通知</h1>");
        sb.append("<p style='color:rgba(255,255,255,.8);margin:8px 0 0;font-size:13px'>系统自动推荐，仅供参考，理性购彩</p>");
        sb.append("</div>");

        // ========== 爬取结果卡片 ==========
        sb.append("<div style='background:#fff;padding:28px 32px;border-left:1px solid #e8e8e8;border-right:1px solid #e8e8e8'>");
        sb.append("<h3 style='margin:0 0 16px;color:#333;font-size:15px;border-left:4px solid #e63946;padding-left:10px'>&#128225; 数据爬取结果</h3>");

        String cardBg      = crawl.isSuccess() ? "#f6ffed" : "#fff2f0";
        String cardBorder  = crawl.isSuccess() ? "#b7eb8f" : "#ffccc7";
        String statusColor = crawl.isSuccess() ? "#52c41a" : "#ff4d4f";
        String statusText  = crawl.isSuccess() ? "&#10004; 成功" : "&#10008; 失败";

        sb.append("<div style='background:").append(cardBg).append(";border:1px solid ").append(cardBorder)
                .append(";border-radius:8px;padding:16px 20px;margin-bottom:8px'>");
        sb.append("<table style='width:100%;border-collapse:collapse;font-size:13px;color:#555'>");

        appendRow(sb,
                "&#128278; 状态",   "<span style='color:" + statusColor + ";font-weight:bold'>" + statusText + "</span>",
                "&#9200; 耗时",     crawl.getDurationSeconds() + " 秒");
        appendRow(sb,
                "&#128196; 总页数",  crawl.getTotalPages() + " 页",
                "&#128202; 总记录",  crawl.getTotalRecords() + " 条");
        appendRow(sb,
                "&#128375; 爬取数量", crawl.getCrawledCount() + " 条",
                "&#128190; 保存数量", crawl.getSavedCount() + " 条");
        appendRow(sb,
                "&#10060; 失败页数", "<span style='color:#ff4d4f'>" + crawl.getFailedPages() + " 页</span>",
                "&#128336; 开始时间", crawl.getStartTime() != null ? crawl.getStartTime().format(FMT) : "-");

        if (crawl.getErrorMessage() != null && !crawl.getErrorMessage().isBlank()) {
            sb.append("<tr><td colspan='4' style='padding:6px 0;color:#ff4d4f'>")
                    .append("&#9888; 错误信息：").append(crawl.getErrorMessage())
                    .append("</td></tr>");
        }
        sb.append("</table></div></div>"); // end 爬取卡片

        // ========== 预测准确性统计卡片 ==========
        if (stats != null) {
            sb.append(buildStatsCard(stats));
        }

        // ========== 推荐任务列表 ==========
        sb.append("<div style='background:#fff;padding:0 32px 28px;border-left:1px solid #e8e8e8;border-right:1px solid #e8e8e8'>");
        sb.append("<h3 style='margin:0 0 16px;color:#333;font-size:15px;border-left:4px solid #e63946;padding-left:10px'>&#127919; 推荐任务列表</h3>");

        if (tasks == null || tasks.isEmpty()) {
            sb.append("<div style='text-align:center;padding:32px;color:#999;font-size:14px'>暂无推荐任务数据</div>");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                sb.append(buildTaskCard(tasks.get(i), i + 1));
            }
        }
        sb.append("</div>");

        // ========== 底部 ==========
        sb.append("<div style='background:#fafafa;border:1px solid #e8e8e8;border-top:none;border-radius:0 0 12px 12px;padding:16px 32px;text-align:center'>");
        sb.append("<p style='color:#bbb;font-size:12px;margin:0'>发送时间：")
                .append(LocalDateTime.now().format(FMT))
                .append(" &nbsp;|&nbsp; 此邮件由系统自动发出，请勿回复</p>");
        sb.append("</div>");

        sb.append("</div></body></html>");
        return sb.toString();
    }

    // ==================== 预测准确性统计卡片 ====================

    @SuppressWarnings("unchecked")
    private static String buildStatsCard(UserStatistics stats) {

        int totalTasks = stats.getTotalTasks() == null ? 0 : stats.getTotalTasks();
        int prizeTasks = stats.getHitTasksCount() == null ? 0 : stats.getHitTasksCount();

        double prizeRate = totalTasks == 0 ? 0 : (prizeTasks * 100.0 / totalTasks);

        // 统计各奖级数量
        Map<String, Long> levelCount = Optional.ofNullable(stats.getHitDetails())
                .map(UserStatistics.HitDetails::getHitLevelList)
                .orElse(Collections.emptyList())
                .stream()
                .filter(v -> v.getPrizeLevel() != null && !"未中奖".equals(v.getPrizeLevel()))
                .collect(Collectors.groupingBy(
                        UserStatistics.HitLevel::getPrizeLevel,
                        Collectors.counting()
                ));

        // 命中率颜色
        String rateColor = prizeRate >= 10 ? "#52c41a" : prizeRate >= 5 ? "#fa8c16" : "#aaa";
        String rateText = String.format("%.1f%%", prizeRate);

        int barWidth = Math.min((int) prizeRate * 5, 100);

        StringBuilder sb = new StringBuilder();

        sb.append("<div style='background:#fff;padding:24px 32px;border-left:1px solid #e8e8e8;border-right:1px solid #e8e8e8;border-top:1px solid #f0f0f0'>");

        sb.append("<h3 style='margin:0 0 16px;color:#333;font-size:15px;border-left:4px solid #faad14;padding-left:10px'>📊 预测准确性统计</h3>");

        // === 顶部指标 ===
        sb.append("<div style='display:flex;gap:12px;margin-bottom:20px;text-align:center'>");

        appendMetricBox(sb, "📋 总验证期数", String.valueOf(totalTasks), "期", "#1677ff", "#e6f4ff");
        appendMetricBox(sb, "🏆 命中次数", String.valueOf(prizeTasks), "次", "#52c41a", "#f6ffed");
        appendMetricBox(sb, "🎯 命中率", rateText, "", rateColor, "#fffbe6");

        sb.append("</div>");

        // === 命中率进度条 ===
        sb.append("<div style='margin-bottom:20px'>");
        sb.append("<div style='display:flex;justify-content:space-between;font-size:12px;color:#888;margin-bottom:6px'>");
        sb.append("<span>命中率</span><span style='color:")
                .append(rateColor)
                .append(";font-weight:bold'>")
                .append(rateText)
                .append("</span></div>");

        sb.append("<div style='background:#f0f0f0;border-radius:4px;height:8px;overflow:hidden'>");
        sb.append("<div style='background:")
                .append(rateColor)
                .append(";width:")
                .append(barWidth)
                .append("%;height:100%;border-radius:4px;'></div>");
        sb.append("</div></div>");

        // === 奖级分布 ===
        if (!levelCount.isEmpty()) {
            sb.append("<div>");
            sb.append("<p style='font-size:13px;color:#888;margin:0 0 10px'>各奖级命中分布：</p>");
            sb.append("<div style='display:flex;flex-wrap:wrap;gap:8px'>");

            levelCount.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .forEach(entry -> {
                        String lvColor = getLevelColor(entry.getKey());
                        sb.append("<span style='background:")
                                .append(lvColor)
                                .append(";color:#fff;padding:4px 12px;border-radius:12px;font-size:12px;font-weight:bold'>")
                                .append(entry.getKey())
                                .append(" × ")
                                .append(entry.getValue())
                                .append("</span>");
                    });

            sb.append("</div></div>");
        } else {
            sb.append("<p style='color:#bbb;font-size:13px;text-align:center;margin:8px 0'>暂无命中记录</p>");
        }

        sb.append("</div>");

        return sb.toString();
    }

    /** 单个指标框 */
    private static void appendMetricBox(StringBuilder sb, String label, String value, String unit,
                                        String color, String bg) {
        sb.append("<div style='flex:1;background:").append(bg)
                .append(";border-radius:8px;padding:16px 8px'>")
                .append("<div style='font-size:12px;color:#888;margin-bottom:8px'>").append(label).append("</div>")
                .append("<div style='font-size:24px;font-weight:bold;color:").append(color).append("'>")
                .append(value).append("<span style='font-size:13px;margin-left:2px'>").append(unit).append("</span></div>")
                .append("</div>");
    }

    /** 奖级对应颜色 */
    private static String getLevelColor(String level) {
        if (level == null) return "#aaa";
        return switch (level) {
            case "一等奖" -> "#c1121f";
            case "二等奖" -> "#e63946";
            case "三等奖" -> "#fa541c";
            case "四等奖" -> "#fa8c16";
            case "五等奖" -> "#faad14";
            case "六等奖" -> "#52c41a";
            default       -> "#1677ff";
        };
    }

    // ==================== 单个推荐任务卡片 ====================

    private static String buildTaskCard(DltRecommendTaskEntity t, int index) {
        String strategyLabel = switch (t.getRecommendStrategy() == null ? "" : t.getRecommendStrategy()) {
            case "DAILY"   -> "日推荐";
            case "WEEKLY"  -> "周推荐";
            case "MONTHLY" -> "月推荐";
            case "ALL"     -> "全量推荐";
            default        -> t.getRecommendStrategy() != null ? t.getRecommendStrategy() : "-";
        };

        String drawedBadge = Boolean.TRUE.equals(t.getIsDrawed())
                ? "<span style='background:#fff1f0;color:#ff4d4f;padding:2px 8px;border-radius:4px;font-size:11px'>已开奖</span>"
                : "<span style='background:#e6f4ff;color:#1677ff;padding:2px 8px;border-radius:4px;font-size:11px'>待开奖</span>";

        Object[][] groups = {
                {t.getRedBalls1(), t.getBlueBalls1(), t.getPrizeLevel1(), t.getHitRedCount1(), t.getHitBlueCount1(), 1},
                {t.getRedBalls2(), t.getBlueBalls2(), t.getPrizeLevel2(), t.getHitRedCount2(), t.getHitBlueCount2(), 2},
                {t.getRedBalls3(), t.getBlueBalls3(), t.getPrizeLevel3(), t.getHitRedCount3(), t.getHitBlueCount3(), 3},
                {t.getRedBalls4(), t.getBlueBalls4(), t.getPrizeLevel4(), t.getHitRedCount4(), t.getHitBlueCount4(), 4},
        };

        StringBuilder rows = new StringBuilder();
        for (Object[] g : groups) {
            int no = (int) g[5];
            boolean isBest = t.getBestRecommendNo() != null && t.getBestRecommendNo() == no;
            String rowBg   = isBest ? "background:#fffbe6;" : "";
            String bestTag = isBest ? " <span style='color:#faad14;font-size:11px'>&#11088;最佳</span>" : "";

            String hitInfo = "-";
            if (Boolean.TRUE.equals(t.getIsDrawed())) {
                hitInfo = "红" + (g[3] != null ? g[3] : 0) + " 蓝" + (g[4] != null ? g[4] : 0);
            }
            String prizeLevel = (String) g[2];
            boolean isWon = prizeLevel != null && !prizeLevel.isBlank() && !"未中奖".equals(prizeLevel);

            rows.append("<tr style='").append(rowBg).append("border-bottom:1px solid #f5f5f5'>")
                    .append("<td style='padding:8px 6px;text-align:center;color:#888;font-size:12px'>推荐").append(no).append(bestTag).append("</td>")
                    .append("<td style='padding:8px 6px'>").append(renderBalls((String) g[0], "#ff4d4f")).append("</td>")
                    .append("<td style='padding:8px 6px'>").append(renderBalls((String) g[1], "#1677ff")).append("</td>")
                    .append("<td style='padding:8px 6px;text-align:center;font-size:12px;color:#555'>").append(hitInfo).append("</td>")
                    .append("<td style='padding:8px 6px;text-align:center;font-size:12px;color:").append(isWon ? "#e63946" : "#999").append("'>")
                    .append(prizeLevel != null && !prizeLevel.isBlank() ? prizeLevel : "-")
                    .append("</td></tr>");
        }

        StringBuilder card = new StringBuilder();
        card.append("<div style='border:1px solid #e8e8e8;border-radius:8px;margin-bottom:16px;overflow:hidden'>");

        card.append("<div style='background:#fff5f5;padding:12px 16px;border-bottom:1px solid #ffd6d6'>")
                .append("<span style='font-weight:bold;color:#c1121f;font-size:14px'>#").append(index)
                .append(" &nbsp; 批次：").append(t.getRecommendBatch() != null ? t.getRecommendBatch() : "-").append("</span>")
                .append("<span style='color:#888;font-size:12px;float:right'>")
                .append(strategyLabel).append(" &nbsp; 分析期数：")
                .append(t.getAnalyzedPeriods() != null ? t.getAnalyzedPeriods() + " 期" : "-")
                .append(" &nbsp; ").append(drawedBadge).append("</span></div>");

        card.append("<table style='width:100%;border-collapse:collapse;font-size:13px'>")
                .append("<thead><tr style='color:#aaa;font-size:11px;background:#fafafa'>")
                .append("<th style='padding:6px;text-align:center;width:80px'>推荐组</th>")
                .append("<th style='padding:6px'>红球（前区5个）</th>")
                .append("<th style='padding:6px'>蓝球（后区2个）</th>")
                .append("<th style='padding:6px;text-align:center;width:80px'>命中</th>")
                .append("<th style='padding:6px;text-align:center;width:80px'>奖级</th>")
                .append("</tr></thead>")
                .append("<tbody>").append(rows).append("</tbody></table>");

        card.append("<div style='background:#fafafa;padding:8px 16px;border-top:1px solid #f0f0f0;font-size:12px;color:#aaa'>")
                .append("目标期号：<b style='color:#555'>").append(t.getTargetDrawNum() != null ? t.getTargetDrawNum() : "-").append("</b>")
                .append(" &nbsp;|&nbsp; 推荐时间：").append(t.getRecommendTime() != null ? t.getRecommendTime().format(FMT) : "-")
                .append(" &nbsp;|&nbsp; 用户：").append(t.getUserName() != null ? t.getUserName() : "-");

        if (Boolean.TRUE.equals(t.getIsDrawed()) && t.getActualDrawResult() != null) {
            card.append(" &nbsp;|&nbsp; 开奖结果：<b style='color:#e63946'>").append(t.getActualDrawResult()).append("</b>");
        }
        card.append("</div></div>");
        return card.toString();
    }

    // ==================== 工具方法 ====================

    private static String renderBalls(String ballStr, String color) {
        if (ballStr == null || ballStr.isBlank()) return "-";
        StringBuilder html = new StringBuilder();
        for (String num : ballStr.split(",")) {
            html.append("<span style='display:inline-block;width:26px;height:26px;line-height:26px;")
                    .append("text-align:center;border-radius:50%;background:").append(color)
                    .append(";color:#fff;font-size:12px;font-weight:bold;margin:1px 2px'>")
                    .append(num.trim()).append("</span>");
        }
        return html.toString();
    }

    private static void appendRow(StringBuilder sb, String k1, String v1, String k2, String v2) {
        sb.append("<tr>")
                .append("<td style='padding:5px 0;width:25%;color:#888'>").append(k1).append("</td>")
                .append("<td style='padding:5px 0'>").append(v1).append("</td>")
                .append("<td style='padding:5px 0;width:25%;color:#888'>").append(k2).append("</td>")
                .append("<td style='padding:5px 0'>").append(v2).append("</td>")
                .append("</tr>");
    }

    private static int toInt(Object val) {
        if (val instanceof Number n) return n.intValue();
        return 0;
    }

    private static double toDouble(Object val) {
        if (val instanceof Number n) return n.doubleValue();
        return 0.0;
    }
}
