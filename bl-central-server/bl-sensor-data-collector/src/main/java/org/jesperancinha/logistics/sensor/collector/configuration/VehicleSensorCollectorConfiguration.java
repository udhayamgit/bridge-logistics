package org.jesperancinha.logistics.sensor.collector.configuration;

import org.jesperancinha.logistics.sensor.collector.rabbitmq.VehicleSensorReceiver;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "bridge.logistics.vehicle.sensor.active",
    matchIfMissing = true)
public class VehicleSensorCollectorConfiguration {

    private static final String BL_VEHICLE_01_SENSOR_EXCHANGE = "bl_vehicle_01_sensor_exchange";

    private static final String BL_VEHICLE_01_SENSOR_QUEUE = "bl_vehicle_01_sensor_queue";

    @Bean(name = "VehicleQueue")
    Queue queue() {
        return new Queue(BL_VEHICLE_01_SENSOR_QUEUE, true);
    }

    @Bean(name = "VehicleExchange")
    FanoutExchange exchange() {
        return new FanoutExchange(BL_VEHICLE_01_SENSOR_EXCHANGE, true, false);
    }

    @Bean(name = "VehicleBinding")
    Binding binding(
        @Qualifier("VehicleQueue")
            Queue queue,
        @Qualifier("VehicleExchange")
            FanoutExchange exchange) {
        return BindingBuilder.bind(queue)
            .to(exchange);
    }

    @Bean(name = "VehicleContainer")
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
        @Qualifier("VehicleListener")
            MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(BL_VEHICLE_01_SENSOR_QUEUE);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean(name = "VehicleListener")
    MessageListenerAdapter listenerAdapter(VehicleSensorReceiver vehicleSensorReceiver) {
        return new MessageListenerAdapter(vehicleSensorReceiver, "receiveMessage");
    }
}
