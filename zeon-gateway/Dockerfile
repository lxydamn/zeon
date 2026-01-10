FROM openjdk:17-jdk-slim
LABEL authors="lxy"

WORKDIR /app

EXPOSE 8080

COPY target/app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]