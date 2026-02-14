#Stage 1#
FROM maven:3.9-eclipse-temurin-25-alpine AS build
WORKDIR /build/
COPY pom.xml /build/
RUN mvn dependency:go-offline
COPY src /build/src/
RUN mvn package -DskipTests

#Stage 2#
FROM eclipse-temurin:25
ARG JAR_FILE=/build/target/*.jar
COPY --from=build $JAR_FILE /opt/telegram-bot-service/app.jar
ENTRYPOINT ["java", "-jar", "opt/telegram-bot-service/app.jar"]