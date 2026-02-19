package com.fixflow.config;

import com.fixflow.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;

/**
 * Spring Security configuration.
 *
 * Design decisions:
 * - Stateless (no sessions) — JWT carries auth state
 * - CSRF disabled — not needed for stateless REST APIs
 * - Rules are ordered most-specific first (Spring uses first-match wins)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Use our CorsConfigurationSource bean (defined in CorsConfig.java)
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Allow CORS preflight requests from browser (OPTIONS must pass before auth check)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Public: login and register
                .requestMatchers("/api/auth/**").permitAll()

                // Buildings: public GET so register page can populate dropdown without a token
                .requestMatchers(HttpMethod.GET, "/api/buildings/**").permitAll()

                // Tenant: view and submit their own requests
                .requestMatchers(HttpMethod.GET, "/api/requests/my").hasAuthority("TENANT")
                .requestMatchers(HttpMethod.POST, "/api/requests").hasAuthority("TENANT")

                // Comments: any authenticated user (tenants need to see their request thread)
                .requestMatchers("/api/requests/*/comments").authenticated()

                // Specific request detail: any authenticated user (tenant views own, staff views all)
                .requestMatchers(HttpMethod.GET, "/api/requests/*").authenticated()

                // Staff / Admin: see all requests list
                .requestMatchers(HttpMethod.GET, "/api/requests").hasAnyAuthority("STAFF", "ADMIN")

                // Status updates: staff and admin
                .requestMatchers(HttpMethod.PATCH, "/api/requests/*/status").hasAnyAuthority("STAFF", "ADMIN")

                // Assignment: admin only
                .requestMatchers(HttpMethod.PATCH, "/api/requests/*/assign").hasAuthority("ADMIN")

                // Staff listing for admin assignment dropdown
                .requestMatchers(HttpMethod.GET, "/api/users/staff").hasAnyAuthority("ADMIN", "STAFF")

                // Dashboard: staff and admin
                .requestMatchers("/api/dashboard/**").hasAnyAuthority("STAFF", "ADMIN")

                // Everything else requires authentication
                .anyRequest().authenticated()
            )
            // JWT filter runs before Spring's username/password filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Expose AuthenticationManager so AuthService can use it for login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
