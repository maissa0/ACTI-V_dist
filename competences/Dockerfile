FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/competences-0.0.1-SNAPSHOT.jar competences.jar
ENTRYPOINT ["java", "-jar", "competences.jar"]
EXPOSE 8082