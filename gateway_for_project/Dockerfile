FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/gateway_for_project-0.0.1-SNAPSHOT.jar gateway_for_project.jar
ENTRYPOINT ["java", "-jar", "gateway_for_project.jar"]
EXPOSE 5000