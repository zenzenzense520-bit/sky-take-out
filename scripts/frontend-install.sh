#!/usr/bin/env bash
# 安装前端依赖
set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "${PROJECT_ROOT}/sky-admin-vue"

echo "===== 安装前端依赖 ====="
npm install --no-audit --no-fund
echo "===== 前端依赖安装完成 ====="
