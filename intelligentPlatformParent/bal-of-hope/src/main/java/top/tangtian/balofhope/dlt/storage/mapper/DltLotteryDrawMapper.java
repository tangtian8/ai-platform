package top.tangtian.balofhope.dlt.storage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import top.tangtian.balofhope.dlt.storage.entity.DltLotteryDrawEntity;

/**
 * @author tangtian
 * @date 2026-01-21 16:42
 */
@Mapper
@Repository
public interface DltLotteryDrawMapper extends BaseMapper<DltLotteryDrawEntity> {

	/**
	 * 查询最新期号
	 */
	@Select("SELECT lottery_draw_num FROM dlt_lottery_draw " +
			"WHERE is_deleted = 0 " +
			"ORDER BY lottery_draw_num DESC LIMIT 1")
	String findLatestDrawNum();

	/**
	 * 根据期号查询
	 */
	@Select("SELECT * FROM dlt_lottery_draw " +
			"WHERE lottery_draw_num = #{drawNum} AND is_deleted = 0")
	DltLotteryDrawEntity findByDrawNum(String drawNum);
}