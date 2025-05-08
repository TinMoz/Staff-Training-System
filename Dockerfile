# Dockerfile
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# 複製項目文件
COPY ./Back-end/pom.xml ./pom.xml
COPY ./Back-end/src ./src
COPY ./Back-end/.mvn ./.mvn
COPY ./Back-end/mvnw ./mvnw
COPY ./Back-end/mvnw.cmd ./mvnw.cmd

# 構建
RUN chmod +x ./mvnw && ./mvnw clean package -DskipTests

# 運行
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# 複製Jar
COPY --from=build /app/target/*.jar app.jar

# 添加啟動
RUN echo '#!/bin/sh\n\
java ${JAVA_OPTS} -jar app.jar ${0} ${@}' > ./entrypoint.sh && \
chmod +x ./entrypoint.sh

EXPOSE 8080
ENTRYPOINT ["./entrypoint.sh"]