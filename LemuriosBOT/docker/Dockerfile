# Dockerfile - multi-stage build with sharding support

FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
# create a non-root user <lemurios> to run everything
RUN useradd -m lemurios
COPY --from=build /app/target/LEMURIOS_BOT.jar app.jar
RUN chown -R lemurios:lemurios /app
EXPOSE 18081
USER lemurios
ENTRYPOINT ["java", "-jar", "app.jar"]
