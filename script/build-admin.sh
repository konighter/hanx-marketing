#!/bin/bash

# Exit on error
set -e

# Default mode is prod
MODE="${1:-prod}"
echo "Build Mode: $MODE"

# Default registry if not provided
REGISTRY="${REGISTRY:-192.168.1.13:5000}"
ADMIN_IMAGE_NAME="hzapp-admin"
IMAGE_TAG="latest"

# Set output directory based on mode
VITE_OUT_DIR="dist-$MODE"
export VITE_OUT_DIR

# Get absolute path to the project root
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

echo "=========================================="
echo " Building Frontend ($ADMIN_IMAGE_NAME) - $MODE "
echo "=========================================="

# Using the tiny template path
cd "$PROJECT_ROOT/hzapp-ui/hzapp-ui-admin-vue3-tiny"

echo "Installing frontend dependencies..."
pnpm install --registry=https://registry.npmmirror.com

echo "Compiling frontend program (Mode: $MODE, Output: $VITE_OUT_DIR)..."
# Verify VITE_BASE_URL in the build environment (.env files will be loaded by Vite)
pnpm run build:$MODE

echo "Transferring frontend artifacts to docker build context..."
mkdir -p "$SCRIPT_DIR/docker/hzapp-ui-admin/dist"
rm -rf "$SCRIPT_DIR/docker/hzapp-ui-admin/dist/"*
cp -r "$VITE_OUT_DIR"/* "$SCRIPT_DIR/docker/hzapp-ui-admin/dist/"

echo "Building Frontend Docker image..."
cd "$SCRIPT_DIR/docker/hzapp-ui-admin"
docker build -t "$REGISTRY/$ADMIN_IMAGE_NAME:$IMAGE_TAG" .

echo "Pushing Frontend Docker image to private registry: $REGISTRY"
docker push "$REGISTRY/$ADMIN_IMAGE_NAME:$IMAGE_TAG"

echo "=========================================="
echo " Frontend build and push completed! "
echo "=========================================="
