package top.tangtian.balofhope.dlt.analyze.service.analysisstorage.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.tangtian.balofhope.dlt.analyze.entity.DltPrizeStatisticsEntity;
import top.tangtian.balofhope.dlt.analyze.mapper.DltPrizeStatisticsMapper;
import top.tangtian.balofhope.dlt.analyze.service.analysisstorage.IDltPrizeStatisticsService;

/**
 * @author tangtian
 * @date 2026-01-23 09:21
 */
@Service
public class DltPrizeStatisticsServiceImpl extends ServiceImpl<DltPrizeStatisticsMapper, DltPrizeStatisticsEntity>
		implements IDltPrizeStatisticsService {}
