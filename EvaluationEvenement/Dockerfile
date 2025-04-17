FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/EvaluationEvenement-0.0.1-SNAPSHOT.jar evaluationevenement.jar
ENTRYPOINT ["java", "-jar", "evaluationevenement.jar"]
EXPOSE 8087