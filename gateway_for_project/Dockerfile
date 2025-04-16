FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar gateway.jar
EXPOSE 5000
ENTRYPOINT ["java","-jar","app.jar"]