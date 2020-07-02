FROM alpine:3.12

LABEL name="Catan Server"
WORKDIR /root
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=2 \
  CMD curl -f http://localhost:8080/ || exit 1

RUN apk update && apk upgrade && apk add ca-certificates curl openjdk11-jre
COPY target/catan-server-1.0-fat.jar /root/application.jar

CMD ["java", "-jar", "application.jar", "server", "config.yaml"]
