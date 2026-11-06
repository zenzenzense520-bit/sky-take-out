#!/usr/bin/env bash
# 启动前端开发服务器，日志输出到 logs/frontend.log
set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "${PROJECT_ROOT}"

mkdir -p logs
cd sky-admin-vue

echo "===== 启动前端开发服务器（端口 80）====="
npm run dev 2>&1 | tee "${PROJECT_ROOT}/logs/frontend.log"
