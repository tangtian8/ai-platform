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


-- ============================================================
-- 大乐透数据分析结果表 - PostgreSQL 建表脚本
-- ============================================================
--SET search_path TO "$user", dlt;
-- 1. 号码频率分析结果表
CREATE TABLE IF NOT EXISTS dlt_number_frequency (
    id BIGSERIAL PRIMARY KEY,
    batch_no VARCHAR(50) NOT NULL,
    ball_type VARCHAR(10) NOT NULL,
    number INTEGER NOT NULL,
    frequency_count INTEGER DEFAULT 0,
    frequency_percentage NUMERIC(5,2) DEFAULT 0,
    is_hot BOOLEAN DEFAULT FALSE,
    is_cold BOOLEAN DEFAULT FALSE,
    analyzed_periods INTEGER,
    analysis_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted SMALLINT DEFAULT 0
);

COMMENT ON TABLE dlt_number_frequency IS '号码频率分析结果表';
COMMENT ON COLUMN dlt_number_frequency.batch_no IS '分析批次号';
COMMENT ON COLUMN dlt_number_frequency.ball_type IS '号码类型：RED-红球, BLUE-蓝球';
COMMENT ON COLUMN dlt_number_frequency.number IS '号码';
COMMENT ON COLUMN dlt_number_frequency.frequency_count IS '出现次数';
COMMENT ON COLUMN dlt_number_frequency.frequency_percentage IS '出现百分比';
COMMENT ON COLUMN dlt_number_frequency.is_hot IS '是否热号';
COMMENT ON COLUMN dlt_number_frequency.is_cold IS '是否冷号';

CREATE INDEX idx_freq_batch ON dlt_number_frequency(batch_no);
CREATE INDEX idx_freq_type ON dlt_number_frequency(ball_type);
CREATE INDEX idx_freq_number ON dlt_number_frequency(number);
CREATE INDEX idx_freq_hot ON dlt_number_frequency(is_hot);

-- 2. 奖池趋势分析结果表
CREATE TABLE IF NOT EXISTS dlt_pool_trend (
    id BIGSERIAL PRIMARY KEY,
    draw_num VARCHAR(20) NOT NULL,
    draw_time VARCHAR(30),
    pool_balance NUMERIC(15,2),
    sales_amount NUMERIC(15,2),
    growth_rate NUMERIC(8,4),
    stat_date VARCHAR(20),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted SMALLINT DEFAULT 0
);

COMMENT ON TABLE dlt_pool_trend IS '奖池趋势分析结果表';
COMMENT ON COLUMN dlt_pool_trend.pool_balance IS '奖池余额';
COMMENT ON COLUMN dlt_pool_trend.sales_amount IS '销售额';
COMMENT ON COLUMN dlt_pool_trend.growth_rate IS '环比增长率（%）';

CREATE INDEX idx_pool_draw ON dlt_pool_trend(draw_num);
CREATE INDEX idx_pool_date ON dlt_pool_trend(stat_date);

-- 3. 奖项统计分析结果表
CREATE TABLE IF NOT EXISTS dlt_prize_statistics (
    id BIGSERIAL PRIMARY KEY,
    batch_no VARCHAR(50) NOT NULL,
    prize_level VARCHAR(50) NOT NULL,
    total_count INTEGER DEFAULT 0,
    total_amount NUMERIC(15,2) DEFAULT 0,
    avg_amount NUMERIC(12,2) DEFAULT 0,
    max_amount NUMERIC(12,2) DEFAULT 0,
    min_amount NUMERIC(12,2) DEFAULT 0,
    analyzed_periods INTEGER,
    analysis_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted SMALLINT DEFAULT 0
);

COMMENT ON TABLE dlt_prize_statistics IS '奖项统计分析结果表';
COMMENT ON COLUMN dlt_prize_statistics.total_count IS '中奖总次数';
COMMENT ON COLUMN dlt_prize_statistics.total_amount IS '中奖总金额';
COMMENT ON COLUMN dlt_prize_statistics.avg_amount IS '平均单注奖金';

CREATE INDEX idx_prize_batch ON dlt_prize_statistics(batch_no);
CREATE INDEX idx_prize_level ON dlt_prize_statistics(prize_level);

-- 4. 号码遗漏分析结果表
CREATE TABLE IF NOT EXISTS dlt_number_omission (
    id BIGSERIAL PRIMARY KEY,
    batch_no VARCHAR(50) NOT NULL,
    ball_type VARCHAR(10) NOT NULL,
    number INTEGER NOT NULL,
    current_omission INTEGER DEFAULT 0,
    avg_omission INTEGER DEFAULT 0,
    max_omission INTEGER DEFAULT 0,
    omission_level VARCHAR(10),
    analysis_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted SMALLINT DEFAULT 0
);

