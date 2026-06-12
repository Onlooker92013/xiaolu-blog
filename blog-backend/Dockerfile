FROM openjdk:17-slim

WORKDIR /app

COPY target/*.jar app.jar

RUN mkdir -p /app/uploads

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]
