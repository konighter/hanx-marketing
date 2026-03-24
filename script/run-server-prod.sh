#!/bin/bash

IMAGE="192.168.1.13:5000/hzapp-server:latest"
CONTAINER_NAME="hzapp-server-prod"

echo "Stopping and removing existing container if any..."
docker stop $CONTAINER_NAME 2>/dev/null || true
docker rm $CONTAINER_NAME 2>/dev/null || true

# 确保内部网络存在
docker network create hzapp-net 2>/dev/null || true

echo "Starting $CONTAINER_NAME in production mode..."

# 运行后端服务
# -e SPRING_PROFILES_ACTIVE=prod 指定使用 application-prod.yml 配置
# 相关的数据库、Redis和AWS等密码若需要覆盖，可在下面继续添加 -e 参数
# --network hzapp-net 将容器加入内部网络
docker run -d \
  --name $CONTAINER_NAME \
  --network hzapp-net \
  --restart unless-stopped \
  -p 48080:48080 \
  -e TZ=Asia/Shanghai \
  -e SPRING_PROFILES_ACTIVE=prod \
  $IMAGE

echo "=========================================="
echo " Container $CONTAINER_NAME started successfully! "
echo " You can view the logs using: docker logs -f $CONTAINER_NAME"
echo "=========================================="
