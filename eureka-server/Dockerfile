FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar eureka.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","app.jar"]