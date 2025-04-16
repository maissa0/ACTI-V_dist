FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar userauth.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]