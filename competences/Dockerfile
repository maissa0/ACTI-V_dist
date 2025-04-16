FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar competence.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","app.jar"]