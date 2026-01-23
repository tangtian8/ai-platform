package top.tangtian.balofhope.dlt.analyze.service.analysisstorage.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.tangtian.balofhope.dlt.analyze.entity.DltRecommendNumbersEntity;
import top.tangtian.balofhope.dlt.analyze.mapper.DltRecommendNumbersMapper;
import top.tangtian.balofhope.dlt.analyze.service.analysisstorage.IDltRecommendNumbersService;

/**
 * @author tangtian
 * @date 2026-01-23 09:22
 */
@Service
public class DltRecommendNumbersServiceImpl extends ServiceImpl<DltRecommendNumbersMapper, DltRecommendNumbersEntity>
		implements IDltRecommendNumbersService {}
