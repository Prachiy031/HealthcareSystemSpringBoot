package com.healthcaresystem.patientservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;


/* Securing your Patient service APIs using spring security and Keycloak JWT authentication
 * Keycloak handles user authentication and issues JWT token 
 * Spring Security validates those tokens and applies your authorization rules
 */

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
            .csrf(csrf -> csrf.disable())			//cross site request forgery is disabled
            .sessionManagement(session ->			//Stateless authentication - authenticates requests using credentials supplied with each request
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )
            .authorizeHttpRequests(auth -> auth

                // Public health endpoint
                .requestMatchers("/actuator/health").permitAll()

                // Retrieve all patients 
                .requestMatchers("/api/v1/patient/all")
                    .hasRole("ADMIN")

                // Retrieve patient using email 
                .requestMatchers("/api/v1/patient/email/**")
                    .hasAnyRole("DOCTOR", "ADMIN")

                // Retrieve patient using ID
                .requestMatchers("/api/v1/patient/{id}")
                    .hasAnyRole("PATIENT", "DOCTOR", "ADMIN")

                // Register patient
                .requestMatchers("/api/v1/patient")
                    .permitAll()

                // Update patient - PATIENT or ADMIN
                .requestMatchers("/api/v1/patient/updatepatient/**")
                    .hasAnyRole("PATIENT", "ADMIN")

                // Delete patient - ADMIN only
                .requestMatchers("/api/v1/patient/deletepatient/**")
                    .hasRole("ADMIN")

                // Remaining endpoints require authentication
                .anyRequest().authenticated()
            )

            .oauth2ResourceServer(oauth2 ->		/* oauth2ResourceServer: this application should accept and validate oauth2 access token */
                oauth2.jwt(jwt ->		  /* Configures jwt based authentication for resource server {Application that protects its apis using access tokens e.g. appointemnt, pateint, docytor service }*/
                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)		/* converts jwt issued by keycloak into spring security accepted jwt */
                )
            );

        return http.build();
    }
}
