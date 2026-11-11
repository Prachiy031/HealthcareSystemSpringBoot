package com.healthcaresystem.appointmentservice.feignclient;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.healthcaresystem.appointmentservice.dto.payloadresponse.GenericResponse;
import com.healthcaresystem.appointmentservice.model.Doctor;

/* Feign Client interface : works as a HTTP client for another service, we can call rest APIS of this client interface' class into other service */

@FeignClient(name = "doctor-service", url = "${doctor.service.url}")
public interface DoctorServiceClient {
	
	/* These methods should match the actual controller's methods and method signature */
	@GetMapping("/api/v1/doctor/{id}")
	ResponseEntity<GenericResponse<Doctor>> getDoctorById(@PathVariable String id);
}
