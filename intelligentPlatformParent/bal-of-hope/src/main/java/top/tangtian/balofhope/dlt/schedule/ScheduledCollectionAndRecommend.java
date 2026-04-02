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
import top.tangtian.balofhope.dlt.recommend.model.UserStatistics;
import top.tangtian.balofhope.dlt.recommend.service.DltRecommendTaskGenerator;
import top.tangtian.balofhope.dlt.recommend.service.DltRecommendTaskService;
import top.tangtian.balofhope.dlt.recommend.service.DltRecommendVerifyService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        UserStatistics userStatistics = getUserStatistics(USERID);
        //生成最新的推荐任务
        dltRecommendTaskGenerator.generateRecommendTask(USERID,"唐甜", 100, "DAILY");
        //查询最新的推荐任务
        List<DltRecommendTaskEntity> userRecommends = getUserRecommends(USERID, 1);
        String html = DltEmailBuilder.build(crawlResult, userRecommends,userStatistics);
        emailService.sendHtml(sender,"来财来财",html);
    }


    public UserStatistics getUserStatistics(Long userId) {
        QueryWrapper<DltRecommendTaskEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("is_drawed", true)
                .eq("is_deleted", 0).orderByDesc("actual_draw_num");

        List<DltRecommendTaskEntity> tasks = recommendTaskService.list(wrapper);

        Map<String, Object> stats = new HashMap<>();
        stats.put("userId", userId);
        stats.put("totalTasks", tasks.size());

        UserStatistics userStatistics = new UserStatistics();


        // 统计中奖的期数
        int hitTasksCount = 0;
        UserStatistics.HitDetails hitDetails = new UserStatistics.HitDetails();

        for (int i = 0; i < tasks.size(); i++) {
            DltRecommendTaskEntity task = tasks.get(i);
            List<UserStatistics.HitLevel> hitLevelList = buildHitLevelList(task);

            boolean hasHit = hitLevelList.stream()
                    .anyMatch(v -> !"未中奖".equals(v.getPrizeLevel()));
            if (hasHit) {
                hitTasksCount++;
            }

            // 顺便处理最新一期（第一条）
            if (i == 0) {
                hitDetails.setActualBatch(task.getActualDrawNum());
                hitDetails.setActualNum(task.getActualDrawResult());
                hitDetails.setHitLevelList(hitLevelList);
            }
        }

        userStatistics.setUserId(userId);
        userStatistics.setTotalTasks(tasks.size());
        userStatistics.setHitTasksCount(hitTasksCount);
        userStatistics.setHitDetails(hitDetails);
        userStatistics.setPrizeLevelInfo(countPrizeLevels(tasks));

        return userStatistics;
    }

    public List<UserStatistics.PrizeLevel> countPrizeLevels(List<DltRecommendTaskEntity> tasks) {
        List<String> PRIZE_ORDER = Arrays.asList(
                "一等奖", "二等奖", "三等奖", "四等奖", "五等奖", "六等奖", "七等奖"
        );

        Map<String, Long> prizeStatistics = tasks.stream()
                .flatMap(task -> Stream.of(
                                task.getPrizeLevel1(),
                                task.getPrizeLevel2(),
                                task.getPrizeLevel3(),
                                task.getPrizeLevel4()
                        )
                        .filter(level -> level != null && !"未中奖".equals(level))
                        .distinct())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return PRIZE_ORDER.stream()
                .filter(prizeStatistics::containsKey)
                .map(level -> UserStatistics.PrizeLevel.builder()
                        .prizeLevel(level)
                        .count(prizeStatistics.get(level).intValue())
                        .build())
                .collect(Collectors.toList());
    }


    // 抽取方法
    private List<UserStatistics.HitLevel> buildHitLevelList(DltRecommendTaskEntity task) {
        return Arrays.asList(
                build(task.getPrizeLevel1(), task.getHitRedCount1(), task.getHitBlueCount1(), task.getRedBalls1(), task.getBlueBalls1()),
                build(task.getPrizeLevel2(), task.getHitRedCount2(), task.getHitBlueCount2(), task.getRedBalls2(), task.getBlueBalls2()),
                build(task.getPrizeLevel3(), task.getHitRedCount3(), task.getHitBlueCount3(), task.getRedBalls3(), task.getBlueBalls3()),
                build(task.getPrizeLevel4(), task.getHitRedCount4(), task.getHitBlueCount4(), task.getRedBalls4(), task.getBlueBalls4())
        );
    }



    private UserStatistics.HitLevel build(String level, Integer red, Integer blue, String redBall, String blueBall) {
        return UserStatistics.HitLevel.builder()
                .prizeLevel(level)
                .hitRedCount(red)
                .hitBlueCount(blue)
                .redBall(redBall)
                .blueBall(blueBall)
                .build();
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
