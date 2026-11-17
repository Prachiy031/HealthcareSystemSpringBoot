package com.healthcaresystem.apigateway.config;

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

	/* Convert roles accepted/understood by spring security {ROLE_ADMIN} from roles in token issued by keycloak {ADMIN} 
	 * JwtAuthenticationConverter : inbuilt class for converting authenticated JWT into spring security JWTauthentication token
	 * setJwtGrantedAuthoritiesConverter : inbuilt method available which accepts a converter responsible for extracting authorities from the JWT
	 */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(
            KeycloakRoleConverter roleConverter) {

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(roleConverter);

        return converter;
    }
    
    /* Interface provided by spring security filter
	 * - Represents a chain of security filters through which incoming HTTP requests pass
	 */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            // Stateless authentication
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(auth -> auth

                // Public health endpoint
                .requestMatchers("/actuator/health/**").permitAll()

                //  DOCTOR SERVICE 

                // Fetch all doctors
                .requestMatchers("/api/v1/doctor/all")
                    .hasAnyRole("PATIENT", "ADMIN")

                // Fetch doctor by email
                .requestMatchers("/api/v1/doctor/email/**")
                    .hasRole("ADMIN")

                // Add doctor
                .requestMatchers("/api/v1/doctor")
                    .hasRole("ADMIN")

                // Update doctor
                .requestMatchers("/api/v1/doctor/updatedoctor/**")
                    .hasAnyRole("DOCTOR", "ADMIN")

                // Delete doctor
                .requestMatchers("/api/v1/doctor/deletedoctor/**")
                    .hasRole("ADMIN")

                // Fetch doctor by ID
                .requestMatchers("/api/v1/doctor/{id}")
                    .hasAnyRole("PATIENT", "DOCTOR", "ADMIN")


                //  PATIENT SERVICE 

                // Retrieve all patients
                .requestMatchers("/api/v1/patient/all")
                    .hasRole("ADMIN")

                // Retrieve patient using email
                .requestMatchers("/api/v1/patient/email/**")
                    .hasAnyRole("DOCTOR", "ADMIN")

                // Register patient
                .requestMatchers("/api/v1/patient")
                    .permitAll()

                // Update patient
                .requestMatchers("/api/v1/patient/updatepatient/**")
                    .hasAnyRole("PATIENT", "ADMIN")

                // Delete patient
                .requestMatchers("/api/v1/patient/deletepatient/**")
                    .hasRole("ADMIN")

                // Retrieve patient using ID
                .requestMatchers("/api/v1/patient/{id}")
                    .hasAnyRole("PATIENT", "DOCTOR", "ADMIN")


                // APPOINTMENT SERVICE 

                // Book appointment
                .requestMatchers("/api/v1/appointments/create")
                    .hasAnyRole("PATIENT", "ADMIN")

                // Doctor's appointments
                .requestMatchers("/api/v1/appointments/doctor/**")
                    .hasAnyRole("DOCTOR", "ADMIN")

                // Patient's appointments
                .requestMatchers("/api/v1/appointments/patient/**")
                    .hasAnyRole("PATIENT", "ADMIN")

                // Retrieve all appointments
                .requestMatchers("/api/v1/appointments/all")
                    .hasRole("ADMIN")

                // Update appointment
                .requestMatchers("/api/v1/appointments/updateappointment/**")
                    .hasAnyRole("DOCTOR", "ADMIN")

                // All remaining endpoints require authentication
                .anyRequest().authenticated()
            )

            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter( jwtAuthenticationConverter)
                )
            );

        return http.build();
    }
}
