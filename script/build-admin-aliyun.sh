#!/bin/bash

# 出现错误即退出
set -e

# 默认构建模式为 prod
MODE="${1:-prod}"
echo "Build Mode: $MODE"

# 阿里云镜像配置
REGISTRY="registry.cn-hangzhou.aliyuncs.com"
NAMESPACE="hanzhan"
ADMIN_IMAGE_NAME="hzapp-erplus-admin"
IMAGE_TAG="latest"

# 根据模式设置输出目录 (对应 .env.prod 中的 VITE_OUT_DIR)
VITE_OUT_DIR="dist-$MODE"
export VITE_OUT_DIR

# 获取项目根目录
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

echo "=========================================="
echo " Building Frontend (Aliyun) - $MODE "
echo "=========================================="

# 1. 进入前端目录
cd "$PROJECT_ROOT/hzapp-ui/hzapp-ui-admin-vue3-tiny"

# 2. 安装依赖 (本地环境)
echo "Installing frontend dependencies..."
pnpm install

# 3. 本地编译
echo "Compiling frontend program (Mode: $MODE, Output: $VITE_OUT_DIR)..."
pnpm run build:"$MODE"

# 4. 准备 Docker 构建上下文
echo "Transferring frontend artifacts to docker build context..."
# 确保目标目录存在
mkdir -p "$SCRIPT_DIR/docker/hzapp-ui-admin/dist"
# 清理旧产物
rm -rf "$SCRIPT_DIR/docker/hzapp-ui-admin/dist/"*
# 拷贝新产物
cp -r "$VITE_OUT_DIR"/* "$SCRIPT_DIR/docker/hzapp-ui-admin/dist/"

# 5. 构建镜像
echo "Building Frontend Docker image for Aliyun..."
cd "$SCRIPT_DIR/docker/hzapp-ui-admin"

FULL_IMAGE_NAME="$REGISTRY/$NAMESPACE/$ADMIN_IMAGE_NAME:$IMAGE_TAG"

# 注意：这里使用本地的 Dockerfile，它应该已经配置好从 ./dist 目录拷贝内容
docker build -t "$FULL_IMAGE_NAME" .

# 6. 推送镜像
echo "Pushing Frontend Docker image to Aliyun: $FULL_IMAGE_NAME"
docker push "$FULL_IMAGE_NAME"

echo "=========================================="
echo " Aliyun Frontend build and push completed! "
echo " Image: $FULL_IMAGE_NAME "
