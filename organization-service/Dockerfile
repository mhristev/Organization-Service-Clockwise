# Use official OpenJDK image
FROM openjdk:21-slim

# Set working directory
WORKDIR /app

# Copy compiled JAR file
COPY build/libs/*.jar app.jar

# Create logs directory for Log4j2
RUN mkdir -p /app/logs/organization-service

# Expose ports for metrics
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-Dspring.profiles.active=metrics", "-jar", "app.jar"]



