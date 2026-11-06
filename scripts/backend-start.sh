#!/usr/bin/env bash
# 启动后端服务，日志输出到 logs/sky-server.log
set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "${PROJECT_ROOT}"

mkdir -p logs

echo "===== 启动后端服务（端口 8080）====="
# 先打包再运行，保证使用最新代码
mvn -q clean package -DskipTests
java -jar sky-server/target/sky-server.jar 2>&1 | tee logs/sky-server.log
