# 私人 AI 智能体系统 v3.0 - 企业级版本

🎉 **重磅发布！** 完整实现 5 大企业级功能的 AI 智能体系统

基于 Spring AI + DeepSeek + PostgreSQL 向量库构建的私人智能助手系统，支持长对话记忆管理、个性化知识库、工具调用链等企业级功能。

## 🌟 v3.0 核心亮点

### ✅ 已完整实现的 5 大功能

1. **对话记忆管理** - 三层架构，支持 1000+ 轮对话
2. **文本向量化** - 双引擎，DeepSeek + 本地模型
3. **提示词工厂** - 场景化模板，动态组合
4. **Tools 调用链** - 强大编排，支持并行执行
5. **MCP 模型生命周期** - 智能缓存，版本管理

## 核心特性

### 🧠 智能记忆管理
- **三层记忆架构**:
  - 短期记忆: 保留最近 10 轮对话
  - 长期记忆: 自动压缩历史对话为摘要
  - 知识库记忆: 用户上传的个人资料
  
- **支持 1000+ 轮对话**: 通过自动压缩和摘要技术,突破上下文窗口限制
- **智能压缩**: 每 100 轮对话自动压缩,保留关键信息

### 📚 个人知识库
- 支持文本、文件导入
- 基于向量语义检索
- 自动分块和 Embedding
- 智能引用来源

### 🎯 优化的嵌入模型
- **DeepSeek Embedding**: 高精度，1024维，支持中文
- **本地 Transformers**: 离线可用，384维，零成本
- 灵活配置，支持切换

### 💬 智能提示词系统
- **多层次提示词管理**: 系统提示词、知识库增强、记忆压缩等
- **场景化模板**: 针对不同场景优化的提示词
- **可自定义**: 通过配置文件轻松调整
- **查询优化**: 自动优化用户查询提高检索效果

### 🔍 增强的 RAG 检索
- **查询扩展**: 自动扩展查询关键词
- **重排序**: 基于语义相似度重新排序
- **去重过滤**: 自动去除重复内容
- **可配置阈值**: 灵活调整检索精度

### 🚀 技术架构
- **LLM**: DeepSeek (OpenAI 兼容接口)
- **向量数据库**: PostgreSQL + pgvector
- **框架**: Spring Boot 3.2 + Spring AI
- **记忆管理**: 自研多层记忆系统
- **Embedding**: DeepSeek API 或本地模型

## 快速开始

### 1. 环境要求
- Java 17+
- PostgreSQL 14+ (需安装 pgvector 扩展)
- Redis (可选,用于会话缓存)
- Maven 3.6+

### 2. 数据库准备

```bash
# 安装 PostgreSQL 和 pgvector
sudo apt-get install postgresql-14 postgresql-14-pgvector

# 创建数据库
createdb ai_agent_db

# 初始化表结构
psql -d ai_agent_db -f src/main/resources/schema.sql
```

### 3. 配置

编辑 `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ai_agent_db
    username: your_username
    password: your_password

# 设置环境变量
export DEEPSEEK_API_KEY=your_deepseek_api_key
```

### 4. 运行

```bash
# 编译
mvn clean package

# 运行
java -jar target/private-ai-agent-1.0.0.jar

# 或直接运行
mvn spring-boot:run
```

## API 使用示例

### 创建会话

```bash
curl -X POST http://localhost:8080/api/v1/assistant/sessions \
  -H "Content-Type: application/json" \
  -d '{"userId": "user123"}'
```

响应:
```json
{
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "message": "会话创建成功"
}
```

### 发送消息 (同步)

```bash
curl -X POST http://localhost:8080/api/v1/assistant/chat \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "sessionId": "550e8400-e29b-41d4-a716-446655440000",
    "message": "你好,请介绍一下自己"
  }'
```

### 发送消息 (流式)

```bash
curl -X POST http://localhost:8080/api/v1/assistant/chat/stream \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "sessionId": "550e8400-e29b-41d4-a716-446655440000",
    "message": "写一首关于春天的诗"
  }'
```

### 添加知识到知识库

```bash
curl -X POST http://localhost:8080/api/v1/assistant/knowledge/text \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "title": "我的项目笔记",
    "content": "这是关于 Spring AI 项目的笔记...",
    "category": "技术文档",
    "source": "个人笔记"
  }'
```

### 上传文件到知识库

```bash
curl -X POST http://localhost:8080/api/v1/assistant/knowledge/file \
  -F "userId=user123" \
  -F "file=@/path/to/document.txt" \
  -F "category=个人资料"
```

### 查询知识库

```bash
curl http://localhost:8080/api/v1/assistant/knowledge/user123
```

### 获取用户会话列表

```bash
curl http://localhost:8080/api/v1/assistant/sessions/user123
```

