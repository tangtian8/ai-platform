-- 启用 pgvector 扩展
CREATE EXTENSION IF NOT EXISTS vector;

-- 创建向量存储表
CREATE TABLE IF NOT EXISTS vector_store (
                                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content TEXT NOT NULL,
    metadata JSONB,
    embedding vector(384),  -- 维度根据 embedding 模型调整
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- 创建 HNSW 索引以加速向量检索
CREATE INDEX IF NOT EXISTS vector_store_embedding_idx
    ON vector_store USING hnsw (embedding vector_cosine_ops);

-- 创建会话表
CREATE TABLE IF NOT EXISTS conversation_sessions (
                                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id VARCHAR(255) NOT NULL,
    session_id VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT true,
    metadata JSONB
    );

-- 创建消息历史表
CREATE TABLE IF NOT EXISTS message_history (
                                               id BIGSERIAL PRIMARY KEY,
                                               session_id VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,  -- user, assistant, system
    content TEXT NOT NULL,
    token_count INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    metadata JSONB,
    FOREIGN KEY (session_id) REFERENCES conversation_sessions(session_id) ON DELETE CASCADE
    );

-- 创建记忆摘要表
CREATE TABLE IF NOT EXISTS memory_summaries (
                                                id BIGSERIAL PRIMARY KEY,
                                                session_id VARCHAR(255) NOT NULL,
    summary TEXT NOT NULL,
    start_message_id BIGINT,
    end_message_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES conversation_sessions(session_id) ON DELETE CASCADE
    );

-- 创建用户知识库表
CREATE TABLE IF NOT EXISTS user_knowledge (
                                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id VARCHAR(255) NOT NULL,
    title VARCHAR(500),
    content TEXT NOT NULL,
    source VARCHAR(255),
    category VARCHAR(100),
    embedding vector(384),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    metadata JSONB
    );

-- 为用户知识库创建索引
CREATE INDEX IF NOT EXISTS user_knowledge_embedding_idx
    ON user_knowledge USING hnsw (embedding vector_cosine_ops);

CREATE INDEX IF NOT EXISTS user_knowledge_user_id_idx
    ON user_knowledge(user_id);

-- 创建索引以提高查询性能
CREATE INDEX IF NOT EXISTS message_history_session_id_idx
    ON message_history(session_id);

CREATE INDEX IF NOT EXISTS message_history_created_at_idx
    ON message_history(created_at DESC);

CREATE INDEX IF NOT EXISTS conversation_sessions_user_id_idx
    ON conversation_sessions(user_id);

-- 创建更新触发器
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_conversation_sessions_updated_at
    BEFORE UPDATE ON conversation_sessions
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_user_knowledge_updated_at
    BEFORE UPDATE ON user_knowledge
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
