FROM openjdk:17-slim
LABEL authors="lxy"

WORKDIR /app

EXPOSE 8761

COPY target/app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]