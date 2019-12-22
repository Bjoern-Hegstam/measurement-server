FROM openjdk:13-jdk-alpine3.10
ARG APP_VERSION

WORKDIR /app

COPY target/measurement-server-${APP_VERSION}.jar .

ENV PORT=80
CMD ["/usr/bin/java", "measurement-server-${APP_VERSION}.jar", "server", "config.yml"]
