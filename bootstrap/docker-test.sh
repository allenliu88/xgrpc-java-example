#!/bin/bash
cd ~/Customize/Share/Git/00-opensource/8-blog/xgrpc-java-example/bootstrap
mvn clean install -Dmaven.test.skip=true
curl -v http://127.0.0.1:8080/api/v1/names/random

curl -v http://127.0.0.1:9000/api/v1/animals/push