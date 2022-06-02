FROM openjdk:17-alpine
COPY ./spotifybot-0.0.1-SNAPSHOT.jar /app/app.jar
WORKDIR /root/
ENTRYPOINT ["java","-jar","/app/app.jar"]
