
ARG PRO_DIR=/hzapp-ui-admin-vue3

FROM node:18 as builder

ARG PRO_DIR

WORKDIR $PRO_DIR

RUN npm install -g pnpm

RUN pnpm config set registry https://registry.npmmirror.com

COPY . ./

RUN pnpm install

RUN pnpm run build:prod


FROM nginx:1.21.6-alpine as production

ARG PRO_DIR

COPY --from=builder $PRO_DIR/dist-prod /usr/share/nginx/html

EXPOSE 80

