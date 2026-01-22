-- ============================================================
-- 大乐透数据库 - PostgreSQL 建表脚本
-- ============================================================

-- 创建数据库（如果不存在）
-- CREATE DATABASE lottery_db WITH ENCODING 'UTF8';

-- 连接到数据库
-- \c lottery_db;

-- ============================================================
-- 1. 大乐透开奖主表
-- ============================================================
CREATE TABLE IF NOT EXISTS dlt_lottery_draw (
    id BIGSERIAL PRIMARY KEY,                                      -- 主键ID（自增）
    lottery_draw_num VARCHAR(20) NOT NULL UNIQUE,                 -- 期号（唯一）
    lottery_game_name VARCHAR(50),                                -- 彩票游戏名称
    lottery_game_num VARCHAR(10),                                 -- 彩票游戏编号
    lottery_draw_result VARCHAR(100),                             -- 开奖结果（排序后）
    lottery_unsort_drawresult VARCHAR(100),                       -- 开奖结果（未排序）
    lottery_draw_time VARCHAR(30),                                -- 开奖时间
    lottery_sale_begin_time VARCHAR(30),                          -- 销售开始时间
    lottery_sale_endtime VARCHAR(30),                             -- 销售结束时间
    lottery_paid_begin_time VARCHAR(30),                          -- 兑奖开始时间
    lottery_paid_end_time VARCHAR(30),                            -- 兑奖结束时间
    pool_balance_afterdraw VARCHAR(50),                           -- 开奖后奖池余额
    draw_flow_fund VARCHAR(50),                                   -- 开奖流动基金
    total_sale_amount VARCHAR(50),                                -- 总销售额
    lottery_draw_status INTEGER,                                  -- 开奖状态
    lottery_suspended_flag INTEGER,                               -- 暂停销售标志
    draw_pdf_url VARCHAR(500),                                    -- 开奖PDF链接
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,              -- 创建时间
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,              -- 更新时间
    is_deleted SMALLINT DEFAULT 0                                 -- 逻辑删除 0-未删除 1-已删除
);

-- 添加注释
COMMENT ON TABLE dlt_lottery_draw IS '大乐透开奖主表';
COMMENT ON COLUMN dlt_lottery_draw.id IS '主键ID';
COMMENT ON COLUMN dlt_lottery_draw.lottery_draw_num IS '期号';
COMMENT ON COLUMN dlt_lottery_draw.lottery_game_name IS '彩票游戏名称';
COMMENT ON COLUMN dlt_lottery_draw.lottery_game_num IS '彩票游戏编号';
COMMENT ON COLUMN dlt_lottery_draw.lottery_draw_result IS '开奖结果（排序后）';
COMMENT ON COLUMN dlt_lottery_draw.lottery_unsort_drawresult IS '开奖结果（未排序）';
COMMENT ON COLUMN dlt_lottery_draw.lottery_draw_time IS '开奖时间';
COMMENT ON COLUMN dlt_lottery_draw.lottery_sale_begin_time IS '销售开始时间';
COMMENT ON COLUMN dlt_lottery_draw.lottery_sale_endtime IS '销售结束时间';
COMMENT ON COLUMN dlt_lottery_draw.lottery_paid_begin_time IS '兑奖开始时间';
COMMENT ON COLUMN dlt_lottery_draw.lottery_paid_end_time IS '兑奖结束时间';
COMMENT ON COLUMN dlt_lottery_draw.pool_balance_afterdraw IS '开奖后奖池余额';
COMMENT ON COLUMN dlt_lottery_draw.draw_flow_fund IS '开奖流动基金';
COMMENT ON COLUMN dlt_lottery_draw.total_sale_amount IS '总销售额';
COMMENT ON COLUMN dlt_lottery_draw.lottery_draw_status IS '开奖状态';
COMMENT ON COLUMN dlt_lottery_draw.lottery_suspended_flag IS '暂停销售标志';
COMMENT ON COLUMN dlt_lottery_draw.draw_pdf_url IS '开奖PDF链接';
COMMENT ON COLUMN dlt_lottery_draw.create_time IS '创建时间';
COMMENT ON COLUMN dlt_lottery_draw.update_time IS '更新时间';
COMMENT ON COLUMN dlt_lottery_draw.is_deleted IS '逻辑删除 0-未删除 1-已删除';

