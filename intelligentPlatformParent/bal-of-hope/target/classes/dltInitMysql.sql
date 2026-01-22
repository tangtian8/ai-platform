-- 创建数据库
CREATE DATABASE IF NOT EXISTS lottery_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE lottery_db;

-- 大乐透开奖主表
CREATE TABLE `dlt_lottery_draw` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `lottery_draw_num` varchar(20) NOT NULL COMMENT '期号',
  `lottery_game_name` varchar(50) DEFAULT NULL COMMENT '彩票游戏名称',
  `lottery_game_num` varchar(10) DEFAULT NULL COMMENT '彩票游戏编号',
  `lottery_draw_result` varchar(100) DEFAULT NULL COMMENT '开奖结果（排序后）',
  `lottery_unsort_drawresult` varchar(100) DEFAULT NULL COMMENT '开奖结果（未排序）',
  `lottery_draw_time` varchar(30) DEFAULT NULL COMMENT '开奖时间',
  `lottery_sale_begin_time` varchar(30) DEFAULT NULL COMMENT '销售开始时间',
  `lottery_sale_endtime` varchar(30) DEFAULT NULL COMMENT '销售结束时间',
  `lottery_paid_begin_time` varchar(30) DEFAULT NULL COMMENT '兑奖开始时间',
  `lottery_paid_end_time` varchar(30) DEFAULT NULL COMMENT '兑奖结束时间',
  `pool_balance_afterdraw` varchar(50) DEFAULT NULL COMMENT '开奖后奖池余额',
  `draw_flow_fund` varchar(50) DEFAULT NULL COMMENT '开奖流动基金',
  `total_sale_amount` varchar(50) DEFAULT NULL COMMENT '总销售额',
  `lottery_draw_status` int(11) DEFAULT NULL COMMENT '开奖状态',
  `lottery_suspended_flag` int(11) DEFAULT NULL COMMENT '暂停销售标志',
  `draw_pdf_url` varchar(500) DEFAULT NULL COMMENT '开奖PDF链接',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_draw_num` (`lottery_draw_num`),
  KEY `idx_draw_time` (`lottery_draw_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='大乐透开奖主表';

-- 大乐透奖项等级表
CREATE TABLE `dlt_prize_level` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `lottery_draw_num` varchar(20) NOT NULL COMMENT '期号',
  `prize_level` varchar(50) DEFAULT NULL COMMENT '奖项等级名称',
  `stake_count` varchar(20) DEFAULT NULL COMMENT '中奖注数',
  `stake_amount` varchar(50) DEFAULT NULL COMMENT '单注奖金',
  `stake_amount_format` varchar(50) DEFAULT NULL COMMENT '单注奖金（不带格式）',
  `total_prizeamount` varchar(50) DEFAULT NULL COMMENT '总奖金',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `award_type` int(11) DEFAULT NULL COMMENT '奖项类型',
  `group_code` varchar(20) DEFAULT NULL COMMENT '分组',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_draw_num` (`lottery_draw_num`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='大乐透奖项等级表';