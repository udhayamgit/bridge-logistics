package org.jesperancinha.logistics.jpa.repositories;

import org.jesperancinha.logistics.jpa.model.Merchandise;
import org.springframework.data.repository.CrudRepository;

public interface BridgeVehicleRepository extends CrudRepository<Merchandise, Long> {
}