-- 创建索引
CREATE INDEX idx_dlt_draw_time ON dlt_lottery_draw(lottery_draw_time);
CREATE INDEX idx_dlt_create_time ON dlt_lottery_draw(create_time);
CREATE INDEX idx_dlt_is_deleted ON dlt_lottery_draw(is_deleted);

-- ============================================================
-- 2. 大乐透奖项等级表
-- ============================================================
CREATE TABLE IF NOT EXISTS dlt_prize_level (
    id BIGSERIAL PRIMARY KEY,                                      -- 主键ID（自增）
    lottery_draw_num VARCHAR(20) NOT NULL,                        -- 期号（关联主表）
    prize_level VARCHAR(50),                                      -- 奖项等级名称
    stake_count VARCHAR(20),                                      -- 中奖注数
    stake_amount VARCHAR(50),                                     -- 单注奖金
    stake_amount_format VARCHAR(50),                              -- 单注奖金（不带格式）
    total_prizeamount VARCHAR(50),                                -- 总奖金
    sort INTEGER,                                                 -- 排序
    award_type INTEGER,                                           -- 奖项类型
    group_code VARCHAR(20),                                       -- 分组
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,              -- 创建时间
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,              -- 更新时间
    is_deleted SMALLINT DEFAULT 0                                 -- 逻辑删除
);

-- 添加注释
COMMENT ON TABLE dlt_prize_level IS '大乐透奖项等级表';
COMMENT ON COLUMN dlt_prize_level.id IS '主键ID';
COMMENT ON COLUMN dlt_prize_level.lottery_draw_num IS '期号';
COMMENT ON COLUMN dlt_prize_level.prize_level IS '奖项等级名称';
COMMENT ON COLUMN dlt_prize_level.stake_count IS '中奖注数';
COMMENT ON COLUMN dlt_prize_level.stake_amount IS '单注奖金';
COMMENT ON COLUMN dlt_prize_level.stake_amount_format IS '单注奖金（不带格式）';
COMMENT ON COLUMN dlt_prize_level.total_prizeamount IS '总奖金';
COMMENT ON COLUMN dlt_prize_level.sort IS '排序';
COMMENT ON COLUMN dlt_prize_level.award_type IS '奖项类型';
COMMENT ON COLUMN dlt_prize_level.group_code IS '分组';
COMMENT ON COLUMN dlt_prize_level.create_time IS '创建时间';
COMMENT ON COLUMN dlt_prize_level.update_time IS '更新时间';
COMMENT ON COLUMN dlt_prize_level.is_deleted IS '逻辑删除';

-- 创建索引
CREATE INDEX idx_prize_draw_num ON dlt_prize_level(lottery_draw_num);
CREATE INDEX idx_prize_sort ON dlt_prize_level(sort);
CREATE INDEX idx_prize_is_deleted ON dlt_prize_level(is_deleted);

-- ============================================================
-- 3. 创建更新时间自动触发器函数
-- ============================================================
CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 为主表创建触发器
CREATE TRIGGER update_dlt_lottery_draw_modtime
    BEFORE UPDATE ON dlt_lottery_draw
    FOR EACH ROW
    EXECUTE FUNCTION update_modified_column();

-- 为奖项表创建触发器
CREATE TRIGGER update_dlt_prize_level_modtime
    BEFORE UPDATE ON dlt_prize_level
    FOR EACH ROW
    EXECUTE FUNCTION update_modified_column();

-- ============================================================
-- 4. 创建外键约束（可选，根据需求决定是否启用）
-- ============================================================
-- ALTER TABLE dlt_prize_level
--     ADD CONSTRAINT fk_prize_draw
--     FOREIGN KEY (lottery_draw_num)
--     REFERENCES dlt_lottery_draw(lottery_draw_num)
--     ON DELETE CASCADE;
