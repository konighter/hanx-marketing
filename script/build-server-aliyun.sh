#!/bin/bash

# 出现错误即退出
set -e

# 阿里云镜像配置
REGISTRY="registry.cn-hangzhou.aliyuncs.com"
NAMESPACE="hanzhan"
SERVER_IMAGE_NAME="hzapp-erplus-server"
IMAGE_TAG="latest"

# 获取项目根目录
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

echo "=========================================="
echo " Building Backend Server (Aliyun) "
echo "=========================================="

cd "$PROJECT_ROOT"

FULL_IMAGE_NAME="$REGISTRY/$NAMESPACE/$SERVER_IMAGE_NAME:$IMAGE_TAG"

# 执行 Docker 构建
# 注意：上下文设为项目根目录，以便 Dockerfile 访问 settings.xml 和所有模块源码
echo "Starting Docker multi-stage build..."
docker build -t "$FULL_IMAGE_NAME" -f script/docker/hzapp-server/Dockerfile.aliyun .

# 推送镜像
echo "Pushing Backend image to Aliyun: $FULL_IMAGE_NAME"
docker push "$FULL_IMAGE_NAME"

echo "=========================================="
echo " Aliyun Backend build and push completed! "
echo " Image: $FULL_IMAGE_NAME "
