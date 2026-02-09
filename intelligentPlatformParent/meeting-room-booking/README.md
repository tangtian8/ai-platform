# 🏢 智能会议室预订系统

基于 AI 的智能会议室预订系统，支持自然语言对话式预订，让会议室预订变得简单高效。

[![Spring Boot](https://private-user-images.githubusercontent.com/44994904/546866053-ee8d03bf-0ce5-463f-a4fe-09ce0fd325a2.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NzA2MDUyNjksIm5iZiI6MTc3MDYwNDk2OSwicGF0aCI6Ii80NDk5NDkwNC81NDY4NjYwNTMtZWU4ZDAzYmYtMGNlNS00NjNmLWE0ZmUtMDljZTBmZDMyNWEyLnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNjAyMDklMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjYwMjA5VDAyNDI0OVomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPTk2MWZiMGE4YmFjZjQ4NWQyNzViZTNlYTkzNGQwZGQ5MTFmMWIzODUzNGZhNmM2MDk3NTM5NjEwNmQwMzVlNzkmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.qZqopK4DyX-1vRReNLLTIFfW-duloDhhuJ9uLImSWEY)](https://spring.io/projects/spring-boot)
[![Java](https://private-user-images.githubusercontent.com/44994904/546866054-e26d6c4d-e7da-4bc6-a66f-a46b5d55692b.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NzA2MDUyNjksIm5iZiI6MTc3MDYwNDk2OSwicGF0aCI6Ii80NDk5NDkwNC81NDY4NjYwNTQtZTI2ZDZjNGQtZTdkYS00YmM2LWE2NmYtYTQ2YjVkNTU2OTJiLnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNjAyMDklMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjYwMjA5VDAyNDI0OVomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPWU2ZTAzZDQ1NzBkODM5MzRiOTQzMmM3MjE0MTE3NGY0YTMyYzY4ZDZmNTA4ZDMzN2Y3NWE3YTFhNzZjODE0YTEmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.Vt81lw9KZWzGFX3hLzKmOI_EkcOrozooB9BnkGWIU-I)](https://www.oracle.com/java/)
[![PostgreSQL](https://private-user-images.githubusercontent.com/44994904/546866056-6d4e4419-b446-4f02-9a77-5c044d442060.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NzA2MDUyNjksIm5iZiI6MTc3MDYwNDk2OSwicGF0aCI6Ii80NDk5NDkwNC81NDY4NjYwNTYtNmQ0ZTQ0MTktYjQ0Ni00ZjAyLTlhNzctNWMwNDRkNDQyMDYwLnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNjAyMDklMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjYwMjA5VDAyNDI0OVomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPWU4ZTAxZTEwZGQ0MzZhNzg1NjU2NWVkYWE4NTJkYTEwNzc3YTA4ZjQwMDZhZTFmMGRjMGJiYTliYmY0MjA3MjcmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.JYAv4JtCNyChBSd1nfiOE7afi616g6bnZPw08gRyJ1E)](https://www.postgresql.org/)

## 📋 目录

- [项目简介](#项目简介)
- [核心功能](#核心功能)
- [技术架构](#技术架构)
- [快速开始](#快速开始)
- [配置说明](#配置说明)
- [API 文档](#api-文档)
- [项目结构](#项目结构)
- [安全特性](#安全特性)
- [开发指南](#开发指南)
- [常见问题](#常见问题)

## 🎯 项目简介

这是一个现代化的会议室管理系统，结合了 **AI 技术**和**自然语言处理**能力，让用户可以通过简单的对话完成会议室预订，无需繁琐的表单填写。

### 主要特点

- 🤖 **AI 智能对话**：支持自然语言预订，如 "明天下午2点预订大会议室2小时"
- 📅 **实时状态查询**：可视化展示所有会议室的实时状态和日程安排
- 🔒 **多重安全防护**：IP 限流、会话限制、验证码等多层安全机制
- 🎨 **现代化界面**：商务风格的 UI 设计，专业且易用
- ⚡ **高性能**：基于 Spring Boot 3.x 构建，响应迅速

## ✨ 核心功能

### 1. AI 对话式预订

- 自然语言理解用户意图
- 自动识别时间、地点、参会人等信息
- 智能补全缺失信息
- 支持上下文对话，记忆历史对话内容

**示例对话：**
```
用户：明天下午2点预订大会议室2小时
AI：好的，我需要确认一下信息：
    - 会议室：大会议厅
    - 时间：2025-02-10 14:00 - 16:00
    - 请问会议主题是什么？组织者是谁？
    
用户：季度总结会，组织者张三
AI：已为您成功预订！
    会议室：大会议厅
    主题：季度总结会
    组织者：张三
    时间：2025-02-10 14:00:00 - 16:00:00
```

### 2. 会议室状态管理

- 实时显示所有会议室的占用状态
- 按日期查看会议室日程
- 支持查看未来7天的会议室安排
- 自动刷新状态（每分钟）

### 3. 安全与限流

- **会话级限流**：每个会话最多 10 次对话
- **IP 级限流**：
  - 每分钟最多 20 次请求
  - 每小时最多 100 次请求
  - 超过阈值自动封禁 IP（1分钟50次触发封禁60分钟）
- **验证码保护**：可选的验证码验证功能

## 🏗️ 技术架构

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17+ | 编程语言 |
| Spring Boot | 3.x | 应用框架 |
| Spring AI | Latest | AI 集成框架 |
| Spring Data JPA | 3.x | 数据持久化 |
| PostgreSQL | 15+ | 关系型数据库 |
| Lombok | Latest | 简化代码 |
| DeepSeek API | - | AI 模型服务 |

### 前端技术

- 纯 HTML/CSS/JavaScript
- 响应式设计，支持移动端
- 现代化商务风格 UI

### 架构设计

```
┌─────────────────┐
│   前端界面       │
│  (HTML/CSS/JS)  │
└────────┬────────┘
         │ HTTP/REST
┌────────▼────────┐
│  ChatController │
│ (REST API 层)   │
└────────┬────────┘
         │
┌────────▼────────┐
│   AIService     │ ◄─── Spring AI Framework
│  (AI 对话服务)   │
└────────┬────────┘
         │
┌────────▼────────┐
│ BookingService  │
│   (业务逻辑)     │
└────────┬────────┘
         │
┌────────▼────────┐
│   Repository    │
│   (数据访问)     │
└────────┬────────┘
         │
┌────────▼────────┐
│   PostgreSQL    │
│    (数据库)      │
└─────────────────┘
```

## 🚀 快速开始

### 前置要求

- ☕ Java 17 或更高版本
- 🐘 PostgreSQL 15 或更高版本
- 🔑 DeepSeek API Key（或其他兼容 OpenAI API 的服务）
- 📦 Maven 3.6+

### 1. 克隆项目

```bash
git clone <repository-url>
cd meeting-room-booking
```

### 2. 配置数据库

创建 PostgreSQL 数据库和 Schema：

```sql
-- 创建数据库
CREATE DATABASE tangtian;

-- 连接到数据库
\c tangtian

-- 创建 Schema
CREATE SCHEMA meetroom;

-- 创建用户（可选）
CREATE USER tangtian WITH PASSWORD 'tangtian';
GRANT ALL PRIVILEGES ON SCHEMA meetroom TO tangtian;
```

### 3. 配置环境变量

设置 DeepSeek API Key：

**Linux/Mac：**
```bash
export DEEPSEEK_API_KEY="your-api-key-here"
```

**Windows (CMD)：**
```cmd
set DEEPSEEK_API_KEY=your-api-key-here
```

**Windows (PowerShell)：**
```powershell
$env:DEEPSEEK_API_KEY="your-api-key-here"
```

### 4. 修改配置文件

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/tangtian?currentSchema=meetroom
    username: tangtian  # 修改为你的数据库用户名
    password: tangtian  # 修改为你的数据库密码
```

### 5. 初始化会议室数据

应用会自动创建表结构，但需要手动添加初始会议室数据：

```sql
INSERT INTO meetroom.meeting_room (name, capacity, location, facilities, created_at) 
VALUES 
('会议室A', 10, '3楼东侧', '投影仪、白板、视频会议', NOW()),
('会议室B', 20, '3楼西侧', '投影仪、白板、音响系统', NOW()),
('会议室C', 6, '4楼', '白板、电话会议', NOW()),
('大会议厅', 50, '1楼', '投影仪、音响系统、舞台、视频会议', NOW());
```

### 6. 启动应用

```bash
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动

### 7. 访问系统

打开浏览器访问：
- 主页面：http://localhost:8080
- 健康检查：http://localhost:8080/api/health
- 配置信息：http://localhost:8080/api/config

## ⚙️ 配置说明

### application.yml 配置项

```yaml
# 数据库配置
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/tangtian?currentSchema=meetroom
    username: tangtian
    password: tangtian

# AI 配置
  ai:
    openai:
      base-url: https://api.deepseek.com  # AI API 地址
      api-key: ${DEEPSEEK_API_KEY}        # 从环境变量读取
      chat:
        options:
          model: deepseek-chat              # 使用的模型
          temperature: 0.7                  # 温度参数（创造性）
          max-tokens: 2000                  # 最大输出 token 数

# 服务器配置
server:
  port: 8080                                # 服务端口

# 安全配置
app:
  security:
    max-conversations-per-session: 10       # 每个会话最大对话次数
    max-requests-per-minute: 20             # 每IP每分钟最大请求数
    max-requests-per-hour: 100              # 每IP每小时最大请求数
    ban-threshold: 50                       # IP封禁阈值（1分钟内）
    ban-duration-minutes: 60                # IP封禁时长（分钟）
```

### 修改配置建议

**生产环境配置：**

1. **数据库安全**：
   - 使用强密码
   - 限制数据库访问 IP
   - 启用 SSL 连接

2. **API Key 安全**：
   - 不要在代码中硬编码 API Key
   - 使用环境变量或密钥管理服务
   - 定期轮换 API Key

3. **限流调整**：
   - 根据实际负载调整限流参数
   - 生产环境建议降低会话对话次数限制

4. **日志级别**：
   ```yaml
   logging:
     level:
       com.example: INFO  # 改为 INFO 或 WARN
   ```

## 📡 API 文档

### 1. 对话接口

**POST** `/api/chat`

发送消息给 AI 助手

**请求头：**
```
Content-Type: application/json
X-Session-Id: session_xxx (可选)
```

**请求体：**
```json
{
  "message": "明天下午2点预订大会议室2小时"
}
```

**响应：**
```json
{
  "response": "好的，我需要确认一下...",
  "bookingInfo": {
    "bookingId": 1,
    "roomName": "大会议厅",
    "startTime": "2025-02-10T14:00:00",
    "endTime": "2025-02-10T16:00:00"
  },
  "remainingCount": 9,
  "usedCount": 1,
  "maxCount": 10,
  "sessionId": "session_xxx"
}
```

### 2. 会议室状态查询

**GET** `/api/rooms/status?date=2025-02-09`

查询指定日期的会议室状态

**响应：**
```json
[
  {
    "id": 1,
    "name": "会议室A",
    "capacity": 10,
    "location": "3楼东侧",
    "facilities": "投影仪、白板、视频会议",
    "status": "available",
    "schedules": [
      {
        "time": "09:00-11:00",
        "title": "部门周会",
        "organizer": "张三"
      }
    ]
  }
]
```

**状态说明：**
- `available`: 全天可用
- `partial`: 部分时段已预订
- `booked`: 已被预订

### 3. 会话管理

**POST** `/api/session/reset`

重置当前会话

**GET** `/api/session/remaining`

查询剩余对话次数

**GET** `/api/health`

健康检查

**GET** `/api/config`

获取系统配置信息

## 📁 项目结构

```
src/
├── main/
│   ├── java/top/tangtian/meetingschedule/
│   │   ├── config/                    # 配置类
│   │   │   ├── IpRateLimiter.java     # IP 限流器
│   │   │   ├── RateLimitConfig.java   # 限流配置
│   │   │   ├── SessionLimitManager.java # 会话管理
│   │   │   └── WebConfig.java         # Web 配置
│   │   ├── controller/                # 控制器层
│   │   │   ├── CaptchaController.java # 验证码控制器
│   │   │   ├── ChatController.java    # 对话控制器
│   │   │   └── RoomStatusController.java # 状态查询控制器
│   │   ├── dto/                       # 数据传输对象
│   │   │   ├── BookingInfo.java
│   │   │   ├── BookingRequest.java
│   │   │   ├── BookingResponse.java
│   │   │   ├── BookingSchedule.java
│   │   │   ├── ChatRequest.java
│   │   │   ├── ChatResponse.java
│   │   │   └── RoomStatusResponse.java
│   │   ├── entity/                    # 实体类
│   │   │   ├── MeetingRoom.java       # 会议室实体
│   │   │   └── RoomBooking.java       # 预订记录实体
│   │   ├── filter/                    # 过滤器
│   │   │   └── SecurityFilter.java    # 安全过滤器
│   │   ├── repository/                # 数据访问层
│   │   │   ├── MeetingRoomRepository.java
│   │   │   └── RoomBookingRepository.java
│   │   ├── service/                   # 业务逻辑层
│   │   │   ├── AIService.java         # AI 对话服务
│   │   │   ├── BookingFunctions.java  # 预订函数定义
│   │   │   ├── BookingService.java    # 预订业务逻辑
│   │   │   ├── CaptchaService.java    # 验证码服务
│   │   │   └── RoomStatusService.java # 状态查询服务
│   │   └── MeetingRoomApplication.java # 应用入口
│   └── resources/
│       ├── application.yml            # 应用配置
│       └── templates/
│           └── index.html             # 前端页面
└── test/
    └── java/
        └── top/tangtian/
            └── AppTest.java           # 测试类
```

### 核心类说明

#### 1. AIService
负责与 AI 模型的交互，管理对话历史

**主要功能：**
- 构建系统提示词
- 管理会话上下文（保留最近3轮对话）
- 调用 AI 模型生成响应
- Function Calling 集成

#### 2. BookingFunctions
定义 AI 可调用的预订函数

**Function Schema：**
```java
{
  "name": "bookMeetingRoom",
  "description": "预订会议室",
  "parameters": {
    "roomName": "会议室名称",
    "title": "会议主题",
    "organizer": "组织者",
    "startTime": "开始时间 (yyyy-MM-dd HH:mm:ss)",
    "endTime": "结束时间 (yyyy-MM-dd HH:mm:ss)",
    "attendees": "参会人数 (可选)",
    "description": "会议描述 (可选)"
  }
}
```

#### 3. SessionLimitManager
管理会话对话次数限制

**实现机制：**
- 基于内存的 ConcurrentHashMap
- 定时清理过期会话（30分钟无活动）
- 线程安全

#### 4. IpRateLimiter
IP 级别的请求限流

**限流策略：**
- 令牌桶算法
- 分钟级和小时级双重限制
- 自动封禁机制

## 🔒 安全特性

### 1. 多层限流保护

```
┌─────────────────────────────────────┐
│  IP 限流 (SecurityFilter)           │
│  - 每分钟最多 20 次                  │
│  - 每小时最多 100 次                 │
│  - 触发阈值：50次/分钟               │
│  - 封禁时长：60 分钟                 │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│  会话限流 (SessionLimitManager)     │
│  - 每会话最多 10 次对话              │
│  - 30分钟无活动自动清理              │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│  验证码保护 (可选)                   │
│  - 可配置的验证码验证                │
└─────────────────────────────────────┘
```

### 2. CORS 配置

当前配置为开发环境，允许所有来源：
```java
@CrossOrigin(origins = "*")
```

**生产环境建议：**
```java
@CrossOrigin(origins = "https://yourdomain.com", 
             allowedHeaders = "*",
             methods = {RequestMethod.GET, RequestMethod.POST})
```

### 3. 敏感信息保护

⚠️ **重要提示：**

代码中包含敏感信息，请在部署前务必处理：

1. **删除硬编码的 API Key**（在 MeetingRoomApplication.java 第8行）
2. **使用环境变量**存储所有敏感配置
3. **不要将包含密钥的配置文件**提交到版本控制

**最佳实践：**
```yaml
# 使用环境变量
spring:
  ai:
    openai:
      api-key: ${DEEPSEEK_API_KEY}
  datasource:
    password: ${DB_PASSWORD}
```

## 🛠️ 开发指南

### 本地开发

1. **使用 IDE（推荐 IntelliJ IDEA）：**
   - 导入 Maven 项目
   - 配置 JDK 17+
   - 设置环境变量
   - 运行 `MeetingRoomApplication.main()`

2. **使用 Maven：**
   ```bash
   # 编译
   mvn clean compile
   
   # 运行测试
   mvn test
   
   # 打包
   mvn clean package
   
   # 运行
   mvn spring-boot:run
   ```

### 添加新会议室

```java
// 方式1：通过 SQL
INSERT INTO meetroom.meeting_room (name, capacity, location, facilities, created_at) 
VALUES ('会议室D', 15, '5楼', '投影仪、白板', NOW());

// 方式2：通过 JPA Repository (在代码中)
@Autowired
private MeetingRoomRepository roomRepository;

public void addRoom() {
    MeetingRoom room = MeetingRoom.builder()
        .name("会议室D")
        .capacity(15)
        .location("5楼")
        .facilities("投影仪、白板")
        .build();
    roomRepository.save(room);
}
```

### 自定义 AI 提示词

修改 `AIService.buildSystemPrompt()` 方法：

```java
private String buildSystemPrompt() {
    return String.format("""
        你是一个智能会议室预订助手。
        
        // 在这里添加你的自定义指令
        // 例如：特定的对话风格、额外的业务规则等
        
        可用的会议室:
        // ... 会议室列表
        """, currentTime);
}
```

### 切换其他 AI 模型

系统使用 Spring AI 框架，可以轻松切换到其他模型：

**使用 OpenAI：**
```yaml
spring:
  ai:
    openai:
      base-url: https://api.openai.com
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4
```

**使用本地模型（Ollama）：**
```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: llama2
```

### 数据库迁移

**备份数据库：**
```bash
pg_dump -U tangtian -d tangtian -n meetroom > backup.sql
```

**恢复数据库：**
```bash
psql -U tangtian -d tangtian < backup.sql
```

## 📊 监控和日志

### 日志配置

修改 `application.yml` 中的日志级别：

```yaml
logging:
  level:
    root: INFO
    top.tangtian.meetingschedule: DEBUG
    org.springframework.ai: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
  file:
    name: logs/meeting-room-booking.log
```

### 关键日志位置

- AI 调用日志：`AIService.java`
- 请求日志：`ChatController.java`
- 限流日志：`SecurityFilter.java`, `SessionLimitManager.java`
- 预订日志：`BookingService.java`

### 性能监控建议

生产环境建议集成：
- Spring Boot Actuator（健康检查、指标）
- Prometheus + Grafana（监控可视化）
- ELK Stack（日志分析）

## ❓ 常见问题

### 1. 无法连接数据库

**问题：** `Connection refused` 或 `Authentication failed`

**解决方案：**
- 检查 PostgreSQL 服务是否启动
- 验证数据库用户名和密码
- 确认数据库和 Schema 已创建
- 检查 `pg_hba.conf` 权限配置

### 2. AI 响应超时

**问题：** AI 调用失败或响应慢

**解决方案：**
- 检查 API Key 是否正确
- 验证网络连接是否正常
- 查看 DeepSeek API 服务状态
- 考虑增加超时时间：
  ```yaml
  spring:
    ai:
      openai:
        chat:
          options:
            timeout: 60s
  ```

### 3. 会话次数显示不正确

**问题：** 剩余次数计算错误

**解决方案：**
- 清除浏览器缓存和 localStorage
- 重置会话：调用 `/api/session/reset`
- 检查 `SessionLimitManager` 配置

### 4. IP 被误封

**问题：** 正常使用却被限流

**解决方案：**
- 调整限流参数（`application.yml`）
- 手动清除封禁（重启应用）
- 考虑使用白名单机制

### 5. 前端无法连接后端

**问题：** CORS 错误或连接失败

**解决方案：**
- 检查后端是否在 8080 端口运行
- 验证前端 API_URL 配置
- 检查 CORS 配置是否正确
- 查看浏览器控制台错误信息

## 🤝 贡献指南

欢迎贡献代码、报告问题或提出建议！

**贡献流程：**
1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 详见 LICENSE 文件

## 👥 作者

- **tangtian** - 初始工作

## 🙏 致谢

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring AI](https://spring.io/projects/spring-ai)
- [DeepSeek](https://www.deepseek.com/)
- [PostgreSQL](https://www.postgresql.org/)

## 📞 联系方式

如有问题或建议，欢迎通过以下方式联系：

- 项目 Issues: [GitHub Issues](your-repo-url/issues)
- Email: your-email@example.com

---

⭐ 如果这个项目对您有帮助，欢迎给个 Star！