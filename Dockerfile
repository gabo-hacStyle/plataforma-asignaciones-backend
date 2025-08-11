FROM maven:3 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY src/main/resources/application.properties ./src/main/resources/application.properties
RUN mvn clean package -X -DskipTests

FROM openjdk:21
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar","app.jar"]