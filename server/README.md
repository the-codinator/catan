# Catan Server

How to start the catan-server application
---

1. Run `mvn clean install` to build your application
1. Start application with `java -jar target/catan-server-1.0.jar server config.yaml`
1. Your application will be running at `http://localhost:8080`

Run with Docker
---

Build the docker images using `make image` \
Start the image with a local dynamodb container using `docker-compose up -d` (`-d` for background mode,
`docker-compose down` to stop) \
Run the image manually using `docker run -p 8080:8080 -e X=Y catan-server`

Health Check
---

Ping the application to check live-ness at `/ping` \
To see your applications health enter url `http://localhost:8081/healthcheck` \
This is also proxied at `/health` for external access

Swagger
---

View Swagger docs at `/swagger`
