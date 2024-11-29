#!/bin/bash

git pull

mvn clean
mvn package

docker-compose stop

read -p "DATABASE USERNAME: " SPRING_DATASOURCE_USERNAME
read -p "DATABASE PASSWORD " SPRING_DATASOURCE_PASSWORD

docker-compose up --build -d