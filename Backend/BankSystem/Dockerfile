# Build stage
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /opt/app
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline

COPY src src
RUN ./mvnw package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-jammy

WORKDIR /opt/app
COPY --from=builder /opt/app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
