# Use official OpenJDK image
FROM openjdk:21-slim

# Set working directory
WORKDIR /app

# Copy compiled JAR file
COPY build/libs/*.jar app.jar

# Create logs directory for Log4j2
RUN mkdir -p /app/logs/organization-service

# Expose ports for metrics
EXPOSE 8084

# Run the application with Docker profile
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]



