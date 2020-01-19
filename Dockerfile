FROM openjdk:11-jdk
MAINTAINER George Tsekouras <tsekouras.gt@gmail.com>
ADD ["target/event-log.jar", "event-log.jar"]
RUN apt-get update && apt-get -y install netcat && apt-get clean
EXPOSE 8081