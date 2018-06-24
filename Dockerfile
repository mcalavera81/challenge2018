FROM openjdk:8-slim as build
RUN apt-get update && apt-get install -y --no-install-recommends openjfx && rm -rf /var/lib/apt/lists/*

#FROM openjdk:8-jdk

WORKDIR /app
COPY .  /app

RUN ./gradlew build

#FROM openjdk:10-jre-slim
#EXPOSE 8080
#COPY --from=builder /home/gradle/src/easytext.web/build/distributions/easytext.web.tar /app/
#WORKDIR /app
#RUN tar -xvf easytext.web.tar
#WORKDIR /app/easytext.web
#CMD bin/easytext.web