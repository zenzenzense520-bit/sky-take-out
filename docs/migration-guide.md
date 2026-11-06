# 云端迁移到电脑操作指南

本项目所有代码、脚本、SQL、文档均为标准文件，不依赖云端特殊环境，可整体迁移到
Windows / macOS / Linux 电脑继续开发。提供两种迁移方式。

## 一、迁移方式

### 方式一：通过 Git 仓库（推荐，便于后续同步）

云端先初始化并提交：

```bash
cd sky-take-out
git init
git add .
git commit -m "feat: 苍穹外卖工程骨架"
# 关联远程仓库后推送（GitHub/Gitee 均可）
git remote add origin <你的仓库地址>
git push -u origin master
```

电脑端克隆：

```bash
git clone <你的仓库地址>
```

### 方式二：直接打包下载

```bash
# 云端打包（排除依赖与构建产物，体积小）
tar --exclude='*/target' --exclude='*/node_modules' --exclude='*/dist' \
    -czf sky-take-out.tar.gz sky-take-out
```

将压缩包下载到电脑后解压即可。

## 二、电脑端环境准备

| 软件 | 版本要求 | 用途 |
| --- | --- | --- |
| JDK | 11 | 后端运行 |
| Maven | 3.6+ | 后端构建 |
| Node.js | 18+（推荐 20/22） | 前端构建 |
| Docker Desktop | 最新 | 一键启动 MySQL/Redis（可选） |
| IDEA | 2022+ | 后端开发，安装 Lombok 插件 |
| VS Code | 最新 | 前端开发，安装 Volar 插件 |

不使用 Docker 时，需自行安装 MySQL 8.0 与 Redis。

## 三、数据库初始化

### 使用 Docker（最省事）

```bash
docker compose up -d
```

首次启动会自动执行 `sql/sky_take_out.sql`，创建库表与管理员数据。

### 本地安装 MySQL

```bash
mysql -u root -p < sql/sky_take_out.sql
```

如数据库账号密码不是 root/root，修改 `sky-server/src/main/resources/application-dev.yml`。

## 四、启动项目

### macOS / Linux

```bash
# 后端（端口 8080）
./scripts/backend-start.sh

# 前端（端口 80）
./scripts/frontend-install.sh
./scripts/frontend-start.sh
```

### Windows

推荐使用 Git Bash 执行 `scripts/` 下的 .sh 脚本；或在 PowerShell 中手动执行等价命令：

```powershell
# 后端
mvn clean package -DskipTests
java -jar sky-server/target/sky-server.jar

# 前端
cd sky-admin-vue
npm install
npm run dev
```

## 五、验证迁移结果

1. 后端：访问 `http://localhost:8080/doc.html` 能打开接口文档。
2. 前端：访问 `http://localhost`，用 **admin / 123456** 登录成功。
3. 登录后进入员工管理，分页数据正常加载即说明前后端与数据库链路打通。

## 六、注意事项与常见问题

1. **端口占用**：80/8080/3306/6379 被占用时，修改 `vite.config.js`、`application.yml` 或 compose 配置。
2. **前端 80 端口**：Windows 下 80 端口可能被 IIS 占用，可在 `vite.config.js` 改为 8081。
3. **Lombok 不生效**：IDEA 中安装并启用 Lombok 插件，开启 Annotation Processing。
4. **MySQL 时区/连接**：连接串已配置 `serverTimezone=Asia/Shanghai` 与 `allowPublicKeyRetrieval=true`。
5. **构建产物不入库**：`target/`、`node_modules/`、`dist/` 已在 `.gitignore` 中忽略，迁移后需重新构建。
6. **敏感配置**：OSS、微信密钥等通过 `application.yml` 占位，切勿提交真实密钥，可使用 `application-local.yml`（已被忽略）覆盖。
7. **后续云端与电脑同步**：统一用 Git 提交推送，两端 `git pull` 即可，避免直接复制文件产生版本混乱。
