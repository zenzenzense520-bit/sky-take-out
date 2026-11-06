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
| 前端 | Vue 3.4、Vite 5、Element Plus 2.6、Pinia、Vue Router 4、Axios、ECharts 5 |
| 工具 | 阿里云 OSS、HttpClient、ZXing 二维码、Apache POI |
| 构建 | JDK 11 ~ 21 (推荐 LTS)、Maven 3.6+、Node 18+ (推荐 20/22) |

> 说明：后端严格遵循经典苍穹技术栈；前端在原 Vue2 版本基础上升级为 Vue3 + Vite，
> 规避 Node 高版本的 node-sass 兼容问题，业务模块与接口路径完全对齐。

## 二、系统模块

- **管理端**：员工管理、分类管理、菜品管理、套餐管理、订单管理（接单/拒单/派送/取消/完成/详情）、数据统计报表（营业额/用户/订单/热销Top10/Apache POI Excel运营报表导出）、WebSocket 实时语音与弹窗提醒、文件上传（阿里云 OSS + 本地回退）
- **用户端**：微信登录（支持真实微信接口与离线开发模拟 Mock）、地址簿全套管理（增删改查、默认地址设置）、购物车（添加/减购/清空）、订单提交、模拟支付、再来一单、催单
- **定时任务**：Spring Task 定时处理超时 15 分钟未支付订单（自动取消）、每日凌晨定时处理派送中订单（自动完成）

## 三、目录结构

```
sky-take-out/
├── pom.xml                 # 父工程，统一版本管理
├── docker-compose.yml      # 一键启动 MySQL + Redis
├── sql/                    # 数据库初始化脚本（11 张表 + 初始数据）
├── scripts/                # 跨平台一键启动脚本（.bat / .sh）
├── docs/                   # 架构与迁移文档
├── sky-common/             # 公共模块：常量/上下文/枚举/异常/JSON/工具
├── sky-pojo/               # 数据对象：entity / dto / vo
├── sky-server/             # 核心服务：controller/service/mapper/配置/拦截器/定时任务/WebSocket
└── sky-admin-vue/          # Vue3 管理端（Element Plus + ECharts）
```

## 四、快速开始

### 1. 启动环境依赖（MySQL + Redis）

本项目依赖 MySQL 8.0 与 Redis 服务：

- **方式一（推荐 Docker 一键启动）**：
  ```bash
  docker compose up -d
  ```
  > 容器首次启动时会自动挂载并执行 `sql/sky_take_out.sql`，完成建库、初始化 11 张表及管理员基础数据。

- **方式二（本地安装中间件）**：
  1. 本地启动 MySQL 8.0 服务，执行 `sql/sky_take_out.sql` 导入数据库表和初始数据。
  2. 本地启动 Redis 服务（默认端口 6379）。
  3. 若本地 MySQL 密码非 `root/root`，请在 `sky-server/src/main/resources/application-dev.yml` 中修改为你的实际账号密码。

---

### 2. 启动后端服务

> **环境要求**：JDK 11 ~ 21（推荐 LTS 版本，如 JDK 11 / 17 / 21）、Maven 3.6+。

- **方式一：通过 IDEA 启动（最常用）**
  1. 用 IntelliJ IDEA 打开项目根目录 `sky-take-out`。
  2. 等待 Maven 依赖导入完成。
  3. 找到并打开 `sky-server/src/main/java/com/sky/SkyApplication.java`。
  4. 点击绿色运行按钮启动项目。

- **方式二：通过命令行启动**
  - **Windows (PowerShell / CMD)**：
    ```cmd
    # 编译打包并运行
    mvn clean package -DskipTests
    java -jar sky-server/target/sky-server.jar

    # 或在项目根目录下直接运行脚本
    .\scripts\backend-start.bat
    ```
  - **Linux / macOS / Git Bash**：
    ```bash
    chmod +x ./scripts/*.sh
    ./scripts/backend-start.sh
    ```

> 服务启动成功后监听 **8080** 端口。  
> Knife4j 接口文档地址：[http://localhost:8080/doc.html](http://localhost:8080/doc.html)

---

### 3. 启动管理端前端

> **环境要求**：Node.js 18+（推荐 Node 18 / 20 / 22）。

- **通用命令行启动**：
  ```bash
  # 1. 进入前端目录
  cd sky-admin-vue

  # 2. 首次运行安装依赖（已安装则跳过）
  npm install

  # 3. 启动本地开发服务器
  npm run dev
  ```

- **脚本一键启动**：
  - Windows：直接双击或运行 `.\scripts\frontend-start.bat`（脚本会自动检测并安装依赖）。
  - Linux / macOS：运行 `./scripts/frontend-start.sh`。

> 启动成功后，浏览器访问：[http://localhost](http://localhost)（若 80 端口被系统占用，控制台会自动顺延分配新端口）。  
> 默认超级管理员账号：**admin**，初始密码：**123456**。

---

## 五、当前进度与迭代计划

- [x] 迭代一：多模块工程骨架、统一返回/异常、JWT 拦截器、AOP 自动填充、员工完整链路、前端骨架
- [x] 迭代二：分类、菜品（含口味）、套餐（含关联菜品）、文件上传联调、Redis 缓存
- [x] 迭代三：用户端登录、地址簿、购物车、订单全流程、WebSocket 推送、定时任务
- [x] 迭代四：数据统计报表、Excel 导出、二维码支付、接口优化

## 六、文档索引

- [架构设计](docs/architecture.md)
- [数据库设计](docs/database-design.md)
- [云端迁移到电脑指南](docs/migration-guide.md)
