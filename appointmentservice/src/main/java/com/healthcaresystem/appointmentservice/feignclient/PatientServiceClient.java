package com.healthcaresystem.appointmentservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.healthcaresystem.appointmentservice.dto.payloadresponse.GenericResponse;
import com.healthcaresystem.appointmentservice.model.Patient;

/* Feign client interface : can call rest apis of this pateint servic einto current service class */

/* need to mention client service name mentioned in respective service yml file and url mentioned in current service's yml for calling right client */
@FeignClient(name = "patient-service", url = "${patient.service.url}")
public interface PatientServiceClient {
	
	/* Rest end point and method signature must match with actual cobtroller class method */
	@GetMapping("/api/v1/patient/{id}")
	ResponseEntity<GenericResponse<Patient>> getPatientById(@PathVariable String id);

}
