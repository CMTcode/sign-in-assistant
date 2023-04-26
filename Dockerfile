FROM openjdk:8
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo '$TZ' > /etc/timezone
COPY /config/ /config/
LABEL authors="ForkManTou"
COPY /target/*.jar /app.jar
ENTRYPOINT ["java", "-jar","app.jar"]
