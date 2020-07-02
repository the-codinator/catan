
BUILDER_IMAGE:=maven:3.6-openjdk-11
BUILD_SCRIPT:=build.sh
DOCKER:=sudo docker
PUBLISH_DOCKER_REPOSITORY?=codinator/codinators
PUBLISH_IMAGE_VERSION?=1.0
PUBLISH_IMAGE_TAG:=catan-server_${PUBLISH_IMAGE_VERSION}

default: local

local: build
	java -jar ./target/catan-server-1.0-fat.jar server config.yaml

build:
	sh ${BUILD_SCRIPT}

build-safe:
	${DOCKER} run --rm -w /build -v ${HOME}/.m2:/root/.m2 -v `pwd`:/build ${BUILDER_IMAGE} sh ${BUILD_SCRIPT}

image: build-safe
	${DOCKER} build -t catan-server .

local-docker: image
	${DOCKER} run --rm --cpus .5 -m 1g -p 8080:8080 -e JDK_JAVA_OPTIONS="-Xmx512m -Xms512m" catan-server

publish: image
	${DOCKER} tag catan-server ${PUBLISH_DOCKER_REPOSITORY}:${PUBLISH_IMAGE_TAG}
	${DOCKER} login
	${DOCKER} push ${PUBLISH_DOCKER_REPOSITORY}:${PUBLISH_IMAGE_TAG}

