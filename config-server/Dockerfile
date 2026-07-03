FROM bellsoft/liberica-openjdk-alpine:22
COPY config-server/target/*.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "/app.jar"]
