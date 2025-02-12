#!/bin/bash

echo "Starting Minikube..."
minikube start

echo "Setting Docker environment for Minikube..."
eval $(minikube -p minikube docker-env)

echo "Building API Service..."
cd api-service && ./gradlew build && cd ..

echo "Building Worker Service..."
cd worker-service && ./gradlew build && cd ..

echo "Building Docker image for API Service..."
docker build -t local/api-service:v1 api-service/

echo "Building Docker image for Worker Service..."
docker build -t local/worker-service:v1 worker-service/

echo "Setting up HazelCast..."
kubectl apply -f hazelcast.yaml

echo "Applying Persistent Volume configuration..."
kubectl apply -f persistent-volume.yaml

echo "Applying RabbitMQ deployment configuration..."
kubectl apply -f rabbitmq-deployment.yaml

echo "Applying API Service deployment configuration..."
kubectl apply -f api-service/api-service-deployment.yml

echo "Applying Worker Service deployment configuration..."
kubectl apply -f worker-service/worker-service-deployment.yml

echo "Deployment complete!"
