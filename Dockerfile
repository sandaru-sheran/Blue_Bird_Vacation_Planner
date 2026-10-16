# Stage 1: Build the application using Maven with JDK 21
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY target .
RUN mvn clean package -DskipTests

# Stage 2: Run the application using JRE 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/Blue_Bird_Vacation_Planner-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
