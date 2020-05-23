FROM openjdk:11.0.7-jre

LABEL name="Catan Server"

COPY ./target/catan-server-*-fat.jar /usr/app/application.jar
WORKDIR /usr/app
EXPOSE 8080

CMD ["java", "-jar", "application.jar", "server", "config.yaml"]