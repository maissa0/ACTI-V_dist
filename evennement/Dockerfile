FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/evennement-0.0.1-SNAPSHOT.jar evennement.jar
ENTRYPOINT ["java", "-jar", "evennement.jar"]
EXPOSE 8083