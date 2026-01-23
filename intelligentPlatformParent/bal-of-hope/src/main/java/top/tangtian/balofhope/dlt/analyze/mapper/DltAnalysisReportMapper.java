package top.tangtian.balofhope.dlt.analyze.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import top.tangtian.balofhope.dlt.analyze.entity.DltAnalysisReportEntity;

/**
 * @author tangtian
 * @date 2026-01-23 09:13
 */
@Mapper
@Repository
public interface DltAnalysisReportMapper extends BaseMapper<DltAnalysisReportEntity> {}