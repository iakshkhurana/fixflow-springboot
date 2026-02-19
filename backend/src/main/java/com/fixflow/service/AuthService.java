package com.fixflow.service;

import com.fixflow.config.JwtUtil;
import com.fixflow.dto.AuthResponse;
import com.fixflow.dto.LoginRequest;
import com.fixflow.dto.RegisterRequest;
import com.fixflow.exception.ResourceNotFoundException;
import com.fixflow.model.Building;
import com.fixflow.model.User;
import com.fixflow.repository.BuildingRepository;
import com.fixflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Handles user registration and login.
 * Returns a JWT token on success so the frontend can make authenticated requests.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final BuildingRepository buildingRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Validates credentials and returns a JWT if correct.
     * We manually check the password rather than using AuthenticationManager
     * to keep the auth flow simple and avoid extra database lookups.
     */
    public AuthResponse login(LoginRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getId());
        return buildAuthResponse(user, token);
    }

    /**
     * Creates a new user and immediately returns a JWT (auto-login after register).
     * Tenants must provide a buildingId; staff/admin do not.
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("An account with this email already exists");
        }

        // Validate role value
        if (!VALID_ROLES.contains(request.getRole())) {
            throw new RuntimeException("Invalid role. Must be TENANT, STAFF, or ADMIN");
        }

        Building building = null;
        if ("TENANT".equals(request.getRole())) {
            if (request.getBuildingId() == null) {
                throw new RuntimeException("Tenants must select a building");
            }
            building = buildingRepo.findById(request.getBuildingId())
                    .orElseThrow(() -> new ResourceNotFoundException("Building not found"));
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(request.getRole())
                .building(building)
                .build();

        user = userRepo.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getId());
        return buildAuthResponse(user, token);
    }

    private AuthResponse buildAuthResponse(User user, String token) {
        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .buildingId(user.getBuilding() != null ? user.getBuilding().getId() : null)
                .buildingName(user.getBuilding() != null ? user.getBuilding().getName() : null)
                .build();

        return AuthResponse.builder()
                .token(token)
                .user(userInfo)
                .build();
    }

    private static final java.util.List<String> VALID_ROLES = java.util.List.of("TENANT", "STAFF", "ADMIN");
}
