FROM maven:3.9.4-eclipse-temurin-17
WORKDIR /app
COPY . .

RUN mvn package

CMD ["java", "-cp", "target/classes", "Main"]