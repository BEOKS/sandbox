FROM gradle:8.5-jdk17 AS build
WORKDIR /workspace

# Copy gradle files
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle

# Copy source code
COPY common ./common
COPY infrastructure ./infrastructure
COPY admin-api ./admin-api

# Build the application
RUN gradle :admin-api:bootJar --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built jar
COPY --from=build /workspace/admin-api/build/libs/*.jar app.jar

# Expose port
EXPOSE 8888

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
