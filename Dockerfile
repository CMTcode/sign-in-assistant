FROM openjdk:8
LABEL authors="ForkManTou"
COPY target/*.jar /app.jar
ENTRYPOINT ["java", "-jar","app.jar"]
