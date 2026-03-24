#!/bin/bash

# Configuration
IMAGE="192.168.1.13:5000/hzapp-admin:latest"
CONTAINER_NAME="hzapp-admin-prod"
# Backend API URL - Change this to your production backend URL if necessary
VITE_BASE_URL="${VITE_BASE_URL:-http://hzapp-server:48080}"

echo "Stopping and removing existing container if any..."
docker stop $CONTAINER_NAME 2>/dev/null || true
docker rm $CONTAINER_NAME 2>/dev/null || true

# 确保内部网络存在
docker network create hzapp-net 2>/dev/null || true

echo "Starting $CONTAINER_NAME in production mode..."

# Run Frontend container
# -e VITE_BASE_URL specifies the backend API URL at runtime
docker run -d \
  --name $CONTAINER_NAME \
  --network hzapp-net \
  --restart unless-stopped \
  -p 80:80 \
  -e TZ=Asia/Shanghai \
  -e VITE_BASE_URL="$VITE_BASE_URL" \
  $IMAGE

echo "=========================================="
echo " Container $CONTAINER_NAME started successfully! "
echo " Backend URL: $VITE_BASE_URL"
echo " You can view the logs using: docker logs -f $CONTAINER_NAME"
echo " Access the UI at: http://your-server-ip"
echo "=========================================="
