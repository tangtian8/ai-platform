package top.tangtian.balofhope.dlt.emailtool;

import top.tangtian.balofhope.dlt.colletcion.model.CrawlResult;
import top.tangtian.balofhope.dlt.recommend.entity.DltRecommendTaskEntity;
import top.tangtian.balofhope.dlt.recommend.model.UserStatistics;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @program: ai-platform
 * @description: DltEmailBuilder 大乐透推荐任务邮件 HTML 构建器（响应式版）
 * @author: tangtian
 * @create: 2026-03-12 17:10
 **/
public class DltEmailBuilder {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ==================== 全局样式 ====================
    private static final String GLOBAL_STYLE = """
            <style>
              * { box-sizing: border-box; }
              body { margin:0; padding:0; background:#f0f2f5; font-family:Arial,'Microsoft YaHei',sans-serif; }
              .wrap { max-width:700px; margin:24px auto; }
              .section { background:#fff; padding:20px 24px; border-left:1px solid #e8e8e8; border-right:1px solid #e8e8e8; }
              .section-title { margin:0 0 14px; color:#333; font-size:15px; border-left:4px solid #e63946; padding-left:10px; }
              .card { border:1px solid #e8e8e8; border-radius:8px; margin-bottom:14px; overflow:hidden; }
              .card-header { background:#fff5f5; padding:10px 14px; border-bottom:1px solid #ffd6d6; }
              .card-footer { background:#fafafa; padding:8px 14px; border-top:1px solid #f0f0f0; font-size:11px; color:#aaa; line-height:1.8; }
              .metric-row { display:flex; gap:12px; margin:14px 0; }
              .metric-box { flex:1; border-radius:8px; padding:12px 8px; text-align:center; }
              .metric-label { font-size:11px; color:#888; margin-bottom:6px; }
              .metric-value { font-size:20px; font-weight:bold; }
              .metric-unit { font-size:12px; margin-left:2px; }
              table.std { width:100%; border-collapse:collapse; font-size:12px; }
              table.std th, table.std td { border:1px solid #eee; padding:7px 8px; }
              table.std thead tr { background:#fafafa; }
              table.std th { color:#666; font-weight:500; text-align:center; }
              .badge { display:inline-block; padding:2px 8px; border-radius:4px; font-size:11px; }
              .badge-blue { background:#e6f4ff; color:#1677ff; }
              .badge-red  { background:#fff1f0; color:#ff4d4f; }
              .ball-wrap { display:flex; flex-wrap:wrap; gap:2px; align-items:center; }
              .ball { display:inline-flex; align-items:center; justify-content:center;
                      width:24px; height:24px; border-radius:50%;
                      color:#fff; font-size:11px; font-weight:bold; }
              .hit-row td { background:#fffbe6 !important; }
              @media (max-width:600px) {
                .wrap { margin:0; }
                .section { padding:14px 12px; }
                .metric-value { font-size:16px; }
                .metric-box { padding:10px 4px; }
                table.std th, table.std td { padding:5px 4px; font-size:11px; }
                .ball { width:20px; height:20px; font-size:10px; }
                .card-header { font-size:12px; }
                .resp-hide { display:none; }
              }
            </style>
            """;

