FROM openjdk:11.0.7-jre-slim

LABEL name="Catan Server"

COPY target/catan-server-1.0-fat.jar /usr/app/application.jar
WORKDIR /usr/app
EXPOSE 8080

CMD ["java", "-jar", "application.jar", "server", "config.yaml"]