# Use the official OpenJDK 8 JRE image as the base image
FROM openjdk:8-jre-alpine


# Set the working directory to /app
WORKDIR /app
COPY ../target/"LEMURIOS_BOT.jar" /app
# Copy the JAR file into the container at /app
RUN chmod +x /app/"LEMURIOS_BOT.jar"
# Run the command to start the bot
CMD ["java", "-jar", "LEMURIOS_BOT.jar"]
