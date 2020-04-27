package org.jesperancinha.logistics.jpa.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "vehicles_log")
public class VehicleLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "vehicle_id",
        referencedColumnName = "id",
        nullable = false,
        updatable = false)
    private Vehicle vehicle;
    private Long lat;
    private Long lon;
    private Long timestamp;
    private String checkInOut;
}