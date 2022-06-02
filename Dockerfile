FROM openjdk:17-alpine
COPY ./build/libs/spotifybot-0.0.1-SNAPSHOT.jar /app/app.jar
WORKDIR /home/SPITCH/
ENTRYPOINT ["java","-jar","/app/app.jar"]
