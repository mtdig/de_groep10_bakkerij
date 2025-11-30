FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/degroep10bakkerij-1.0-SNAPSHOT.jar app.jar
EXPOSE 7070
CMD ["java", "-jar", "app.jar"]
