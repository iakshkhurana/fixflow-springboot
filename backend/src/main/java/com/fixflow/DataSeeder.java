package com.fixflow;

import com.fixflow.model.Building;
import com.fixflow.model.MaintenanceRequest;
import com.fixflow.model.User;
import com.fixflow.repository.BuildingRepository;
import com.fixflow.repository.MaintenanceRequestRepository;
import com.fixflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds the database with default buildings and users on first startup.
 * Uses count check to avoid duplicating data on restarts.
 *
 * Default credentials (change in production!):
 *   admin@fixflow.com  / admin123
 *   staff@fixflow.com  / staff123
 *   tenant@fixflow.com / tenant123
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final BuildingRepository buildingRepo;
    private final UserRepository userRepo;
    private final MaintenanceRequestRepository requestRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Only seed if the database is empty
        if (buildingRepo.count() > 0) {
            log.info("Database already seeded — skipping.");
            return;
        }

        log.info("Seeding database with default data...");

        // --- Buildings ---
        Building sunrise = buildingRepo.save(Building.builder()
                .name("Sunrise Apartments")
                .address("123 Main Street, Building A")
                .totalFloors(10)
                .build());

        Building oakwood = buildingRepo.save(Building.builder()
                .name("Oakwood Residency")
                .address("456 Oak Avenue, Block C")
                .totalFloors(8)
                .build());

        Building greenview = buildingRepo.save(Building.builder()
                .name("Greenview Complex")
                .address("789 Park Road, Tower 2")
                .totalFloors(15)
                .build());

        // --- Users ---
        User admin = userRepo.save(User.builder()
                .email("admin@fixflow.com")
                .passwordHash(passwordEncoder.encode("admin123"))
                .fullName("Admin User")
                .role("ADMIN")
                .build());

        User staff = userRepo.save(User.builder()
                .email("staff@fixflow.com")
                .passwordHash(passwordEncoder.encode("staff123"))
                .fullName("Staff Member")
                .role("STAFF")
                .build());

        User tenant = userRepo.save(User.builder()
                .email("tenant@fixflow.com")
                .passwordHash(passwordEncoder.encode("tenant123"))
                .fullName("John Tenant")
                .role("TENANT")
                .building(sunrise)
                .build());

        // --- Sample maintenance requests ---
        requestRepo.save(MaintenanceRequest.builder()
                .title("Broken AC in Room 305")
                .description("The air conditioning unit stopped working completely. Room is very hot.")
                .category("HVAC")
                .priority("HIGH")
                .status("OPEN")
                .floorNumber(3)
                .roomNumber("305")
                .building(sunrise)
                .createdBy(tenant)
                .build());

        requestRepo.save(MaintenanceRequest.builder()
                .title("Water Leak in Bathroom")
                .description("There is a slow drip under the bathroom sink. Started 2 days ago.")
                .category("PLUMBING")
                .priority("MEDIUM")
                .status("ASSIGNED")
                .floorNumber(3)
                .roomNumber("305")
                .building(sunrise)
                .createdBy(tenant)
                .assignedTo(staff)
                .build());

        requestRepo.save(MaintenanceRequest.builder()
                .title("Lobby Light Flickering")
                .description("The main lobby ceiling light has been flickering for a week.")
                .category("ELECTRICAL")
                .priority("LOW")
                .status("IN_PROGRESS")
                .floorNumber(1)
                .building(sunrise)
                .createdBy(tenant)
                .assignedTo(staff)
                .build());

        log.info("Database seeded successfully.");
        log.info("Default logins: admin@fixflow.com/admin123, staff@fixflow.com/staff123, tenant@fixflow.com/tenant123");
    }
}
