package top.tangtian.balofhope.dlt.storage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import top.tangtian.balofhope.dlt.storage.entity.DltPrizeLevelEntity;

import java.util.List;

/**
 * @author tangtian
 * @date 2026-01-21 16:42
 */
@Mapper
@Repository
public interface DltPrizeLevelMapper extends BaseMapper<DltPrizeLevelEntity> {

	/**
	 * 根据期号删除奖项数据
	 */
	@Select("DELETE FROM dlt_prize_level WHERE lottery_draw_num = #{drawNum}")
	void deleteByDrawNum(String drawNum);

	/**
	 * 根据期号查询奖项列表
	 */
	@Select("SELECT * FROM dlt_prize_level " +
			"WHERE lottery_draw_num = #{drawNum} AND is_deleted = 0 " +
			"ORDER BY sort ASC")
	List<DltPrizeLevelEntity> findByDrawNum(String drawNum);
}
