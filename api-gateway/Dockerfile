FROM bellsoft/liberica-openjdk-alpine:22
COPY ./api-gateway/target/api-gateway-1.0-SNAPSHOT.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "/app.jar"]
