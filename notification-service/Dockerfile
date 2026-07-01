FROM bellsoft/liberica-openjdk-alpine:22
COPY notification-service/target/*.jar app.jar
ENV TZ=Europe/Moscow
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app.jar"]
