FROM bellsoft/liberica-openjdk-alpine:22
COPY discovery-server/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "/app.jar"]
