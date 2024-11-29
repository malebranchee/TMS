#!/bin/bash

git pull

sudo mvn clean package -DskipTests

export username=$1
export password=$2

docker compose up -d --build