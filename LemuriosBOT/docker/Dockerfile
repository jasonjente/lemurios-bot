# Use the official OpenJDK 8 JRE image as the base image
FROM openjdk:8-jre-alpine


# Set the working directory to /app
WORKDIR /app
COPY target/"bot-1.jar" /app
# Copy the JAR file into the container at /app
RUN chmod +x /app/"bot-1.jar"
# Run the command to start the bot
CMD ["java", "-jar", "bot-1.jar"]
