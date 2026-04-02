package top.tangtian.balofhope.dlt.recommend.model;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @program: ai-platform
 * @description: 推荐结果
 * @author: tangtian
 * @create: 2026-03-26 10:08
 **/
@Data
public class UserStatistics {
    //用户id
    private Long userId;
    //总任务数量
    private Integer totalTasks;
    //命中的任务数量
    private Integer hitTasksCount;
    //最新一期命中详情
    private HitDetails hitDetails;

    private List<PrizeLevel> prizeLevelInfo;

    //命中详情
    @Data
    public static class HitDetails {
        //推荐批次
        private String actualBatch;
        //实际开奖号码
        private String actualNum;
        //命中等级
        private List<HitLevel> hitLevelList;
    }

    @Data
    @Builder
    public static class HitLevel  {
        //命中等级
        private String prizeLevel;
        //命中红球数量
        private Integer hitRedCount;
        //命中篮球数量
        private Integer hitBlueCount;
        //命中红球数量
        private String redBall;
        //命中篮球数量
        private String blueBall;

    }

    @Data
    @Builder
    public static class PrizeLevel  {
        //命中等级
        private String prizeLevel;

        private Integer count;

    }
}
