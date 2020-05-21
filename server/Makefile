
default: local

local:
	mvn package -DskipTests

build:
	mvn clean package

image: build
	docker build -t catan-server .
