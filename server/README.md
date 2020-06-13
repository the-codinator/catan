# Catan Server

How to start the catan-server application
---

1. Run `mvn clean install` to build your application
1. Start application with `java -jar target/catan-server-1.0.jar server config.yaml`
1. Your application will be running at `http://localhost:8080`

Database
---

By default, the application tries to use AWS Dynamo DB as its data store.
It connects using credentials specified in the `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY` environment variables.
In the absence of these variables (null or blank), an In-Memory data store is used.
This is particularly useful for local development.

Run with Docker
---

Build the docker images using `make image` \
Start the image with a local dynamodb container using `docker-compose up -d` (`-d` for background mode,
`docker-compose down` to stop) \
Run the image manually using `docker run -p 8080:8080 -e AWS_ACCESS_KEY_ID=***** -e AWS_SECRET_ACCESS_KEY=***** catan-server`\
Skip the AWS Credentials to use the In-Memory data store

Health Check
---

Ping the application to check live-ness at `/ping` \
To see your applications health enter url `http://localhost:8081/healthcheck` (default DW admin API) \
This is also available at `/healthcheck` for external access

Swagger
---

View Swagger docs at `/swagger`

Implementation TODOs
---

- Dynamo DB integration
- Play DevCard API
- Test cases
- Long Poll for GET State
- Server Hosting
