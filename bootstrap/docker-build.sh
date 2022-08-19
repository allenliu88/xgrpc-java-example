#!/bin/bash

cd ~/Customize/Share/Git/00-opensource/8-blog/opentracing-microservices-example/bootstrap

docker image build --platform linux/amd64 -t allen88/animal-name-service:0.0.1 -f ./Dockerfile-Animal .
docker image build --platform linux/amd64 -t allen88/animal-name-service-error:0.0.1 -f ./Dockerfile-Animal .
docker image build --platform linux/amd64 -t allen88/scientist-name-service:0.0.1 -f ./Dockerfile-Scientist .
docker image build --platform linux/amd64 -t allen88/name-generator-service:0.0.1 -f ./Dockerfile-Name .
docker image build --platform linux/amd64 -t allen88/opentelemetry-agent:0.0.1 -f ./Dockerfile-TelemetryAgent .
docker image build --platform linux/amd64 -t allen88/otel-skywalking-collector:0.0.1 -f ./Dockerfile-OTELSkywalkingCollector .

docker push allen88/animal-name-service:0.0.1
docker push allen88/animal-name-service-error:0.0.1
docker push allen88/scientist-name-service:0.0.1
docker push allen88/name-generator-service:0.0.1
docker push allen88/opentelemetry-agent:0.0.1
docker push allen88/otel-skywalking-collector:0.0.1