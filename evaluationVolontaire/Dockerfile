FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/evaluation-volontaire-service-0.0.1-SNAPSHOT.jar evaluationvolontaire.jar
ENTRYPOINT ["java", "-jar", "evaluationvolontaire.jar"]
EXPOSE 8088