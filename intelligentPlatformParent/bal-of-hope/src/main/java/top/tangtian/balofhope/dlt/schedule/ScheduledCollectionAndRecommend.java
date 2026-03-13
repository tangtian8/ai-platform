package top.tangtian.balofhope.dlt.schedule;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import top.tangtian.balofhope.dlt.colletcion.DltCollection;
import top.tangtian.balofhope.dlt.colletcion.model.CrawlResult;
import top.tangtian.balofhope.dlt.emailtool.DltEmailBuilder;
import top.tangtian.balofhope.dlt.emailtool.EmailService;
import top.tangtian.balofhope.dlt.recommend.entity.DltRecommendTaskEntity;
import top.tangtian.balofhope.dlt.recommend.service.DltRecommendTaskGenerator;
import top.tangtian.balofhope.dlt.recommend.service.DltRecommendTaskService;
import top.tangtian.balofhope.dlt.recommend.service.DltRecommendVerifyService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: ai-platform
 * @description: 采集和推荐任务
 * @author: tangtian
 * @create: 2026-03-12 16:23
 **/
@Service
public class ScheduledCollectionAndRecommend {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledGenerateReport.class);

    @Autowired
    private DltCollection collection;

    @Autowired
    private DltRecommendTaskGenerator dltRecommendTaskGenerator;

    @Autowired
    private DltRecommendVerifyService dltRecommendVerifyService;

    @Autowired
    private DltRecommendTaskService recommendTaskService;

    @Autowired
    private EmailService emailService;


    @Value("${spring.mail.username}")
    private String sender;


    private final static Long USERID = 1L;
    @Scheduled(cron = "0 0 22 * * 1,3,6")
    public void collectionAndRecommend(){
        //拉起最新的一期
        CrawlResult crawlResult = collection.crawlLatestData();
        //验证上一期的推荐任务
        dltRecommendVerifyService.verifyUserTasks(USERID);
        //获取推荐的正确性
        Map<String, Object> userStatistics = getUserStatistics(USERID);
        //生成最新的推荐任务
        dltRecommendTaskGenerator.generateRecommendTask(USERID,"唐甜", 100, "DAILY");
        //查询最新的推荐任务
        List<DltRecommendTaskEntity> userRecommends = getUserRecommends(USERID, 1);
        String html = DltEmailBuilder.build(crawlResult, userRecommends,userStatistics);
        emailService.sendHtml(sender,"来财来财",html);
    }


    public Map<String, Object> getUserStatistics(Long userId) {
        QueryWrapper<DltRecommendTaskEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("is_drawed", true)
                .eq("is_deleted", 0);

        List<DltRecommendTaskEntity> tasks = recommendTaskService.list(wrapper);

        Map<String, Object> stats = new HashMap<>();
        stats.put("userId", userId);
        stats.put("totalTasks", tasks.size());

        int prizeTasks = 0;
        Map<String, Integer> prizeLevelCount = new HashMap<>();

        for (DltRecommendTaskEntity task : tasks) {
            String[] levels = {task.getPrizeLevel1(), task.getPrizeLevel2(),
                    task.getPrizeLevel3(), task.getPrizeLevel4()};

            for (String level : levels) {
                if (level != null && !level.equals("未中奖")) {
                    prizeTasks++;
                    prizeLevelCount.put(level, prizeLevelCount.getOrDefault(level, 0) + 1);
                }
            }
        }

        stats.put("prizeTasks", prizeTasks);
        stats.put("prizeRate", tasks.size() > 0 ? (prizeTasks * 100.0 / (tasks.size() * 4)) : 0);
        stats.put("prizeLevelCount", prizeLevelCount);

        return stats;
    }


    public List<DltRecommendTaskEntity> getUserRecommends(
             Long userId,
             Integer limit) {

        QueryWrapper<DltRecommendTaskEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("is_deleted", 0)
                .orderByDesc("recommend_time")
                .last("LIMIT " + limit);

        return recommendTaskService.list(wrapper);
    }
}
