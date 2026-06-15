FROM maven:3.9.6-eclipse-temurin-22-jammy AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:22-jre-jammy
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV TZ=Europe/Moscow

ENTRYPOINT ["java", "-jar", "app.jar"]
