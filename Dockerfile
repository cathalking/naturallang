FROM eclipse-temurin:21-jre-focal
WORKDIR /app
COPY build/libs/naturallang-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar","--server.port=8080"]
