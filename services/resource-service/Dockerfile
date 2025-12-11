FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

# Copy Gradle wrapper and dependencies first (for caching)
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY src src

# Build the application
RUN ./gradlew clean build -x test --no-daemon

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]