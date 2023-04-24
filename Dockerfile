FROM openjdk:8
COPY /config/ /config/
LABEL authors="ForkManTou"
COPY /target/*.jar /app.jar
ENTRYPOINT ["java", "-jar","app.jar"]
