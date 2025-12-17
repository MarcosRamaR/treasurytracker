#Use an official java image
FROM eclipse-temurin:17-jdk-alpine
#Creates a work directory
WORKDIR /app
#Copy the JAR builded by Maven
COPY target/*.jar app.jar
#Expose the port of Spring Boot
EXPOSE 8080
#Command to run the app
ENTRYPOINT ["java", "-jar", "app.jar"]