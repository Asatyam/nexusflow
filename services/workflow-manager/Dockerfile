FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/services/workflow-manager/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]