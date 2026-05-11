#!/bin/bash

# 1. 配置信息
REGISTRY="registry.cn-hangzhou.aliyuncs.com"
IMAGE_NAME="registry.cn-hangzhou.aliyuncs.com/hanzhan/hzapp-erplus-server:latest"
CONTAINER_NAME="hzapp-server-prod"
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ENV_FILE="$SCRIPT_DIR/server.env"

echo "=========================================="
echo " Running Backend Server (Prod) "
echo "=========================================="

# 2. 自动登录
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

# 确保日志目录存在
mkdir -p "$SCRIPT_DIR/logs"

# 检查配置文件是否存在
ENV_OPTS=""
if [ -f "$ENV_FILE" ]; then
    echo "Using environment file: $ENV_FILE"
    ENV_OPTS="--env-file $ENV_FILE"
else
    echo "Warning: $ENV_FILE not found, using default env."
    ENV_OPTS="-e SPRING_PROFILES_ACTIVE=prod -e TZ=Asia/Shanghai -e JAVA_OPTS='-Xms512m -Xmx512m'"
fi

docker run -d \
    --name "$CONTAINER_NAME" \
    --restart always \
    -p 48080:48080 \
    -v "$SCRIPT_DIR/logs:/root/logs" \
    $ENV_OPTS \
    "$IMAGE_NAME"

echo "------------------------------------------"
echo " Server is running at http://localhost:48080"
echo "=========================================="
