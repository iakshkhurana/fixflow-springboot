package com.fixflow.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Core entity — a maintenance request submitted by a tenant.
 * Follows the workflow: OPEN → ASSIGNED → IN_PROGRESS → RESOLVED → CLOSED
 */
@Entity
@Table(name = "maintenance_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Category of the issue (PLUMBING, ELECTRICAL, HVAC, etc.)
    @Column(nullable = false)
    private String category;

    // How urgent is this? LOW / MEDIUM / HIGH / CRITICAL
    @Column(nullable = false)
    private String priority;

    // Tracks where this request is in the workflow
    @Column(nullable = false)
    private String status;

    @Column(name = "floor_number")
    private Integer floorNumber;

    @Column(name = "room_number")
    private String roomNumber;

    // Which building the issue is in
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    // Tenant who reported the issue
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    // Staff member currently responsible for this request
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    // Cascade comments deletion when request is deleted
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RequestComment> comments = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Set when a staff member marks this as RESOLVED
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = "OPEN"; // Default status for new requests
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
