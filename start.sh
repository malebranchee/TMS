#!/bin/bash

git pull

mvn clean package -DskipTests

export username=$1
export password=$2

docker compose up -d --build