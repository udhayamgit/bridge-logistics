#!/usr/bin/env bash

docker-compose down

docker stop docker-psql_postgres_1

cd bl-demo-server/bl-core-service

python launch_generate_people.py

cd ../..

cd bl-central-server/bl-sensor-data-collector

mvn clean install -Pdemo -DskipTests

cd ..

cd bl-web-app

mvn clean install -Pdemo -DskipTests

cd ../..

cd bl-bridge-server/bl-bridge-temperature-coap

npm run build

cd ..

cd bl-bridge-humidity-mqtt

npm run build

cd ../..

cd bl-train-server/bl-train-people-mqtt

npm run build

cd ../..

docker-compose up -d --build --remove-orphans
