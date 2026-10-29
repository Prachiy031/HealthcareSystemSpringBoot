package com.healthcaresystem.doctorservice.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.healthcareystem.doctorservice.repository.DoctorServiceRepository;

/* Service class for applying business logic */

@Service
public class DoctorService {
	
	/* For Logging */
	private static final Logger logger = LoggerFactory.getLogger(DoctorService.class);
	
	@Autowired
	private DoctorServiceRepository doctorRepository;
	
	/* Retrieve all doctors in list */
	
	/* Retrieve doctor by Doctor ID */
	
	/* Retrieve doctor by email */
	
	/* Add Doctor */
	
	/* Update doctor by Doctor ID */
	
	/* Delete doctor by Doctor ID */
}
