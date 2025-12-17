FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

#Use an official java image
FROM eclipse-temurin:17-jdk-alpine
#Creates a work directory
WORKDIR /app
#Copy the JAR builded by Maven
COPY --from=build /app/target/*.jar app.jar
#Expose the port of Spring Boot
EXPOSE 8080
#Command to run the app
ENTRYPOINT ["java", "-jar", "app.jar"]