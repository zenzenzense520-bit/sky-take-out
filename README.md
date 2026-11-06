# 苍穹外卖 sky-take-out

基于 **Spring Boot + MyBatis + Vue3** 的经典外卖平台全栈项目，采用 Maven 多模块分层架构。

## 一、技术栈

| 层级 | 技术选型 |
| --- | --- |
| 后端框架 | Spring Boot 2.7.12、Spring AOP、Spring Task、Spring Cache |
| 持久层 | MyBatis 2.3.1、PageHelper 1.4.6、Druid 1.2.16 |
| 数据库/缓存 | MySQL 8.0、Redis 7 |
| 认证与文档 | JWT (jjwt 0.9.1)、Knife4j 3.0.3 |
| 实时通信 | WebSocket (spring-boot-starter-websocket) |
| 前端 | Vue 3.4、Vite 5、Element Plus 2.6、Pinia、Vue Router 4、Axios |
| 工具 | 阿里云 OSS、HttpClient、ZXing 二维码、Apache POI |
| 构建 | JDK 11 / JDK 21、Maven 3.6+、Node 18+ |

> 说明：后端严格遵循经典苍穹技术栈；前端在原 Vue2 版本基础上升级为 Vue3 + Vite，
> 规避 Node 高版本的 node-sass 兼容问题，业务模块与接口路径完全对齐。

## 二、系统模块

- **管理端**：员工管理、分类管理、菜品管理、套餐管理、订单管理、文件上传
- **用户端（接口已预留）**：微信登录、地址簿、购物车、下单、订单流转、催单

## 三、目录结构

```
sky-take-out/
├── pom.xml                 # 父工程，统一版本管理
├── docker-compose.yml      # 一键启动 MySQL + Redis
├── sql/                    # 数据库初始化脚本（11 张表 + 初始数据）
├── scripts/                # 统一运行脚本
├── docs/                   # 架构与迁移文档
├── sky-common/             # 公共模块：常量/上下文/枚举/异常/JSON/工具
├── sky-pojo/               # 数据对象：entity / dto / vo
├── sky-server/             # 核心服务：controller/service/mapper/配置/拦截器
└── sky-admin-vue/          # Vue3 管理端
```

## 四、快速开始

### 1. 启动依赖（MySQL + Redis）

方式一（推荐，需安装 Docker）：

```bash
docker compose up -d
```

方式二：本地自行安装 MySQL 8 与 Redis，执行 `sql/sky_take_out.sql` 初始化。

### 2. 启动后端

```bash
./scripts/backend-start.sh
```

服务端口 8080，接口文档地址：`http://localhost:8080/doc.html`

### 3. 启动前端

```bash
./scripts/frontend-install.sh   # 首次运行安装依赖
./scripts/frontend-start.sh
```

访问 `http://localhost`，默认管理员账号：**admin / 123456**

## 五、当前进度与迭代计划

- [x] 迭代一：多模块工程骨架、统一返回/异常、JWT 拦截器、AOP 自动填充、员工完整链路、前端骨架
- [x] 迭代二：分类、菜品（含口味）、套餐（含关联菜品）、文件上传联调、Redis 缓存
- [ ] 迭代三：用户端登录、地址簿、购物车、订单全流程、WebSocket 推送、定时任务
- [ ] 迭代四：数据统计报表、Excel 导出、二维码支付、接口优化

## 六、文档索引

- [架构设计](docs/architecture.md)
- [数据库设计](docs/database-design.md)
- [云端迁移到电脑指南](docs/migration-guide.md)
