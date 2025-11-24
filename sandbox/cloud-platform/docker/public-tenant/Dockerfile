FROM gradle:8.5-jdk17 AS build
WORKDIR /workspace

# Copy gradle files
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle

# Copy source code
COPY common ./common
COPY infrastructure ./infrastructure
COPY public-tenant-api ./public-tenant-api

# Build the application
RUN gradle :public-tenant-api:bootJar --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built jar
COPY --from=build /workspace/public-tenant-api/build/libs/*.jar app.jar

# Expose port
EXPOSE 9090

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
