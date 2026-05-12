#!/bin/bash

# Exit on error
set -e

# Default registry if not provided
REGISTRY="${REGISTRY:-registry.cn-hangzhou.aliyuncs.com/hanx-dev}"
SERVER_IMAGE_NAME="hzapp-erplus-server"
IMAGE_TAG="latest"

# Get absolute path to the project root
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

echo "=========================================="
echo " Building Backend ($SERVER_IMAGE_NAME) "
echo "=========================================="

cd "$PROJECT_ROOT"

# echo "Compiling backend program using Maven with JDK 17..."
# docker volume create --name hzapp-maven-repo >/dev/null 2>&1 || true
# docker run --rm --name hzapp-maven-build \
#     -v hzapp-maven-repo:/root/.m2 \
#     -v "$PROJECT_ROOT":/usr/src/mymaven \
#     -w /usr/src/mymaven \
#     maven:3.9-eclipse-temurin-17 \
#     mvn clean install package -Dmaven.test.skip=true


echo "Compiling backend program locally (Requires JDK 17)..."
# 注意：本地编译前请确保安装了 JDK 17 并且配好了有私有包的 maven settings
mvn clean install package -Dmaven.test.skip=true

echo "Transferring backend artifacts to docker build context..."
mkdir -p "$SCRIPT_DIR/docker/hzapp-server"

# Find the executable jar (ignoring .original or sources jars)
JAR_FILE=$(find hzapp-server/target -maxdepth 1 -name "*.jar" ! -name "*.original" ! -name "*-sources.jar" | head -n 1)
if [ -z "$JAR_FILE" ]; then
    echo "Error: Could not find compiled jar in hzapp-server/target/"
    exit 1
fi

echo "Copying $JAR_FILE to docker context..."
cp -f "$JAR_FILE" "$SCRIPT_DIR/docker/hzapp-server/app.jar"

echo "Building Backend Docker image..."
cd "$SCRIPT_DIR/docker/hzapp-server"
docker build -t "$REGISTRY/$SERVER_IMAGE_NAME:$IMAGE_TAG" .

if [ -n "$DOCKER_PASSWORD" ]; then
    echo "Logging in to Aliyun Registry..."
    echo "$DOCKER_PASSWORD" | docker login --username=konighter8212 registry.cn-hangzhou.aliyuncs.com --password-stdin
fi

echo "Pushing Backend Docker image to private registry: $REGISTRY"
docker push "$REGISTRY/$SERVER_IMAGE_NAME:$IMAGE_TAG"

echo "=========================================="
echo " Backend build and push completed! "
echo "=========================================="