    public static String build(CrawlResult crawl, List<DltRecommendTaskEntity> tasks, UserStatistics stats) {
        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html><html lang='zh'><head>")
                .append("<meta charset='UTF-8'/>")
                .append("<meta name='viewport' content='width=device-width,initial-scale=1.0'/>")
                .append(GLOBAL_STYLE)
                .append("</head>")
                .append("<body><div class='wrap'>");

        // ========== 顶部标题栏 ==========
        sb.append("<div style='background:linear-gradient(135deg,#e63946,#c1121f);padding:24px 20px;border-radius:12px 12px 0 0;text-align:center'>")
                .append("<h1 style='color:#fff;margin:0;font-size:20px;letter-spacing:2px'>&#127921; 大乐透推荐通知</h1>")
                .append("<p style='color:rgba(255,255,255,.8);margin:6px 0 0;font-size:12px'>系统自动推荐，仅供参考，理性购彩</p>")
                .append("</div>");

        // ========== 爬取结果 ==========
        sb.append("<div class='section'>");
        sb.append("<h3 class='section-title'>&#128225; 数据爬取结果</h3>");

        String cardBg      = crawl.isSuccess() ? "#f6ffed" : "#fff2f0";
        String cardBorder  = crawl.isSuccess() ? "#b7eb8f" : "#ffccc7";
        String statusColor = crawl.isSuccess() ? "#52c41a" : "#ff4d4f";
        String statusText  = crawl.isSuccess() ? "&#10004; 成功" : "&#10008; 失败";

        sb.append("<div style='background:").append(cardBg)
                .append(";border:1px solid ").append(cardBorder)
                .append(";border-radius:8px;padding:14px 16px'>");

        // 用 div 两列 grid 代替 table，手机上不会挤
        sb.append("<div style='display:grid;grid-template-columns:1fr 1fr;gap:8px;font-size:13px;color:#555'>");
        appendGridItem(sb, "&#128278; 状态", "<span style='color:" + statusColor + ";font-weight:bold'>" + statusText + "</span>");
        appendGridItem(sb, "&#9200; 耗时", crawl.getDurationSeconds() + " 秒");
        appendGridItem(sb, "&#128196; 总页数", crawl.getTotalPages() + " 页");
        appendGridItem(sb, "&#128202; 总记录", crawl.getTotalRecords() + " 条");
        appendGridItem(sb, "&#128375; 爬取数量", crawl.getCrawledCount() + " 条");
        appendGridItem(sb, "&#128190; 保存数量", crawl.getSavedCount() + " 条");
        appendGridItem(sb, "&#10060; 失败页数", "<span style='color:#ff4d4f'>" + crawl.getFailedPages() + " 页</span>");
        appendGridItem(sb, "&#128336; 开始时间", crawl.getStartTime() != null ? crawl.getStartTime().format(FMT) : "-");
        sb.append("</div>");

        if (crawl.getErrorMessage() != null && !crawl.getErrorMessage().isBlank()) {
            sb.append("<div style='margin-top:10px;color:#ff4d4f;font-size:12px'>&#9888; ").append(crawl.getErrorMessage()).append("</div>");
        }
        sb.append("</div></div>"); // end 爬取卡片

        // ========== 统计卡片 ==========
        if (stats != null) {
            sb.append(buildStatsCard(stats));
        }

        // ========== 推荐任务列表 ==========
        sb.append("<div class='section'>");
        sb.append("<h3 class='section-title'>&#127919; 推荐任务列表</h3>");
        if (tasks == null || tasks.isEmpty()) {
            sb.append("<div style='text-align:center;padding:28px;color:#999;font-size:13px'>暂无推荐任务数据</div>");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                sb.append(buildTaskCard(tasks.get(i), i + 1));
            }
        }
        sb.append("</div>");

        // ========== 中奖规则 ==========
        sb.append(buildPrizeRuleTable());

        // ========== 底部 ==========
        sb.append("<div style='background:#fafafa;border:1px solid #e8e8e8;border-top:none;border-radius:0 0 12px 12px;padding:14px 20px;text-align:center'>")
                .append("<p style='color:#bbb;font-size:11px;margin:0'>发送时间：")
                .append(LocalDateTime.now().format(FMT))
                .append(" | 此邮件由系统自动发出，请勿回复</p>")
                .append("</div>");

