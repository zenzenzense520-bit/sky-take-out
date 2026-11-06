#!/usr/bin/env bash
# 编译后端全部模块（跳过测试）
set -e

# 定位项目根目录（脚本位于 scripts/ 下）
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "${PROJECT_ROOT}"

echo "===== 开始编译后端 ====="
mvn clean compile -DskipTests
echo "===== 后端编译完成 ====="
