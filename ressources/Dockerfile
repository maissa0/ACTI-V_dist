FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/RessourcesMS-0.0.1-SNAPSHOT.jar ressources.jar
ENTRYPOINT ["java", "-jar", "ressources.jar"]
EXPOSE 8086