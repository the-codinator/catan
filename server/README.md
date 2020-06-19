# Catan Server

How to start the catan-server application
---

1. Run `mvn clean install` to build your application
1. Start application with `java -jar target/catan-server-1.0.jar server config.yaml`
1. Your application will be running at `http://localhost:8080`

Database
---

By default, the application uses an In Memory Database if unspecified (useful for local development). \
To enable a specific DB, set the env var `CATAN_DATABASE_TYPE` to one of the values in [DatabaseType](src/main/java/org/codi/catan/impl/data/DatabaseType.java) \
See below for additional required environment variables for database credentials based on provider

- AWS Dynamo DB - `CATAN_AWS_ACCESS_KEY_ID` and `CATAN_AWS_SECRET_ACCESS_KEY`
- Azure Cosmos DB - `CATAN_AZURE_COSMOS_DB_CONNECTION_STRING` 

Run with Docker
---

Build the docker images using `make image` \
Start the image with a local dynamodb container using `docker-compose up -d` (`-d` for background mode,
`docker-compose down` to stop) \
Run the image manually using `docker run -p 8080:8080 -e CATAN_DATABASE_TYPE=inMemory catan-server`\
If using a different database, add any other necessary env vars for credentials using `-e key=val` per var.

Health Check
---

Ping the application to check live-ness at `/ping` \
Detailed health check is available at `/healthcheck` \
This is also available at `http://localhost:8081/healthcheck` (default DW admin API)

Swagger
---

View Swagger docs at `/swagger`

Implementation TODOs
---

- Actual DB integration
- Play DevCard API
- Test cases
- Long Poll for GET State
- Server Hosting
