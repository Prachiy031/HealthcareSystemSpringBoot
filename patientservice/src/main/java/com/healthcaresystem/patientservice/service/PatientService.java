package com.healthcaresystem.patientservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.healthcaresystem.patientservice.dto.payloadrequest.PatientRequest;
import com.healthcaresystem.patientservice.model.Patient;
import com.healthcaresystem.patientservice.repository.PatientRepository;

/* Service class for business logic */
@Service
public class PatientService {
	
	/* for logging */
	private static final Logger logger = LoggerFactory.getLogger(PatientService.class);
	
	@Autowired
	private PatientRepository patientRepository;
	
	/* 
	 * Retrieve all patients 
	 * @return list of patient entity
	 */
	public List<Patient> getAllPatients(){
		
		List<Patient> patientList = patientRepository.findAll();
		logger.info("Number of patients fetched: "+ patientList.size());
		return new ArrayList<>(patientList);
		
	}
	
	/*
	 * Retrieve patient using patient Id 
	 * @Param string id
	 * @return patient entity
	 */
	public Patient getPatientById(String id) throws ResourceNotFoundException{
		
		/* Convert string into UUID */
		UUID uuid = UUID.fromString(id);
		
		Optional<Patient> patient = patientRepository.findById(uuid);
		
		if(patient.isEmpty()) {
			logger.error("Patient not found with Id: {}", id);
			throw new ResourceNotFoundException("Patient not found");
		}
		logger.info("Patient fetched successfully with Id: {}", id);
		return patient.get();
		
	}
	
	/*
	 * Retrieve patient using patient's email
	 * @param email
	 * @return Patient entity
	 */
	public Patient getPatientByEmail(String email) throws ResourceNotFoundException{
				
		Optional<Patient> patient = patientRepository.findByEmail(email);
		
		if(patient.isEmpty()) {
			logger.error("Patient not found with email: {}", email);
			throw new ResourceNotFoundException("Patient not found");
		}
		logger.info("Patient fetched successfully with email: {}", email);
		return patient.get();
		
	}
	
	/*
	 * Create new patient
	 * @param patient request DTO with details of new patient
	 * @return Created Patient entity 
	 */
	public Patient addPatient(PatientRequest patientRequest) throws ResourceNotFoundException{
		
		/* Retrieve patient details with email 
		 * Check if patient with same email already exists 
		 * if exists -> can't create new patient account
		 * if doesn't exist -> can create new patient account
		 * */
		Optional<Patient> patient = patientRepository.findByEmail(patientRequest.getEmail());
		
		if(patient.isPresent()) {
			
			logger.error("Failed to add patient : Patient with email {} already exists", patientRequest.getEmail());
			throw new Exception("A patient with this email already exists");
			
		}
		
		/* Create patient if it is new email */
		UUID uuid = UUID.randomUUID();
		Patient newPatient = new Patient(
							uuid,
							patientRequest.getFirstName(),
							patientRequest.getLastName(),
							patientRequest.getEmail(),
							patientRequest.getPhone(),
							patientRequest.getAge()
							);
		
		logger.info("Saved patient with Id: "+ uuid);
		return patientRepository.save(newPatient);
		
	}
	
	/* 
	 * Update patient by Patient Id 
	 * @param String id, PatientRequest DTO object
	 * @return updated patient entity
	 */
	public Patient updatePatientById(String id, PatientRequest patientRequest) throws ResourceNotFoundException{
		
		/* Convert String id to UUID type id */
		UUID uuid = UUID.fromString(id);
		
		/*
		 * Check if patient already exists for the given id
		 * if exists -> can't create new patient account
		 * if doesn't exist -> can create new patient account
		 * */
		Optional<Patient> existingPatient = patientRepository.findById(uuid);
		
		if(existingPatient.isEmpty()) {
			logger.error("Failed to update patient: Patient not found with id: {}", id);
			throw new ResourceNotFoundException("Patient not found with id: " + id);
		}
		
		/* Get existing patient entity */
		Patient patientToUpdate = existingPatient.get();
		
		/* Update fields of existing patient with new values */
		patientToUpdate.setId(uuid);
		patientToUpdate.setFirstName(patientRequest.getFirstName());
		patientToUpdate.setLastName(patientRequest.getLastName());
		patientToUpdate.setEmail(patientRequest.getEmail());
		patientToUpdate.setPhone(patientRequest.getPhone());
		patientToUpdate.setAge(patientRequest.getAge());

		
		logger.info("Updated patient with id {}", id);
		return patientRepository.save(patientToUpdate);
		
	}
	
	/* 
	 * Delete patient by patient ID 
	 * @param string id
	 */
	public void deletePatient(String id) throws ResourceNotFoundException{
		
		/* Convert string to UUID type id */
		UUID uuid = UUID.fromString(id);
		if(!patientRepository.existsById(uuid)) {
			logger.error("falied to delete patient: Patient not found with id: {}", id);
			return ResourceNotFoundException("Patient not found with Id: {}", id);
		}
		
		/* Delete patient with uuid */
		patientRepository.deleteById(uuid);
		logger.info("Patient deleted successfully with Id: {}", id);
		
	}
	
	
	
}
