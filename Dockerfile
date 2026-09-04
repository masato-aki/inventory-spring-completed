FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY . .
RUN chmod +x gradlew && ./gradlew build -x test
EXPOSE 8080
CMD ["sh", "-c", "java -jar build/libs/*-SNAPSHOT.jar"]