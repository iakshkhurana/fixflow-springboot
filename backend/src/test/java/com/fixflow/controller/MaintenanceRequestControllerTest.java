package com.fixflow.controller;

import com.fixflow.config.JwtUtil;
import com.fixflow.model.Building;
import com.fixflow.model.MaintenanceRequest;
import com.fixflow.model.User;
import com.fixflow.repository.BuildingRepository;
import com.fixflow.repository.MaintenanceRequestRepository;
import com.fixflow.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the maintenance request API.
 * Uses a real Spring context with H2 in-memory database.
 * Each test is rolled back via @Transactional to keep tests isolated.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MaintenanceRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BuildingRepository buildingRepo;

    @Autowired
    private MaintenanceRequestRepository requestRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String staffToken;
    private String tenantToken;
    private User staffUser;
    private User tenantUser;
    private Building building;

    @BeforeEach
    void setUp() {
        // Create a test building
        building = buildingRepo.save(Building.builder()
                .name("Integration Test Building")
                .address("1 Test Ave")
                .totalFloors(5)
                .build());

        // Create staff user and generate their JWT
        staffUser = userRepo.save(User.builder()
                .email("staff.test@fixflow.com")
                .passwordHash(passwordEncoder.encode("password"))
                .fullName("Staff Tester")
                .role("STAFF")
                .build());
        staffToken = jwtUtil.generateToken(staffUser.getEmail(), staffUser.getRole(), staffUser.getId());

        // Create tenant user and generate their JWT
        tenantUser = userRepo.save(User.builder()
                .email("tenant.test@fixflow.com")
                .passwordHash(passwordEncoder.encode("password"))
                .fullName("Tenant Tester")
                .role("TENANT")
                .building(building)
                .build());
        tenantToken = jwtUtil.generateToken(tenantUser.getEmail(), tenantUser.getRole(), tenantUser.getId());
    }

    @Test
    void shouldCreateRequestAndReturn201() throws Exception {
        String body = """
                {
                    "title": "Broken AC Unit Test",
                    "description": "AC stopped working in test room",
                    "category": "HVAC",
                    "priority": "HIGH",
                    "buildingId": %d,
                    "floorNumber": 3,
                    "roomNumber": "301"
                }
                """.formatted(building.getId());

        mockMvc.perform(post("/api/requests")
                        .header("Authorization", "Bearer " + tenantToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Broken AC Unit Test"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.category").value("HVAC"));
    }

    @Test
    void shouldGetAllRequestsForStaff() throws Exception {
        // Seed a request directly via repo
        requestRepo.save(MaintenanceRequest.builder()
                .title("Test Request For Staff View")
                .description("desc")
                .category("PLUMBING")
                .priority("LOW")
                .status("OPEN")
                .building(building)
                .createdBy(tenantUser)
                .build());

        mockMvc.perform(get("/api/requests")
                        .header("Authorization", "Bearer " + staffToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldGetRequestById() throws Exception {
        MaintenanceRequest saved = requestRepo.save(MaintenanceRequest.builder()
                .title("Specific Request")
                .description("For detail view test")
                .category("ELECTRICAL")
                .priority("MEDIUM")
                .status("OPEN")
                .building(building)
                .createdBy(tenantUser)
                .build());

        mockMvc.perform(get("/api/requests/" + saved.getId())
                        .header("Authorization", "Bearer " + staffToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.title").value("Specific Request"));
    }

    @Test
    void shouldUpdateStatus() throws Exception {
        MaintenanceRequest saved = requestRepo.save(MaintenanceRequest.builder()
                .title("Status Update Test")
                .description("desc")
                .category("GENERAL")
                .priority("LOW")
                .status("OPEN")
                .building(building)
                .createdBy(tenantUser)
                .build());

        mockMvc.perform(patch("/api/requests/" + saved.getId() + "/status")
                        .header("Authorization", "Bearer " + staffToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"ASSIGNED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ASSIGNED"));
    }

    @Test
    void shouldReturn404ForNonExistentRequest() throws Exception {
        mockMvc.perform(get("/api/requests/999999")
                        .header("Authorization", "Bearer " + staffToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFilterRequestsByStatus() throws Exception {
        requestRepo.save(MaintenanceRequest.builder()
                .title("Open Request")
                .description("desc")
                .category("HVAC")
                .priority("HIGH")
                .status("OPEN")
                .building(building)
                .createdBy(tenantUser)
                .build());

        requestRepo.save(MaintenanceRequest.builder()
                .title("Resolved Request")
                .description("desc")
                .category("PLUMBING")
                .priority("LOW")
                .status("RESOLVED")
                .building(building)
                .createdBy(tenantUser)
                .build());

        mockMvc.perform(get("/api/requests?status=OPEN")
                        .header("Authorization", "Bearer " + staffToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.status == 'RESOLVED')]").doesNotExist());
    }
}
