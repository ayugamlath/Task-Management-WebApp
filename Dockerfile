# Use an official Java 24 base image
FROM eclipse-temurin:24-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file to the container
COPY target/*.jar app.jar

# Set the port (Render will pass $PORT)
ENV PORT=8080

# Expose the port
EXPOSE 8080

# Start the Spring Boot application
CMD ["java", "-jar", "app.jar"]
