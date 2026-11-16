# ==========================================
# Stage 1: Build Spring Boot Application
# ==========================================
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy Maven configuration
COPY pom.xml .

# Copy source code
COPY src ./src

# Build Spring Boot JAR
RUN mvn clean package -DskipTests


# ==========================================
# Stage 2: Run Spring Boot Application
# ==========================================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Spring Boot port
EXPOSE 8080

# Start application
ENTRYPOINT ["java", "-jar", "app.jar"]