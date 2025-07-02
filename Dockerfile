# Dockerfile
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# 複製項目文件
COPY ./Back-end/pom.xml ./pom.xml
COPY ./Back-end/src ./src
COPY ./Back-end/.mvn ./.mvn
COPY ./Back-end/mvnw ./mvnw
COPY ./Back-end/mvnw.cmd ./mvnw.cmd

# 構建應用程式
RUN mvn clean package -DskipTests

# 運行階段
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 從構建階段複製 JAR 文件
COPY --from=build /app/target/*.jar app.jar

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=cloud
ENV PORT=8080
ENV JAVA_OPTS="-Xmx512m -Xms256m"


# 暴露端口
EXPOSE ${PORT} 

# 運行應用程式
CMD ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]