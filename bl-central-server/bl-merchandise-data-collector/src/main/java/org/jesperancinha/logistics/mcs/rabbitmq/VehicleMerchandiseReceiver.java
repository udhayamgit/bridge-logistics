package org.jesperancinha.logistics.mcs.rabbitmq;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.jesperancinha.logistics.jpa.model.Company;
import org.jesperancinha.logistics.jpa.model.MerchandiseLog;
import org.jesperancinha.logistics.jpa.model.Product;
import org.jesperancinha.logistics.jpa.model.ProductCargo;
import org.jesperancinha.logistics.jpa.model.TransportPackage;
import org.jesperancinha.logistics.jpa.repositories.CompanyRepository;
import org.jesperancinha.logistics.jpa.repositories.ContainerRepository;
import org.jesperancinha.logistics.jpa.repositories.MerchandiseLogRepository;
import org.jesperancinha.logistics.jpa.repositories.MerchandiseRepository;
import org.jesperancinha.logistics.jpa.repositories.ProductCargoRepository;
import org.jesperancinha.logistics.jpa.repositories.ProductRepository;
import org.jesperancinha.logistics.jpa.repositories.TransportPackageRepository;
import org.jesperancinha.logistics.mcs.converter.MerchandiseLogConverter;
import org.jesperancinha.logistics.mcs.data.VehicleMerchandiseDto;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

import static org.jesperancinha.logistics.jpa.types.Status.DELIVERED;

@Slf4j
@Component
public class VehicleMerchandiseReceiver {

    private final Gson gson;

    private CountDownLatch latch = new CountDownLatch(1);

    private final MerchandiseLogRepository merchandiseLogRepository;

    private final MerchandiseRepository merchandiseRepository;

    private final ProductRepository productRepository;

    private final TransportPackageRepository transportPackageRepository;

    private final CompanyRepository companyRepository;

    private final ProductCargoRepository productCargoRepository;

    private final ContainerRepository containerRepository;

    public VehicleMerchandiseReceiver(Gson gson, MerchandiseLogRepository merchandiseLogRepository, MerchandiseRepository merchandiseRepository, ProductRepository productRepository, TransportPackageRepository transportPackageRepository,
        CompanyRepository companyRepository, ProductCargoRepository productCargoRepository, ContainerRepository containerRepository) {
        this.gson = gson;
        this.merchandiseLogRepository = merchandiseLogRepository;
        this.merchandiseRepository = merchandiseRepository;
        this.productRepository = productRepository;
        this.transportPackageRepository = transportPackageRepository;
        this.companyRepository = companyRepository;
        this.productCargoRepository = productCargoRepository;
        this.containerRepository = containerRepository;
    }

    public void receiveMessage(byte[] message) {
        String messageString = new String(message, Charset.defaultCharset());
        System.out.println("Received <" + messageString + ">");
        try {
            final VehicleMerchandiseDto[] vehicleMerchandiseDtos = gson.fromJson(messageString, VehicleMerchandiseDto[].class);
            Stream.of(vehicleMerchandiseDtos)
                .forEach(vehicleMerchandiseDto -> {
                    final Company supplier = companyRepository.findById(vehicleMerchandiseDto.supplierId())
                        .orElse(null);
                    final Company vendor = companyRepository.findById(vehicleMerchandiseDto.vendorId())
                        .orElse(null);

                    vehicleMerchandiseDto.composition()
                        .parallelStream()
                        .forEach(containerDto -> {
                            final Long packageId = containerDto.packageId();
                            final TransportPackage transportPackage = TransportPackage.builder()
                                .id(packageId)
                                .supplier(supplier)
                                .vendor(vendor)
                                .container(containerRepository.findById(containerDto.containerId())
                                    .orElse(null))
                                .productCargos(new ArrayList<>())
                                .build();
                            final TransportPackage transportPackage1 = transportPackageRepository.save(transportPackage);
                            containerDto.products()
                                .parallelStream()
                                .forEach(productInTransitDto -> {
                                    final Product product = productRepository.findById(productInTransitDto.productId())
                                        .orElse(null);
                                    final ProductCargo productCargo = ProductCargo.builder()
                                        .product(product)
                                        .quantity(productInTransitDto.quantity())
                                        .build();
                                    final ProductCargo productCargoDb = productCargoRepository.save(productCargo);
                                    final MerchandiseLog merchandiseLog = MerchandiseLog.builder()
                                        .supplier(supplier)
                                        .vendor(vendor)
                                        .timestamp(Instant.now()
                                            .toEpochMilli())
                                        .transportPackage(transportPackage1)
                                        .productCargo(productCargoDb)
                                        .status(vehicleMerchandiseDto.status())
                                        .lat(vehicleMerchandiseDto.lat())
                                        .lon(vehicleMerchandiseDto.lon())
                                        .build();
                                    transportPackage1.getProductCargos()
                                        .add(productCargoDb);
                                    merchandiseLogRepository.save(merchandiseLog);
                                    if (merchandiseLog.getStatus() == DELIVERED) {
                                        merchandiseRepository.save(MerchandiseLogConverter.toMerchandise(merchandiseLog));
                                    }
                                });
                            transportPackageRepository.save(transportPackage1);

                        });
                    latch.countDown();
                });
        } catch (Exception e) {
            log.error("Error receiving message!", e);
        }
    }

    public CountDownLatch getLatch() {
        return latch;
    }

}