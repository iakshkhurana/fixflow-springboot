package com.fixflow.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a physical building managed by FixFlow.
 * Requests are scoped to buildings; tenants belong to one building.
 */
@Entity
@Table(name = "buildings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String address;

    @Column(name = "total_floors")
    private Integer totalFloors;
}
