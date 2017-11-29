FROM openjdk:8-jdk-slim
COPY ./target/hunchback-backend-*.jar hunchback-backend.jar
EXPOSE 8080 7890
ENTRYPOINT ["java", "-jar", "hunchback-backend.jar"]