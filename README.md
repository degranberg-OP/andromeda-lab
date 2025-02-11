# Andromeda tech lab

## Overview

This is just a simple project trying to incorporate all the technical aspects of the Andromeda Bootcamp course.
The project consists of 2 small services written in Java using SpringBoot.
The services will run in a kubernetes cluster.

The first service `api-serivce` simply sets up a REST api consisting of 2 endpoints. `upload` and `process_data`.
The `upload` endpoint accepts a file. This file will then be uploaded to a persistent volume in the kubernetes cluster.
Some metadata is also stored in the `HazelCast` cluster for fast retrieval later on.
The `process_data` endpoint accepts a filename. This filename should correspond to a previous uploaded file on the other endpoint.
The filename is then sent to a message queue on `RabbitMQ`.

The other service `worker-service` is a simple service that listens to the `RabbitMQ` queue that the `api-service` sends messages to.
The service will consume the message which only consists of a filename, then try to retrieve the metadata from the `HazelCast` cluster
and lastly try to look for the file in the shared persistent volume in the kubernetes cluster. If the file is found it will just print the contents.

## Tech Stack
The technologies used in this simple project is:

* Kubernetes
* Docker
* SpringBoot
* Gradle
* HazelCast
* RabbitMQ
* minikube
* OpenApi

## Prerequisites
Need to have the following installed:
* kubernetes-cli (kubectl)
* minikube
* Docker
* Gradle
* stern (Recommended for listening on pods)

## Setup
Starting all the services locally on a minikube cluster requires lots of commands to be executed sequentially.
Therefore, a script was created called `startup.sh`. Please inspect it with a text editor and see that you understand all the steps.

It will, in order do these things:
* Start minikube
* Change docker env to minikube
* Build both services with gradle
* Build both Docker images
* Apply the hazelcast, rabbitmq, persistent volume deployments to kubernetes.
* Apply the services to kubernetes

Once these steps are done, expose the api-service to your local network by running this command:
`minikube service api-service`. This will make the api-service available on your localhost,
you can see that the command will tell you what __PORT__ it uses. Copy this port so you can access it later. 

## Operation
Once the setup is complete and the service is exposed on a PORT, try to follow the pods in different terminal windows with `stern`.
Then try sending a POST request to the endpoints exposed by the `api-service` using the __PORT__ you retrieved earlier.

Replace PORTHERE with the actual port
```
UPLOAD DATA
curl -X POST http://localhost:PORTHERE/api/upload -F "file=@testdata.txt"

PROCESS DATA
curl -X POST http://localhost:PORTHERE/api/process_data -H "Content-Type: text/plain; charset: utf-8" -d 'testdata.txt'
```

You should see that the services has received the requests/messages.
If it seems like the worker-service crashes or doesnt work, try killing the pod and let it restart. Then it should work.
`kubectl get pods`
`kubectl delete pod ` enter podname here

I suspect that there is some issue on startup that the RabbitMQ queue wont be created until a message is attempted to be sent. 
Therefore the worker cant listen to the queue since it doesent exist yet. But a restart after a message is attempted should make it work.

### Changes
If any code changes has been made to a service, that service needs to be rebuilt with gradle and docker.
Check the `startup.sh` file for the commands to do so.
Then kill the pod and let it restart, that should let it reload the changes.

### Swagger/OpenApi
The `api-service` also launches a OpenApi/Swagger specification. 
Access it by navigation to `http://127.0.0.1:PORTHERE/swagger-ui/index.html` replace PORTHERE with the port.

### RabbitMQ GUI
A RabbitMQ Management GUI is also available. To access it, first portforward the rabbitmq pod.

* Find the rabbitmq pod name: `kubectl get pods`
* Portforward: `kubectl port-forward pod/RABBITMQPODNAME 15672:15672`
* Access on: `http://127.0.0.1:15672`
* Login with: guest/guest