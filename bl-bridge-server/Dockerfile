FROM jesperancinha/je-streams:0.0.1

WORKDIR /root

ENV LANG=C.UTF-8

RUN rabbitmq-plugins enable --offline rabbitmq_management

RUN rabbitmq-plugins enable rabbitmq_federation

RUN rabbitmq-plugins enable rabbitmq_federation_management

RUN rabbitmq-plugins enable rabbitmq_shovel rabbitmq_shovel_management

COPY bl-bridge-temperature-coap/dist /root/bl-bridge-temperature-coap/dist

COPY bl-bridge-temperature-coap/node_modules /root/bl-bridge-temperature-coap/node_modules

COPY bl-bridge-humidity-mqtt/dist /root/bl-bridge-humidity-mqtt/dist

COPY bl-bridge-humidity-mqtt/node_modules /root/bl-bridge-humidity-mqtt/node_modules

COPY entrypoint.sh /root

RUN rm /usr/local/etc/kafka/config/server.properties

COPY kafka/*.* /usr/local/etc/kafka/config/

COPY zookeeper/*.* /usr/local/etc/kafka/config/

COPY startKafka.sh /root

COPY startZookeeper.sh /root

ENTRYPOINT ["/root/entrypoint.sh"]

#RabbitMQ
EXPOSE 5674 15674

#CoAP
EXPOSE 5683

#Mosquittto
EXPOSE 1883

#Kafka
EXPOSE 9092 9093 9094