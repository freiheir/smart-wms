FROM gradle:8-jdk21-alpine AS build
WORKDIR /app
COPY build.gradle.kts ./
COPY gradle ./gradle
COPY gradlew gradlew.bat ./
RUN chmod +x gradlew
COPY src ./src
RUN ./gradlew build -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 5182
ENTRYPOINT ["java", "-jar", "app.jar"]
