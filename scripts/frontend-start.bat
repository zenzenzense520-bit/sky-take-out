@echo off
chcp 65001 >nul
cd /d "%~dp0\..\sky-admin-vue"
echo ===== 启动苍穹外卖前端开发服务器 =====
if not exist node_modules (
    echo 首次启动，正在安装前端依赖...
    call npm install
    if %errorlevel% neq 0 (
        echo [ERROR] 依赖安装失败，请检查 Node.js 与 npm 环境！
        pause
        exit /b %errorlevel%
    )
)
call npm run dev
pause
