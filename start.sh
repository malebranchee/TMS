#!/bin/bash

git pull

sudo mvn clean package -DskipTests

#read -p "DATABASE USERNAME: " SPRING_DATASOURCE_USERNAME
#read -p "DATABASE PASSWORD " SPRING_DATASOURCE_PASSWORD
#sudo docker compose stop

export SPRING_DATASOURCE_USERNAME=$1
export SPRING_DATASOURCE_PASSWORD=$2

sudo docker compose up -d --build --force-recreate