## 架构设计

### 记忆管理流程

```
用户消息
   ↓
保存到消息历史
   ↓
检索知识库 (向量检索)
   ↓
构建上下文:
  - 系统提示
  - 长期记忆摘要
  - 短期对话历史
  - 相关知识库内容
   ↓
调用 DeepSeek API
   ↓
保存助手回复
   ↓
检查是否需要压缩记忆
```

### 数据库表结构

- `conversation_sessions`: 会话信息
- `message_history`: 消息历史
- `memory_summaries`: 记忆摘要
- `user_knowledge`: 用户知识库
- `vector_store`: 通用向量存储

## 配置说明

### 记忆管理配置

```yaml
agent:
  memory:
    short-term-window: 10          # 短期记忆窗口大小
    summary-threshold: 50          # 触发摘要的消息数阈值
    max-history: 1000              # 最大历史消息数
    compression:
      enabled: true                # 启用自动压缩
      interval: 100                # 压缩间隔(消息数)
```

### 检索配置

```yaml
agent:
  retrieval:
    top-k: 5                       # 检索文档数量
    similarity-threshold: 0.7      # 相似度阈值
    rerank: true                   # 启用重排序
```

## 性能优化建议

1. **向量索引**: 使用 HNSW 索引加速检索
2. **Redis 缓存**: 缓存热点会话数据
3. **批量处理**: 批量添加知识时使用批量接口
4. **异步处理**: 使用流式 API 提升用户体验
5. **连接池**: 合理配置数据库连接池大小

## 扩展功能

### 计划中的功能
- [ ] 多模态支持(图片、音频)
- [ ] 工具调用(Function Calling)
- [ ] 多用户隔离和权限管理
- [ ] 对话分析和统计
- [ ] 知识图谱集成
- [ ] 自定义系统提示词
- [ ] 导出对话历史

## 📚 文档

- **[README.md](README.md)** - 项目概述和快速开始
- **[QUICKSTART.md](QUICKSTART.md)** - 详细的快速开始指南
- **[PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md)** - 项目结构和架构说明
- **[EMBEDDING_PROMPTS_GUIDE.md](EMBEDDING_PROMPTS_GUIDE.md)** - 🔥 嵌入模型与提示词优化完整指南
- **[CONFIG_EXAMPLES.md](CONFIG_EXAMPLES.md)** - 🔥 不同场景的最佳配置示例

## 常见问题

### Q: 应该选择 DeepSeek Embedding 还是本地模型?
A: 
- **DeepSeek Embedding**: 精度更高，特别适合中文场景，需要 API Key
- **本地模型**: 完全免费，离线可用，精度稍低但够用
- **建议**: 生产环境用 DeepSeek，开发测试用本地模型

详见: [EMBEDDING_PROMPTS_GUIDE.md](EMBEDDING_PROMPTS_GUIDE.md)

### Q: 如何调整 Embedding 维度?
A: 修改 `application.yml` 中的 `dimensions` 和数据库表的向量维度。
- DeepSeek: 1024 维
- 本地模型: 384 维

切换模型后需要：
```sql
ALTER TABLE user_knowledge ALTER COLUMN embedding TYPE vector(1024);
-- 重建索引
DROP INDEX user_knowledge_embedding_idx;
CREATE INDEX user_knowledge_embedding_idx ON user_knowledge USING hnsw (embedding vector_cosine_ops);
```

### Q: 如何优化提示词?
A: 
1. 查看 `src/main/java/com/assistant/service/PromptService.java`
2. 修改系统提示词、记忆压缩提示词等
3. 或编辑 `src/main/resources/prompts.yml` 配置文件

详见: [EMBEDDING_PROMPTS_GUIDE.md](EMBEDDING_PROMPTS_GUIDE.md)

### Q: 检索效果不好怎么办?
A: 调整检索参数：
```yaml
agent:
  retrieval:
    top-k: 5                    # 增加检索数量
    similarity-threshold: 0.6   # 降低阈值
    rerank: true                # 启用重排序
    query-expansion: true       # 启用查询扩展
```

参考: [CONFIG_EXAMPLES.md](CONFIG_EXAMPLES.md)

### Q: 支持哪些文件格式?
A: 目前支持纯文本文件,未来将支持 PDF、Word、Markdown 等。

### Q: 如何切换到其他 LLM?
A: Spring AI 支持多种模型,只需更改配置和依赖即可。

### Q: 记忆压缩会丢失信息吗?
A: 压缩使用 AI 生成摘要,保留关键信息,非关键细节可能简化。可以在配置中调整压缩间隔。

## 许可证

MIT License

## 贡献

欢迎提交 Issue 和 Pull Request!

## 联系方式

- GitHub: [你的 GitHub]
- Email: [你的邮箱]
