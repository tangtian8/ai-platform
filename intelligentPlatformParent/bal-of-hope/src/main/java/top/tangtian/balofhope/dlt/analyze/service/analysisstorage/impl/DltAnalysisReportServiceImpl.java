package top.tangtian.balofhope.dlt.analyze.service.analysisstorage.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.tangtian.balofhope.dlt.analyze.entity.DltAnalysisReportEntity;
import top.tangtian.balofhope.dlt.analyze.mapper.DltAnalysisReportMapper;
import top.tangtian.balofhope.dlt.analyze.service.analysisstorage.IDltAnalysisReportService;

/**
 * @author tangtian
 * @date 2026-01-23 09:22
 */
@Service
public class DltAnalysisReportServiceImpl extends ServiceImpl<DltAnalysisReportMapper, DltAnalysisReportEntity>
		implements IDltAnalysisReportService {}