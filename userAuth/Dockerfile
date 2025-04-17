FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/userAuth-0.0.1-SNAPSHOT.jar userauth.jar
ENTRYPOINT ["java", "-jar", "userauth.jar"]
EXPOSE 8081