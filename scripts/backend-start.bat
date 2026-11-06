@echo off
chcp 65001 >nul
cd /d "%~dp0\.."
echo ===== 编译并启动苍穹外卖后端服务（端口 8080）=====
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo [ERROR] Maven 打包失败，请检查 JDK 与 Maven 环境配置！
    pause
    exit /b %errorlevel%
)
echo ===== 启动 sky-server.jar =====
java -jar sky-server\target\sky-server.jar
pause
