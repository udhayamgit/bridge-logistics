package org.jesperancinha.logistics.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import lombok.extern.slf4j.Slf4j;
import org.jesperancinha.logistics.jpa.model.Bridge;
import org.jesperancinha.logistics.jpa.model.BridgeOpeningTime;
import org.jesperancinha.logistics.jpa.model.Carriage;
import org.jesperancinha.logistics.jpa.model.Container;
import org.jesperancinha.logistics.jpa.model.Freight;
import org.jesperancinha.logistics.jpa.model.Product;
import org.jesperancinha.logistics.jpa.model.Train;
import org.jesperancinha.logistics.jpa.model.Vehicle;
import org.jesperancinha.logistics.jpa.repositories.BridgeCarriageRepository;
import org.jesperancinha.logistics.jpa.repositories.BridgeContainerRepository;
import org.jesperancinha.logistics.jpa.repositories.BridgeFreightRepository;
import org.jesperancinha.logistics.jpa.repositories.BridgeOpeningTimeRepository;
import org.jesperancinha.logistics.jpa.repositories.BridgeProductRepository;
import org.jesperancinha.logistics.jpa.repositories.BridgeRepository;
import org.jesperancinha.logistics.jpa.repositories.BridgeTrainRepository;
import org.jesperancinha.logistics.jpa.repositories.BridgeVehicleRepository;
import org.jesperancinha.logistics.web.data.FreightDto;
import org.jesperancinha.logistics.web.data.TrainDto;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.reflect.RecordComponent;
import java.sql.Date;
import java.sql.Time;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.stream;

@Component
@Slf4j
@Profile({ "local", "demo", "prod" })
public class BridgeLogisticsInitializer implements CommandLineRunner {

    private final BridgeRepository bridgeRepository;

    private final BridgeCarriageRepository carriageRepository;

    private final BridgeContainerRepository containerRepository;

    private final BridgeFreightRepository freightRepository;

    private final BridgeProductRepository productRepository;

    private final BridgeTrainRepository trainRepository;

    private final BridgeVehicleRepository vehicleRepository;

    private final BridgeOpeningTimeRepository openingTimeRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public BridgeLogisticsInitializer(BridgeRepository bridgeRepository, BridgeCarriageRepository carriageRepository, BridgeContainerRepository containerRepository, BridgeFreightRepository freightRepository, BridgeProductRepository productRepository,
        BridgeTrainRepository trainRepository, BridgeVehicleRepository vehicleRepository, BridgeOpeningTimeRepository openingTimeRepository) {
        this.bridgeRepository = bridgeRepository;
        this.carriageRepository = carriageRepository;
        this.containerRepository = containerRepository;
        this.freightRepository = freightRepository;
        this.productRepository = productRepository;
        this.trainRepository = trainRepository;
        this.vehicleRepository = vehicleRepository;
        this.openingTimeRepository = openingTimeRepository;
        JacksonAnnotationIntrospector implicitRecordAI = new JacksonAnnotationIntrospector() {
            @Override
            public String findImplicitPropertyName(AnnotatedMember m) {
                if (m.getDeclaringClass()
                    .isRecord()) {
                    if (m instanceof AnnotatedParameter parameter) {
                        return m.getDeclaringClass()
                            .getRecordComponents()[parameter.getIndex()].getName();
                    }
                    if (m instanceof AnnotatedMember member) {
                        for (RecordComponent recordComponent : m.getDeclaringClass()
                            .getRecordComponents()) {
                            if (recordComponent.getName()
                                .equals(member.getName())) {
                                return member.getName();
                            }
                        }
                    }
                }
                return super.findImplicitPropertyName(m);
            }
        };
        objectMapper.setAnnotationIntrospector(implicitRecordAI);
    }

    @Override
    public void run(String... args) throws Exception {
        stream(objectMapper.readValue(getClass().getResourceAsStream("/bridges.json"), Bridge[].class)).forEach(bridgeRepository::save);

        stream(objectMapper.readValue(getClass().getResourceAsStream("/carriages.json"), Carriage[].class)).forEach(carriageRepository::save);

        stream(objectMapper.readValue(getClass().getResourceAsStream("/containers.json"), Container[].class)).forEach(containerRepository::save);

        stream(objectMapper.readValue(getClass().getResourceAsStream("/products.json"), Product[].class)).forEach(productRepository::save);

        stream(objectMapper.readValue(getClass().getResourceAsStream("/vehicles.json"), Vehicle[].class)).forEach(vehicleRepository::save);

        stream(objectMapper.readValue(getClass().getResourceAsStream("/freight.json"), FreightDto[].class)).map(freightDto -> Freight.builder()
            .id(freightDto.id())
            .name(freightDto.name())
            .type(freightDto.type())
            .containers(freightDto.composition()
                .stream()
                .map(containerFullDto -> containerRepository.findById(containerFullDto.containerId())
                    .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList()))
            .build())
            .forEach(freightRepository::save);

        stream(objectMapper.readValue(getClass().getResourceAsStream("/train.json"), TrainDto[].class)).map(trainDto -> Train.builder()
            .id(trainDto.id())
            .name(trainDto.name())
            .type(trainDto.type())
            .carriages(trainDto.composition()
                .stream()
                .map(carriageFullDto -> carriageRepository.findById(carriageFullDto.carriageId())
                    .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList()))
            .build())
            .forEach(trainRepository::save);

        final Instant now = Instant.now();
        final long millisToAdd = 10000;
        final Bridge bridge = bridgeRepository.findById(1L)
            .orElse(Bridge.builder()
                .id(1L)
                .build());
        IntStream.range(0, 50)
            .boxed()
            .map(integer -> BridgeOpeningTime.builder()
                .id((long) integer)
                .bridge(bridge)
                .openingTime(now.plusMillis(millisToAdd * integer).toEpochMilli())
                .closingTime(now.plusMillis(millisToAdd * (integer + 1)).toEpochMilli())
                .build())
            .forEach(openingTimeRepository::save);
    }

}