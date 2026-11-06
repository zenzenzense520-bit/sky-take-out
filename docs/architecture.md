# 苍穹外卖架构设计

## 一、总体架构

系统采用经典三层架构 + Maven 多模块组织，前后端分离，通过 RESTful 接口交互。

```
┌───────────────────────────────────────────────┐
│  前端：Vue3 + Vite + Element Plus（管理端）      │
│       Axios → Vite 代理 → 后端 8080             │
└───────────────────────┬───────────────────────┘
                        │ HTTP / JSON（JWT 鉴权）
┌───────────────────────▼───────────────────────┐
│  sky-server 核心服务                            │
│  Controller 控制层 → Service 业务层 → Mapper 层  │
│  拦截器 / 全局异常 / AOP / WebSocket / 定时任务  │
└───────┬───────────────────────────┬───────────┘
        │                           │
┌───────▼────────┐          ┌───────▼────────┐
│   MySQL 8.0    │          │    Redis 7     │
│  11 张业务表    │          │ 缓存/购物车/验证码│
└────────────────┘          └────────────────┘
```

## 二、Maven 模块与依赖关系

| 模块 | 职责 | 依赖 |
| --- | --- | --- |
| sky-take-out | 父工程，聚合模块、统一依赖版本 | spring-boot-starter-parent |
| sky-common | 常量、上下文、枚举、异常、JSON、配置属性、工具类 | 无内部依赖 |
| sky-pojo | entity 实体、dto 传输对象、vo 视图对象 | sky-common |
| sky-server | 启动类、controller、service、mapper、配置等 | sky-pojo（传递 common） |

依赖方向单向收敛：`server → pojo → common`，禁止反向依赖与循环依赖。

## 三、分层职责

1. **Controller 层**：接收请求、参数校验、调用 Service、封装统一返回结果，不写业务逻辑。
   - `controller/admin`：管理端接口，路径前缀 `/admin`
   - `controller/user`：用户端接口，路径前缀 `/user`
2. **Service 层**：核心业务逻辑、事务控制、缓存策略，接口与实现分离（`impl` 包）。
3. **Mapper 层**：数据库访问，注解写简单 SQL，XML 写动态 SQL。
4. **POJO**：entity 对应表结构，dto 接收前端参数，vo 封装返回数据，职责分离避免实体直接暴露。

## 四、一次请求的处理流程

```
请求 → JwtToken 拦截器（解析令牌、写入 BaseContext）
     → Controller（参数绑定/校验）
     → Service（业务处理、@Transactional 事务）
     → Mapper（AOP 自动填充审计字段 → 执行 SQL）
     → Result 统一封装 → 响应拦截器 → 前端
异常 → GlobalExceptionHandler 统一转换为 Result.error
```

## 五、横切关注点设计

### 1. JWT 鉴权拦截器

- 管理端与用户端各自独立的拦截器、密钥与请求头名称。
- 解析令牌得到用户 ID 存入 `BaseContext`（ThreadLocal），请求结束后清理，防止线程复用串数据。
- 登录、店铺状态等白名单路径放行。

### 2. 公共字段自动填充（AOP）

- 自定义 `@AutoFill` 注解标注在 Mapper 方法上，声明 INSERT/UPDATE。
- 切面拦截后通过反射统一注入 `createTime/updateTime/createUser/updateUser`，
  消除每个业务方法重复赋值的冗余代码。

### 3. 全局异常处理

- `@RestControllerAdvice` 统一捕获：业务异常返回友好提示，
  唯一约束冲突提取冲突字段，未知异常记录完整日志并返回通用提示，避免堆栈外泄。

### 4. 统一返回与序列化

- 所有接口返回 `Result<T>`，code 1 成功、0 失败，与前端约定一致。
- 自定义 Jackson 转换器：Long 转 String 防止前端精度丢失，统一 Java 8 时间格式。

### 5. WebSocket 与定时任务

- WebSocket 维护在线会话，用于订单来电提醒、催单、状态实时推送。
- Spring Task 处理超时未支付订单自动取消、派送中订单自动完成。

## 六、关键设计决策

| 决策 | 理由 |
| --- | --- |
| 后端使用 Spring Boot 2.7 + JDK 11 | 与经典苍穹教程和主流资料一致，学习与问题排查成本低 |
| 前端升级 Vue3 + Vite | 规避 Node 高版本 node-sass 兼容问题，构建快、技术栈更新 |
| 中间件提供 docker-compose | 依赖环境一键启动，迁移电脑时零配置，数据持久化到本地目录 |
| 公共字段用 AOP 而非手动赋值 | 横切逻辑统一收口，避免冗余与遗漏 |
| DTO/VO 与实体分离 | 隔离数据库结构与对外接口，参数校验更清晰 |
