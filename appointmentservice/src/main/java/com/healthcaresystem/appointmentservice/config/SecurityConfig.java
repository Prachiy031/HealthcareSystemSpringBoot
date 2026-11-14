package com.healthcaresystem.appointmentservice.config;

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
            KeycloakRoleConverter roleConverter)  {

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(roleConverter);

        return converter;
    }
	
	
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            		)
            .authorizeHttpRequests(auth -> auth

            	    // Public endpoint
            	    .requestMatchers("/actuator/health").permitAll()

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
            oauth2.jwt(jwt ->
                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)
            )
        );

        return http.build();
    }
    
}
