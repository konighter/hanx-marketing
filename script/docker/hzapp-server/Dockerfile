FROM eclipse-temurin:17-jre

## 创建目录，并使用它作为工作目录
RUN mkdir -p /hzapp-server
WORKDIR /hzapp-server

## 将后端项目的 Jar 文件，复制到镜像中。
## 假定将打包好的 jar 放在该 Dockerfile 同级目录下
COPY *.jar app.jar

## 设置 TZ 时区
ENV TZ=Asia/Shanghai
## 设置 JAVA_OPTS 环境变量，可通过 docker run -e "JAVA_OPTS=" 进行覆盖
ENV JAVA_OPTS="-Xms512m -Xmx512m -Djava.security.egd=file:/dev/./urandom"

## 应用参数
ENV ARGS=""

## 暴露后续服务的 48080 端口
EXPOSE 48080

## 启动后端项目
CMD java ${JAVA_OPTS} -jar app.jar $ARGS
