#!/usr/bin/env bash
sleep 4
/usr/local/etc/kafka/bin/kafka-server-start.sh /usr/local/etc/kafka/config/server0.properties &
/usr/local/etc/kafka/bin/kafka-server-start.sh /usr/local/etc/kafka/config/server1.properties &
/usr/local/etc/kafka/bin/kafka-server-start.sh /usr/local/etc/kafka/config/server2.properties &
sleep 4
/usr/local/etc/kafka/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 2 --partitions 3 --topic TEMPERATURE
/usr/local/etc/kafka/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 2 --partitions 3 --topic HUMIDITY
#/usr/local/etc/kafka/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 2 --partitions 3 --topic WINDSPEED
#/usr/local/etc/kafka/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 2 --partitions 3 --topic WINDDIRECTION
