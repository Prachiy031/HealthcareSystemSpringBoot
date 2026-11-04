package com.healthcaresystem.patientservice.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.healthcaresystem.patientservice.model.Patient;

/* Repository for handling entities */

@Repository
public interface PatientRepository extends MongoRepository<Patient, UUID>{
	
	/* Derived query method involving other than primary key */
	
	/* Search patient by email */
	Optional<Patient> findByEmail(String email);
}
