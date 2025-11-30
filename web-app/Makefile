-include .env
export DOCKER_REGISTRY

VERSION := $(shell cat src/main/resources/VERSION.txt)


.PHONY: help build run docker-build docker-run docker-push docker-deploy clean

.DEFAULT_GOAL := help

help: ## Show this help message
	@echo 'Usage: make [target]'
	@echo ''
	@echo 'Available targets:'
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  %-15s %s\n", $$1, $$2}' $(MAKEFILE_LIST)

clean: ## Clean build artifacts
	mvn clean

build: ## Build the project with Maven
	mvn clean package

test: ## Run unit tests
	mvn test

run: build ## Build and run the application locally
	mvn exec:java -Dexec.mainClass="com.example.bakkerij.Application"

docker-build: ## Build Docker image
	docker build -t bakkerij:$(VERSION) .

docker-run: docker-build ## Build and run Docker container
	docker run -p 7070:7070 bakkerij:$(VERSION)

docker-push: docker-build ## Build, tag and push Docker image to registry
	docker tag bakkerij:$(VERSION) $$DOCKER_REGISTRY/bakkerij:$(VERSION)
	docker tag bakkerij:$(VERSION) $$DOCKER_REGISTRY/bakkerij:latest
	docker push $$DOCKER_REGISTRY/bakkerij:$(VERSION)
	# latest tag for convenience
	docker push $$DOCKER_REGISTRY/bakkerij:latest

docker-deploy: docker-push ## Build, push and deploy to remote server
	ssh pi5 'cd dockers/breadandbytes && docker-compose pull && docker compose down && docker-compose up -d'