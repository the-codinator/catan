# Catan Server

How to start the catan-server application
---

1. Run `mvn clean install` to build your application
1. Start application with `java -jar target/catan-server-1.0.jar server config.yaml`
1. To check that your application is running enter url `http://localhost:8080`

Run with Docker
---

Build the docker images using `make image`\
Start the image with a local dynamodb container using `docker-compose up -d` (`-d` for background mode,
`docker-compose down` to stop)\
Run the image manually using `docker run -p 8080:8080 -e X=Y catan-server`

Health Check
---

To see your applications health enter url `http://localhost:8081/healthcheck`
