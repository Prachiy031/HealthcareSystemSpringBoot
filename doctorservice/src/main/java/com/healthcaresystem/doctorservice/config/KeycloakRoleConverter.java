package com.healthcaresystem.doctorservice.config;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/* Converter class for converting roles 
 * issued by keycloak as ADMIN/PATIENT/DOCTOR in the form of 
 * ROLE_ADMIN, ROLE_PATIENT,etc as expected by spring security 
 */

@Component
public class KeycloakRoleConverter
        implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {           /* Returns collection of granted authorities */

        Map<String, Object> realmAccess =                        /* Extracts realm access claim from jwt */
                jwt.getClaim("realm_access");                   /* keycloak stores realm-level roles in realmaccess.roles */

        if (realmAccess == null) {				/* If realm access is missing in jwt */
            return Collections.emptyList();
        }

        Collection<String> roles =             /* Extracts roles from roles key */
                ((Collection<String>) realmAccess.get("roles"));

        if (roles == null) {                  /* If roles are missing */
            return Collections.emptyList();
        }

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))      /* Create authority object for each role using SimpleGrantedAuthority*/
                .collect(Collectors.toList());
        
    }
    
}

/* Granted authority : spring security automatically validates incoming jwt -> extracts user roles -> convert them into granted authority object -> 
 * creates an authentication object -> stores authentication in security context -> makes it accessible through security context holder
 */
 