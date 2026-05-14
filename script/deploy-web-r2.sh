#!/bin/bash

# --- 配置区 (必须通过环境变量传入，保护隐私) ---
# 前端项目路径 (相对于脚本所在目录)
WEB_PATH="../hzapp-ui/hzapp-ui-admin-vue3-tiny"
# R2 终端地址
ENDPOINT=${R2_ENDPOINT}
# 存储桶名称
BUCKET_NAME=${R2_BUCKET_NAME:-"erplus-web"}

# 检查环境变量
if [ -z "$ENDPOINT" ] || [ -z "$BUCKET_NAME" ]; then
    echo "❌ 错误: 未设置必要环境变量 R2_ENDPOINT 或 R2_BUCKET_NAME"
    echo "使用示例: export R2_ENDPOINT='https://xxx.r2.cloudflarestorage.com' R2_BUCKET_NAME='erplus-web'"
    exit 1
fi
# --- --- --- ---

# 检查 aws-cli 是否安装
if ! command -v aws &> /dev/null; then
    echo "❌ 错误: 未安装 aws-cli，请先安装 (macOS: brew install awscli)"
    exit 1
fi

# 进入前端项目目录
SCRIPT_DIR=$(cd $(dirname $0); pwd)
cd "$SCRIPT_DIR/$WEB_PATH"

# 1. 开始构建
echo "🚀 [1/2] 开始构建前端项目..."
pnpm install --frozen-lockfile
pnpm build:prod

if [ $? -ne 0 ]; then
    echo "❌ 构建失败，请检查错误输出"
    exit 1
fi

echo "✅ 构建完成。"

# 2. 上传到 R2
echo "☁️ [2/2] 正在同步到 Cloudflare R2..."

# 使用 aws s3 sync 同步 dist 目录
# --delete 会删除存储桶中存在但本地 dist 中不存在的文件 (保持完全一致)
aws s3 sync dist-prod/ s3://$BUCKET_NAME \
    --endpoint-url $ENDPOINT \
    --region auto \
    --delete

if [ $? -eq 0 ]; then
    echo "------------------------------------------------"
    echo "🎉 部署成功！静态资源已同步至 R2: $BUCKET_NAME"
    echo "------------------------------------------------"
else
    echo "❌ 上传失败！"
    echo "请检查："
    echo "1. 是否配置了环境变量 AWS_ACCESS_KEY_ID 和 AWS_SECRET_ACCESS_KEY"
    echo "2. R2 Token 权限是否包含 'Edit' (读写权限)"
    echo "3. 当前 ENDPOINT: $ENDPOINT"
    echo "4. 当前 BUCKET_NAME: $BUCKET_NAME"
    exit 1
fi
