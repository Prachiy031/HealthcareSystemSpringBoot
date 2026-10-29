package com.healthcareystem.doctorservice.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.healthcaresystem.doctorservice.model.Doctor;

/* Repository interface for managing Doctor Entity */

@Repository
public interface DoctorServiceRepository extends MongoRepository<Doctor, UUID>{
	
	/* Used derived query method */
	
	/* Retrieving doctor by email */
	Optional<Doctor> findByEmail(String email);
	
}
