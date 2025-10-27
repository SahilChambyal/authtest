# syntax=docker/dockerfile:1.7

# ---- Build stage ----
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copy Gradle wrapper & build files first
COPY gradlew gradlew
COPY gradle gradle
# Groovy DSL:
COPY build.gradle settings.gradle ./
# Kotlin DSL alternative:
# COPY build.gradle.kts settings.gradle.kts ./

# Prime Gradle deps cache
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon build -x test || true

# Copy sources and build
COPY src src
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon clean bootJar -x test

# Extract Boot jar into layers for better Docker cache reuse
RUN mkdir -p /layers && \
    java -Djarmode=layertools -jar build/libs/*.jar extract --destination /layers
# ---- Runtime stage (small) ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# (Optional) Non-root user
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

# Copy Spring Boot layers separately for better caching
COPY --from=builder /layers/dependencies/ ./dependencies/
COPY --from=builder /layers/snapshot-dependencies/ ./snapshot-dependencies/
COPY --from=builder /layers/spring-boot-loader/ ./spring-boot-loader/
COPY --from=builder /layers/application/ ./application/

EXPOSE 8080
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+UseContainerSupport"
# ENV SPRING_PROFILES_ACTIVE=prod

# Run via Spring Boot loader to use layers
ENTRYPOINT ["java","org.springframework.boot.loader.launch.JarLauncher"]
``