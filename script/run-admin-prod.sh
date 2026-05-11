#!/bin/bash

# 1. 配置信息
REGISTRY="registry.cn-hangzhou.aliyuncs.com"
IMAGE_NAME="registry.cn-hangzhou.aliyuncs.com/hanzhan/hzapp-erplus-admin:latest"
CONTAINER_NAME="hzapp-admin-prod"

# 默认后端 API 地址 (可根据实际情况修改或通过 ENV 传入)
VITE_BASE_URL="${VITE_BASE_URL:-http://localhost:48080}"

echo "=========================================="
echo " Running Frontend (Prod) "
echo "=========================================="

# 2. 自动登录 (如果设置了环境变量)
if [ ! -z "$DOCKER_USERNAME" ] && [ ! -z "$DOCKER_PASSWORD" ]; then
    echo "Logging in to Aliyun Registry..."
    echo "$DOCKER_PASSWORD" | docker login --username "$DOCKER_USERNAME" --password-stdin "$REGISTRY"
fi

# 3. 拉取最新镜像
echo "Pulling latest image: $IMAGE_NAME"
docker pull "$IMAGE_NAME"

# 4. 停止并删除旧容器
if [ "$(docker ps -aq -f name=$CONTAINER_NAME)" ]; then
    echo "Stopping and removing existing container: $CONTAINER_NAME"
    docker stop "$CONTAINER_NAME" || true
    docker rm "$CONTAINER_NAME" || true
fi

# 5. 启动容器
echo "Starting new container: $CONTAINER_NAME"
docker run -d \
    --name "$CONTAINER_NAME" \
    --restart always \
    -p 80:80 \
    -e VITE_BASE_URL="$VITE_BASE_URL" \
    "$IMAGE_NAME"

echo "------------------------------------------"
echo " Frontend is running at http://localhost"
echo " API Proxy to: $VITE_BASE_URL"
echo "=========================================="
