FROM openjdk:13-jdk-alpine3.10

WORKDIR /app

COPY target/measurement-server-1.0-SNAPSHOT.jar .

ENV PORT=80
CMD ["/usr/bin/java", "measurement-server-1.0-SNAPSHOT.jar", "server", "config.yml"]
