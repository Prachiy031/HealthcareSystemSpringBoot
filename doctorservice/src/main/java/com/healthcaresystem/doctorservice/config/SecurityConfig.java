
package com.healthcaresystem.doctorservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(
            KeycloakRoleConverter roleConverter) {

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(roleConverter);

        return converter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth

                .requestMatchers("/actuator/health").permitAll()

                // Fetch doctor by ID
                .requestMatchers("/api/v1/doctor/{id}")
                    .hasAnyRole("PATIENT", "DOCTOR", "ADMIN")

                // Fetch doctor by email
                .requestMatchers("/api/v1/doctor/email/**")
                    .hasRole("ADMIN")

                // Fetch all doctors
                .requestMatchers("/api/v1/doctor/all")
                    .hasAnyRole("PATIENT", "ADMIN")

                // Add doctor
                .requestMatchers("/api/v1/doctor")
                    .hasRole("ADMIN")

                // Update doctor
                .requestMatchers("/api/v1/doctor/updatedoctor/**")
                    .hasAnyRole("DOCTOR", "ADMIN")

                // Delete doctor
                .requestMatchers("/api/v1/doctor/deletedoctor/**")
                    .hasRole("ADMIN")

                .anyRequest().authenticated()
            )

            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt ->
                    jwt.jwtAuthenticationConverter(
                        jwtAuthenticationConverter)
                )
            );

        return http.build();
    }
}
