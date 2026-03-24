#!/bin/bash

# Exit on error
set -e

# Default registry if not provided
REGISTRY="${REGISTRY:-192.168.1.13:5000}"
SERVER_IMAGE_NAME="hzapp-server"
ADMIN_IMAGE_NAME="hzapp-admin"
IMAGE_TAG="latest"

# Get absolute path to the project root
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

echo "=========================================="
echo " 1. Building Backend ($SERVER_IMAGE_NAME) "
echo "=========================================="

cd "$PROJECT_ROOT"

echo "Compiling backend program..."
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

echo "Pushing Backend Docker image to private registry: $REGISTRY"
docker push "$REGISTRY/$SERVER_IMAGE_NAME:$IMAGE_TAG"

echo "=========================================="
echo " 2. Building Frontend ($ADMIN_IMAGE_NAME) "
echo "=========================================="

cd "$PROJECT_ROOT/hzapp-ui/hzapp-ui-admin-vue3-tiny"

echo "Installing frontend dependencies..."
pnpm install --registry=https://registry.npmmirror.com

echo "Compiling frontend program..."
VITE_BASE_URL="" pnpm run build:prod

echo "Transferring frontend artifacts to docker build context..."
mkdir -p "$SCRIPT_DIR/docker/hzapp-ui-admin/dist"
rm -rf "$SCRIPT_DIR/docker/hzapp-ui-admin/dist/"*
cp -r dist/* "$SCRIPT_DIR/docker/hzapp-ui-admin/dist/"

echo "Building Frontend Docker image..."
cd "$SCRIPT_DIR/docker/hzapp-ui-admin"
docker build -t "$REGISTRY/$ADMIN_IMAGE_NAME:$IMAGE_TAG" .

echo "Pushing Frontend Docker image to private registry: $REGISTRY"
docker push "$REGISTRY/$ADMIN_IMAGE_NAME:$IMAGE_TAG"

echo "=========================================="
echo " All builds and pushes completed successfully! "
echo "=========================================="
