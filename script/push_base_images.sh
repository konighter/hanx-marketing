#!/bin/bash

REGISTRY="registry.cn-hangzhou.aliyuncs.com"
NAMESPACE="hanx-public"

IMAGES=(
  "maven:3.9-jdk17 maven:3.9-jdk17"
  "eclipse-temurin:17-jre eclipse-temurin:17-jre"
  "node:20-alpine node:20-alpine"
  "node:20-slim node:20-slim"  # 新增：Debian 版本的 Node 20
  "nginx:alpine nginx:alpine"
)

echo "同步状态检查..."
for item in "${IMAGES[@]}"; do
  read -r SRC DEST <<< "$item"
  echo "正在处理: $SRC -> $DEST"
  docker pull $SRC
  docker tag $SRC $REGISTRY/$NAMESPACE/$DEST
  docker push $REGISTRY/$NAMESPACE/$DEST
done
