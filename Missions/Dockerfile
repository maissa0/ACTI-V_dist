FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/Missions-0.0.1-SNAPSHOT.jar missions.jar
ENTRYPOINT ["java", "-jar", "missions.jar"]
EXPOSE 8089