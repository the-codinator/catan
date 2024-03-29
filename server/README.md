# Catan Server

## Local Development

1. Run `mvn clean install` to build your application
1. Start application with `java -jar target/catan-server-1.0.jar server config.yaml`
1. Your application will be running at `http://localhost:8080`

## Database

By default, the application uses an In Memory Database if unspecified (useful for local development). \
To enable a specific DB, set the environment variable `CATAN_DATABASE_TYPE`. Valid choices at [DatabaseType](src/main/java/org/codi/catan/impl/data/DatabaseType.java) \
Additional required environment variables for database credentials based on provider are:

- AWS Dynamo DB - `CATAN_AWS_ACCESS_KEY_ID` and `CATAN_AWS_SECRET_ACCESS_KEY`
- Azure Cosmos DB - `CATAN_AZURE_COSMOS_DB_CONNECTION_STRING` 

## Run with Docker

### Automatically Run with `make`

Run `make local-docker` to start it with some basic options and resources

### Manually Build and Run Docker Image

Build the docker images using `make image` \
Run the image manually using `docker run -p 8080:8080 -e CATAN_DATABASE_TYPE=inMemory catan-server` \
If using a different database, add any other necessary env vars for credentials using `-e key=val` per var.

### Docker Compose

Run `make image` first for building the image (in not built already) \
Run using `docker-compose up -d` (`-d` for background mode) - starts the server with a local dynamo db container as persistence \
Use `docker-compose down` to stop the server

### JVM Options

The `JDK_JAVA_OPTIONS` environment can be set to include JVM args. \
This is as per the standard [Java docs](https://docs.oracle.com/en/java/javase/11/tools/java.html#GUID-3B1CE181-CD30-4178-9602-230B800D4FAE__USINGTHEJDK_JAVA_OPTIONSLAUNCHERENV-F3C0E3BA) by Oracle \
For example `-e JDK_JAVA_OPTIONS="-Xmx512m -Xms512m"`

## Swagger

View Swagger docs at `/swagger`

## Health Check

Liveness & Readiness Probe: Ping the application at `/ping` (might split in the future) \
Detailed Health Check: available at `/healthcheck` \
Health Check is also available at `http://localhost:8081/healthcheck` (default DW admin API)

## Deployment

Build and publish the image to `docker.io` (DockerHub) using `make publish` \
Ensure that you have logged into the Docker repository (`docker login`) before running the command to ensure push access is available \
Set a custom repository using the environment variable `PUBLISH_DOCKER_REPOSITORY` before running the `make` command

## Implementation TODOs

- Actual DB integration
- Test cases
- Long Poll for GET State
- Server Hosting (GCP K8s engine, Azure Container Instances, AWS Elastic Container Service, ...)
- Server Monitoring (New Relic Lite?)
