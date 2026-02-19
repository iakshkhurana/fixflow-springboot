package com.fixflow.repository;

import com.fixflow.model.Building;
import com.fixflow.model.MaintenanceRequest;
import com.fixflow.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository slice tests — uses @DataJpaTest to load only JPA components.
 * Faster than @SpringBootTest because it skips web layer, security, etc.
 */
@DataJpaTest
class MaintenanceRequestRepositoryTest {

    @Autowired
    private MaintenanceRequestRepository requestRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BuildingRepository buildingRepo;

    private Building building;
    private User tenant;

    @BeforeEach
    void setUp() {
        building = buildingRepo.save(Building.builder()
                .name("Repo Test Building")
                .address("1 Repo St")
                .totalFloors(3)
                .build());

        tenant = userRepo.save(User.builder()
                .email("repo.tenant@test.com")
                .passwordHash("hash")
                .fullName("Repo Tenant")
                .role("TENANT")
                .building(building)
                .build());
    }

    @Test
    void shouldFindRequestsByStatus() {
        requestRepo.save(MaintenanceRequest.builder()
                .title("Open Request")
                .description("desc")
                .category("HVAC")
                .priority("HIGH")
                .status("OPEN")
                .building(building)
                .createdBy(tenant)
                .build());

        requestRepo.save(MaintenanceRequest.builder()
                .title("Resolved Request")
                .description("desc")
                .category("PLUMBING")
                .priority("LOW")
                .status("RESOLVED")
                .building(building)
                .createdBy(tenant)
                .build());

        List<MaintenanceRequest> openRequests = requestRepo.findByStatusOrderByCreatedAtDesc("OPEN");

        assertThat(openRequests).hasSize(1);
        assertThat(openRequests.get(0).getTitle()).isEqualTo("Open Request");
    }

    @Test
    void shouldFindRequestsByCreatedUserId() {
        requestRepo.save(MaintenanceRequest.builder()
                .title("My Request")
                .description("desc")
                .category("GENERAL")
                .priority("MEDIUM")
                .status("OPEN")
                .building(building)
                .createdBy(tenant)
                .build());

        List<MaintenanceRequest> myRequests = requestRepo.findByCreatedByIdOrderByCreatedAtDesc(tenant.getId());

        assertThat(myRequests).hasSize(1);
        assertThat(myRequests.get(0).getCreatedBy().getId()).isEqualTo(tenant.getId());
    }

    @Test
    void shouldCountByStatus() {
        requestRepo.save(MaintenanceRequest.builder()
                .title("Open 1").description("d").category("HVAC").priority("HIGH")
                .status("OPEN").building(building).createdBy(tenant).build());
        requestRepo.save(MaintenanceRequest.builder()
                .title("Open 2").description("d").category("PLUMBING").priority("LOW")
                .status("OPEN").building(building).createdBy(tenant).build());

        Long count = requestRepo.countByStatus("OPEN");

        assertThat(count).isEqualTo(2L);
    }

    @Test
    void shouldReturnCategoryBreakdown() {
        requestRepo.save(MaintenanceRequest.builder()
                .title("HVAC Issue").description("d").category("HVAC").priority("HIGH")
                .status("OPEN").building(building).createdBy(tenant).build());
        requestRepo.save(MaintenanceRequest.builder()
                .title("Plumbing Issue").description("d").category("PLUMBING").priority("LOW")
                .status("OPEN").building(building).createdBy(tenant).build());

        List<Object[]> breakdown = requestRepo.countByCategory();

        assertThat(breakdown).isNotEmpty();
        assertThat(breakdown).hasSize(2);
    }
}