COMMENT ON TABLE dlt_number_omission IS '号码遗漏分析结果表';
COMMENT ON COLUMN dlt_number_omission.current_omission IS '当前遗漏值';
COMMENT ON COLUMN dlt_number_omission.avg_omission IS '平均遗漏值';
COMMENT ON COLUMN dlt_number_omission.max_omission IS '最大遗漏值';
COMMENT ON COLUMN dlt_number_omission.omission_level IS '遗漏等级：HIGH-高, MEDIUM-中, LOW-低';

CREATE INDEX idx_omission_batch ON dlt_number_omission(batch_no);
CREATE INDEX idx_omission_type ON dlt_number_omission(ball_type);
CREATE INDEX idx_omission_level ON dlt_number_omission(omission_level);

-- 5. 推荐号码记录表
CREATE TABLE IF NOT EXISTS dlt_recommend_numbers (
    id BIGSERIAL PRIMARY KEY,
    recommend_batch VARCHAR(50) NOT NULL,
    recommend_strategy VARCHAR(20) NOT NULL,
    red_numbers VARCHAR(100),
    blue_numbers VARCHAR(50),
    recommend_reason VARCHAR(200),
    recommend_time TIMESTAMP,
    is_drawed BOOLEAN DEFAULT FALSE,
    actual_draw_num VARCHAR(20),
    hit_red_count INTEGER DEFAULT 0,
    hit_blue_count INTEGER DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted SMALLINT DEFAULT 0
);

COMMENT ON TABLE dlt_recommend_numbers IS '推荐号码记录表';
COMMENT ON COLUMN dlt_recommend_numbers.recommend_strategy IS '推荐策略：HOT-热号, OMISSION-遗漏, BALANCED-均衡';
COMMENT ON COLUMN dlt_recommend_numbers.is_drawed IS '是否已开奖';
COMMENT ON COLUMN dlt_recommend_numbers.hit_red_count IS '命中红球数';
COMMENT ON COLUMN dlt_recommend_numbers.hit_blue_count IS '命中蓝球数';

CREATE INDEX idx_recommend_batch ON dlt_recommend_numbers(recommend_batch);
CREATE INDEX idx_recommend_strategy ON dlt_recommend_numbers(recommend_strategy);
CREATE INDEX idx_recommend_drawed ON dlt_recommend_numbers(is_drawed);

-- 6. 综合分析报告表
CREATE TABLE IF NOT EXISTS dlt_analysis_report (
    id BIGSERIAL PRIMARY KEY,
    report_batch VARCHAR(50) NOT NULL UNIQUE,
    report_type VARCHAR(20) NOT NULL,
    analyzed_periods INTEGER,
    current_pool NUMERIC(15,2),
    avg_sales NUMERIC(15,2),
    report_summary TEXT,
    generate_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted SMALLINT DEFAULT 0
);

COMMENT ON TABLE dlt_analysis_report IS '综合分析报告表';
COMMENT ON COLUMN dlt_analysis_report.report_type IS '报告类型：DAILY-日报, WEEKLY-周报, MONTHLY-月报, COMPREHENSIVE-综合';
COMMENT ON COLUMN dlt_analysis_report.report_summary IS '报告摘要（JSON格式）';

CREATE INDEX idx_report_batch ON dlt_analysis_report(report_batch);
CREATE INDEX idx_report_type ON dlt_analysis_report(report_type);
CREATE INDEX idx_report_time ON dlt_analysis_report(generate_time);

-- 7. 更新时间触发器
CREATE OR REPLACE FUNCTION update_analysis_modtime()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_number_frequency_modtime
    BEFORE UPDATE ON dlt_number_frequency
    FOR EACH ROW EXECUTE FUNCTION update_analysis_modtime();

CREATE TRIGGER update_pool_trend_modtime
    BEFORE UPDATE ON dlt_pool_trend
    FOR EACH ROW EXECUTE FUNCTION update_analysis_modtime();

CREATE TRIGGER update_prize_statistics_modtime
    BEFORE UPDATE ON dlt_prize_statistics
    FOR EACH ROW EXECUTE FUNCTION update_analysis_modtime();

CREATE TRIGGER update_number_omission_modtime
    BEFORE UPDATE ON dlt_number_omission
    FOR EACH ROW EXECUTE FUNCTION update_analysis_modtime();

CREATE TRIGGER update_recommend_numbers_modtime
    BEFORE UPDATE ON dlt_recommend_numbers
    FOR EACH ROW EXECUTE FUNCTION update_analysis_modtime();

CREATE TRIGGER update_analysis_report_modtime
    BEFORE UPDATE ON dlt_analysis_report
    FOR EACH ROW EXECUTE FUNCTION update_analysis_modtime();