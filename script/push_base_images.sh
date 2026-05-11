#!/bin/bash

# 你的阿里云镜像仓库配置
REGISTRY="registry.cn-hangzhou.aliyuncs.com"
NAMESPACE="hanx-public"

# 需要迁移的镜像列表 (格式: "公共镜像名:标签 私有镜像名:标签")
IMAGES=(
  "maven:3.9-eclipse-temurin-17 maven:3.9-jdk17"
  "eclipse-temurin:17-jre eclipse-temurin:17-jre"
  "node:18-alpine node:18-alpine"
  "nginx:alpine nginx:alpine"
)

echo "同步状态检查..."

for item in "${IMAGES[@]}"; do
  read -r SRC DEST <<< "$item"
  echo "检查镜像: $REGISTRY/$NAMESPACE/$DEST"
  docker inspect $REGISTRY/$NAMESPACE/$DEST > /dev/null 2>&1
  if [ $? -eq 0 ]; then
    echo "状态: 已就绪"
  else
    echo "状态: 缺失，正在同步..."
    docker pull $SRC
    docker tag $SRC $REGISTRY/$NAMESPACE/$DEST
    docker push $REGISTRY/$NAMESPACE/$DEST
  fi
done

echo "------------------------------------------------"
echo "所有基础镜像同步完成！"
