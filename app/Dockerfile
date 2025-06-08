FROM maven:3.9.9-eclipse-temurin-21 AS build
LABEL authors="cesar"

WORKDIR /app

COPY ./pom.xml .
RUN mvn dependency:go-offline

COPY ./src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S nonroot && adduser -S -G nonroot nonroot

USER nonroot
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]