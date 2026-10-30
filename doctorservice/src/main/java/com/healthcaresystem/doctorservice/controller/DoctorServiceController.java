package com.healthcaresystem.doctorservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.healthcaresystem.doctorservice.dto.payloadreponse.GenericResponse;
import com.healthcaresystem.doctorservice.model.Doctor;
import com.healthcaresystem.doctorservice.service.DoctorService;

/* Controller for handling rest endpoints */

@RestController
@RequestMapping("/api/v1/doctor")
public class DoctorServiceController {
	/* For Logging */
	private static final Logger logger = LoggerFactory.getLogger(DoctorService.class);
	
	@Autowired
	private DoctorService doctorService;
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getDoctorById(@PathVariable String id){
		
		try {
			
			Doctor doctor = doctorService.getDoctorById(id);
			return ResponseEntity.ok(new GenericResponse<>("Doctor fetched successfully", doctor));
			
		}catch(Exception e) {
			logger.error("Error fetching doctor with Id: {} {}", id, e.getMessage());
			
			String errorMessage = "Error fetching doctor's details";
			
			if(e.getMessage()!=null && !e.getMessage().isEmpty()) {
				errorMessage += ": " + e.getMessage();
			}
			
			return ResponseEntity.internalServerError().body(new GenericResponse<>(errorMessage));
		}
	} 
}
