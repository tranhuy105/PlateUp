FROM openjdk:21-jdk-slim
WORKDIR /app
COPY . /app
RUN chmod +x mvnw
RUN ./mvnw clean package
EXPOSE 12345
CMD ["java", "-jar", "target/plate-up-1.0-SNAPSHOT.jar"]