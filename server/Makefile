
BUILDER_IMAGE:=maven:3.6-openjdk-11
BUILD_SCRIPT:=build.sh
PUBLISH_DOCKER_REPOSITORY?=codinator/codinators
PUBLISH_IMAGE_VERSION?=1.0
PUBLISH_IMAGE_TAG:=catan-server_${PUBLISH_IMAGE_VERSION}

default: local

local: build
	java -jar ./target/catan-server-1.0-fat.jar server config.yaml

build:
	sh ${BUILD_SCRIPT}

build-safe:
	docker run --rm -w /build -v ${HOME}/.m2:/root/.m2 -v `pwd`:/build ${BUILDER_IMAGE} sh ${BUILD_SCRIPT}

image: build-safe
	docker build -t catan-server .

publish: image
	docker tag catan-server ${PUBLISH_DOCKER_REPOSITORY}:${PUBLISH_IMAGE_TAG}
	docker login
	docker push ${PUBLISH_DOCKER_REPOSITORY}:${PUBLISH_IMAGE_TAG}
	docker logout
