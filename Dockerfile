#Use  OPEN JDK 17 base image
FROM eclipse-temurin:17-jdk-alpine

#Set work directory
WORKDIR /app

#Copy JAR into container
COPY target/AuthService-0.0.1-SNAPSHOT.jar app.jar

#Expose port
EXPOSE 8080

#Command to run the jar
ENTRYPOINT ["java", "-jar","app.jar"]
