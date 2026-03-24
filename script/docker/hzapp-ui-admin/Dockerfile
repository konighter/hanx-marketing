FROM nginx:alpine

# 接收构建参数
ARG NODE_ENV
ARG PUBLIC_PATH
ARG VUE_APP_TITLE
ARG VUE_APP_BASE_API
ARG VUE_APP_APP_NAME
ARG VUE_APP_TENANT_ENABLE
ARG VUE_APP_CAPTCHA_ENABLE
ARG VUE_APP_DOC_ENABLE
ARG VUE_APP_BAIDU_CODE

# 拷贝基础配置
COPY nginx.conf /etc/nginx/nginx.conf

# 拷贝前端构建产物。
# 假定将通过 npm run build 构建好的 dist 放在该 Dockerfile 同级目录下
COPY ./dist /usr/share/nginx/html/

# 拷贝并设置入口脚本
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 80

ENTRYPOINT ["/entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]
