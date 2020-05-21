FROM openjdk:11

LABEL name="Catan Server"

COPY ./target/catan-server-*-fat.jar /usr/app/application.jar
WORKDIR /usr/app
EXPOSE 8080

CMD ["java", "-jar", "application.jar", "server", "config.yaml"]