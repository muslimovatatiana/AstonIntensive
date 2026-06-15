FROM maven:3.9.8-eclipse-temurin-22 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:resolve-plugins dependency:resolve -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:22-jre-jammy
WORKDIR /app

COPY --from=build /app/target/user-service-*.jar app.jar

ENV TZ=Europe/Moscow

ENTRYPOINT ["java", "-jar", "app.jar"]