        sb.append("</div></body></html>");
        return sb.toString();
    }

    // ==================== 统计卡片 ====================

    private static String buildStatsCard(UserStatistics stats) {
        int totalTasks = stats.getTotalTasks() == null ? 0 : stats.getTotalTasks();
        int hitTasks   = stats.getHitTasksCount() == null ? 0 : stats.getHitTasksCount();
        double rate    = totalTasks == 0 ? 0 : (hitTasks * 100.0 / totalTasks);
        String rateColor = rate >= 10 ? "#52c41a" : rate >= 5 ? "#fa8c16" : "#aaa";

        List<UserStatistics.PrizeLevel> prizeLevelInfo = stats.getPrizeLevelInfo();
        UserStatistics.HitDetails details = stats.getHitDetails();

        StringBuilder sb = new StringBuilder();
        sb.append("<div class='section'>");
        sb.append("<h3 class='section-title'>&#128200; 预测准确性统计</h3>");
        sb.append("<p style='font-size:13px;color:#555;margin:0 0 12px'>用户：<b>").append(stats.getUserId()).append("</b></p>");

        // 指标行
        sb.append("<div class='metric-row'>");
        appendMetricBox(sb, "总任务", totalTasks + "", "期", "#1677ff", "#e6f4ff");
        appendMetricBox(sb, "命中期数", hitTasks + "", "期", "#52c41a", "#f6ffed");
        appendMetricBox(sb, "命中率", String.format("%.1f%%", rate), "", rateColor, "#fffbe6");
        sb.append("</div>");

        if (details != null) {
            sb.append("<p style='font-size:12px;color:#888;margin:10px 0 4px'>")
                    .append("开奖批次：<b style='color:#333'>").append(nullToDash(details.getActualBatch())).append("</b>")
                    .append(" &nbsp;|&nbsp; 开奖号码：<b style='color:#e63946'>").append(nullToDash(details.getActualNum())).append("</b>")
                    .append("</p>");

            List<UserStatistics.HitLevel> list = details.getHitLevelList();
            if (list != null && !list.isEmpty()) {
                sb.append("<p style='font-size:12px;color:#888;margin:12px 0 6px'>上期命中明细：</p>");
                sb.append("<div style='overflow-x:auto'>");
                sb.append("<table class='std'><thead><tr>")
                        .append("<th>奖级</th><th>红球命中</th><th>蓝球命中</th>")
                        .append("<th>推荐红球</th><th>推荐蓝球</th>")
                        .append("</tr></thead><tbody>");
                for (UserStatistics.HitLevel lv : list) {
                    String color = getLevelColor(lv.getPrizeLevel());
                    sb.append("<tr>")
                            .append("<td style='text-align:center;color:#fff;background:").append(color).append("'>").append(nullToDash(lv.getPrizeLevel())).append("</td>")
                            .append("<td style='text-align:center'>").append(nullToZero(lv.getHitRedCount())).append("</td>")
                            .append("<td style='text-align:center'>").append(nullToZero(lv.getHitBlueCount())).append("</td>")
                            .append("<td>").append(nullToDash(lv.getRedBall())).append("</td>")
                            .append("<td>").append(nullToDash(lv.getBlueBall())).append("</td>")
                            .append("</tr>");
                }
                sb.append("</tbody></table></div>");
            }

            // 历史奖级统计
            if (prizeLevelInfo != null && !prizeLevelInfo.isEmpty()) {
                sb.append("<p style='font-size:12px;color:#888;margin:14px 0 6px'>历史奖级统计：</p>");
                sb.append("<div style='display:flex;flex-wrap:wrap;gap:8px'>");
                for (UserStatistics.PrizeLevel pl : prizeLevelInfo) {
                    String color = getLevelColor(pl.getPrizeLevel());
                    sb.append("<div style='background:").append(color)
                            .append(";color:#fff;border-radius:6px;padding:6px 14px;font-size:13px;text-align:center'>")
                            .append("<div style='font-size:11px;opacity:.85'>").append(nullToDash(pl.getPrizeLevel())).append("</div>")
                            .append("<div style='font-weight:bold;font-size:16px'>").append(pl.getCount() == null ? 0 : pl.getCount()).append("</div>")
                            .append("<div style='font-size:10px;opacity:.75'>次</div>")
                            .append("</div>");
                }
                sb.append("</div>");
            }
        }

        sb.append("</div>");
        return sb.toString();
    }

    // ==================== 推荐任务卡片 ====================

    private static String buildTaskCard(DltRecommendTaskEntity t, int index) {
        String strategyLabel = switch (t.getRecommendStrategy() == null ? "" : t.getRecommendStrategy()) {
            case "DAILY"   -> "日推荐";
            case "WEEKLY"  -> "周推荐";
            case "MONTHLY" -> "月推荐";
            case "ALL"     -> "全量推荐";
            default        -> t.getRecommendStrategy() != null ? t.getRecommendStrategy() : "-";
        };

        String drawedBadge = Boolean.TRUE.equals(t.getIsDrawed())
                ? "<span class='badge badge-red'>已开奖</span>"
                : "<span class='badge badge-blue'>待开奖</span>";

        Object[][] groups = {
                {t.getRedBalls1(), t.getBlueBalls1(), t.getPrizeLevel1(), t.getHitRedCount1(), t.getHitBlueCount1(), 1},
                {t.getRedBalls2(), t.getBlueBalls2(), t.getPrizeLevel2(), t.getHitRedCount2(), t.getHitBlueCount2(), 2},
                {t.getRedBalls3(), t.getBlueBalls3(), t.getPrizeLevel3(), t.getHitRedCount3(), t.getHitBlueCount3(), 3},
                {t.getRedBalls4(), t.getBlueBalls4(), t.getPrizeLevel4(), t.getHitRedCount4(), t.getHitBlueCount4(), 4},
        };

        StringBuilder rows = new StringBuilder();
        for (Object[] g : groups) {
            int no     = (int) g[5];
            boolean isBest = t.getBestRecommendNo() != null && t.getBestRecommendNo() == no;
            String prizeLevel = (String) g[2];
            boolean isWon = prizeLevel != null && !prizeLevel.isBlank() && !"未中奖".equals(prizeLevel);
            String hitInfo = Boolean.TRUE.equals(t.getIsDrawed())
                    ? "红" + (g[3] != null ? g[3] : 0) + " 蓝" + (g[4] != null ? g[4] : 0)
                    : "-";

            String rowStyle = isBest ? " class='hit-row'" : "";
            String bestTag  = isBest ? "<span style='color:#faad14;font-size:10px'> &#11088;</span>" : "";

            rows.append("<tr").append(rowStyle).append(">")
                    .append("<td style='text-align:center;color:#888;font-size:11px;white-space:nowrap'>推荐").append(no).append(bestTag).append("</td>")
                    .append("<td>").append(renderBalls((String) g[0], "#ff4d4f")).append("</td>")
                    .append("<td>").append(renderBalls((String) g[1], "#1677ff")).append("</td>")
                    .append("<td style='text-align:center;font-size:11px;color:#555;white-space:nowrap'>").append(hitInfo).append("</td>")
                    .append("<td style='text-align:center;font-size:11px;white-space:nowrap;color:").append(isWon ? "#e63946" : "#bbb").append("'>")
                    .append(prizeLevel != null && !prizeLevel.isBlank() ? prizeLevel : "-")
                    .append("</td></tr>");
        }

        StringBuilder card = new StringBuilder();
        card.append("<div class='card'>");

        // 卡片头
        card.append("<div class='card-header' style='display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:4px'>")
                .append("<span style='font-weight:bold;color:#c1121f;font-size:13px'>#").append(index)
                .append("  批次：").append(t.getRecommendBatch() != null ? t.getRecommendBatch() : "-").append("</span>")
                .append("<span style='font-size:11px;color:#888;display:flex;gap:6px;align-items:center'>")
                .append(strategyLabel).append(" · 分析 ")
                .append(t.getAnalyzedPeriods() != null ? t.getAnalyzedPeriods() : "-")
                .append("期 ").append(drawedBadge).append("</span>")
                .append("</div>");

        // 球号表格（overflow 横滑）
        card.append("<div style='overflow-x:auto'>")
                .append("<table class='std'><thead><tr>")
                .append("<th style='width:52px'>推荐</th>")
                .append("<th>前区红球</th>")
                .append("<th>后区蓝球</th>")
                .append("<th style='width:56px'>命中</th>")
                .append("<th style='width:56px'>奖级</th>")
                .append("</tr></thead><tbody>")
                .append(rows)
                .append("</tbody></table></div>");

        // 卡片底
        card.append("<div class='card-footer'>")
                .append("目标期号：<b style='color:#555'>").append(t.getTargetDrawNum() != null ? t.getTargetDrawNum() : "-").append("</b>")
                .append(" · 推荐时间：").append(t.getRecommendTime() != null ? t.getRecommendTime().format(FMT) : "-")
                .append(" · 用户：").append(t.getUserName() != null ? t.getUserName() : "-");
        if (Boolean.TRUE.equals(t.getIsDrawed()) && t.getActualDrawResult() != null) {
            card.append("<br>开奖结果：<b style='color:#e63946'>").append(t.getActualDrawResult()).append("</b>");
        }
        card.append("</div></div>");
        return card.toString();
    }

    // ==================== 中奖规则表 ====================

    private static String buildPrizeRuleTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class='section'>");
        sb.append("<h3 class='section-title'>&#128203; 大乐透中奖规则</h3>");
        sb.append("<div style='overflow-x:auto'>");
        sb.append("<table class='std'><thead><tr>")
                .append("<th style='width:56px'>奖级</th>")
                .append("<th>中奖条件</th>")
                .append("<th style='background:#e6f4ff;width:90px'>奖池8亿以下</th>")
                .append("<th style='background:#e6f4ff;width:90px'>奖池8亿及以上</th>")
                .append("</tr></thead><tbody>");

        appendPrizeRow(sb, "一等奖", buildBalls(5, 2),
                "浮动奖<br><span style='color:#999;font-size:10px'>最高1000万</span>",
                "浮动奖<br><span style='color:#999;font-size:10px'>最高1800万</span>");
        appendPrizeRow(sb, "二等奖", buildBalls(5, 0),
                "浮动奖",
                "浮动奖<br><span style='color:#999;font-size:10px'>追加多80%</span>");
        appendPrizeRow(sb, "三等奖", buildMultiBalls(new int[][]{{4, 2}, {5, 1}}), "5,000元", "6,666元");
        appendPrizeRow(sb, "四等奖", buildBalls(3, 2),                             "300元",   "380元");
        appendPrizeRow(sb, "五等奖", buildMultiBalls(new int[][]{{3, 1}, {4, 0}}), "150元",   "200元");
        appendPrizeRow(sb, "六等奖", buildMultiBalls(new int[][]{{2, 2}, {3, 0}}), "15元",    "18元");
        appendPrizeRow(sb, "七等奖", buildMultiBalls(new int[][]{{0, 2}, {1, 2}, {2, 0}}), "5元", "7元");

        sb.append("</tbody></table></div></div>");
        return sb.toString();
    }

    private static void appendPrizeRow(StringBuilder sb, String level, String condition, String low, String high) {
        String color = getLevelColor(level);
        sb.append("<tr>")
                .append("<td style='text-align:center;color:#fff;background:").append(color).append(";white-space:nowrap'>").append(level).append("</td>")
                .append("<td style='text-align:center'>").append(condition).append("</td>")
                .append("<td style='text-align:center'>").append(low).append("</td>")
                .append("<td style='text-align:center;color:#185FA5;font-weight:bold'>").append(high).append("</td>")
                .append("</tr>");
    }

    private static String buildBalls(int red, int blue) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='display:flex;gap:3px;align-items:center;justify-content:center;flex-wrap:wrap'>");
        for (int i = 0; i < red; i++) {
            sb.append("<span style='width:14px;height:14px;border-radius:50%;background:#4a90d9;display:inline-block'></span>");
        }
        for (int i = 0; i < blue; i++) {
            sb.append("<span style='width:14px;height:14px;border-radius:50%;background:#f5a623;display:inline-block'></span>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    private static String buildMultiBalls(int[][] combos) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='display:flex;flex-direction:column;gap:3px'>");
        for (int[] combo : combos) {
            sb.append(buildBalls(combo[0], combo[1]));
        }
        sb.append("</div>");
        return sb.toString();
    }

    // ==================== 工具方法 ====================

    private static String renderBalls(String ballStr, String color) {
        if (ballStr == null || ballStr.isBlank()) return "-";
        StringBuilder html = new StringBuilder();
        html.append("<div class='ball-wrap'>");
        for (String num : ballStr.split(",")) {
            html.append("<span class='ball' style='background:").append(color).append("'>")
                    .append(num.trim()).append("</span>");
        }
        html.append("</div>");
        return html.toString();
    }

    private static void appendMetricBox(StringBuilder sb, String label, String value, String unit,
                                        String color, String bg) {
        sb.append("<div class='metric-box' style='background:").append(bg).append("'>")
                .append("<div class='metric-label'>").append(label).append("</div>")
                .append("<div class='metric-value' style='color:").append(color).append("'>")
                .append(value)
                .append("<span class='metric-unit'>").append(unit).append("</span>")
                .append("</div></div>");
    }

    /** 两列 grid 条目 */
    private static void appendGridItem(StringBuilder sb, String key, String value) {
        sb.append("<div style='background:#fafafa;border-radius:6px;padding:8px 10px'>")
                .append("<div style='font-size:11px;color:#aaa;margin-bottom:3px'>").append(key).append("</div>")
                .append("<div style='font-size:13px;color:#333'>").append(value).append("</div>")
                .append("</div>");
    }

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

    private static String nullToDash(String s)  { return s == null ? "-" : s; }
    private static int    nullToZero(Integer i)  { return i == null ? 0 : i; }
}