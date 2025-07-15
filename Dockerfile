# Etapa de construcción con Maven
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests

# Etapa de ejecución con Java
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/target/app.jar ./app.jar
ENV PORT=10000
EXPOSE 10000
CMD ["java", "-jar", "app.jar"]