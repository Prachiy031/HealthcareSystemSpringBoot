package com.healthcaresystem.doctorservice.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.healthcaresystem.doctorservice.dto.payloadrequest.DoctorRequest;
import com.healthcaresystem.doctorservice.exception.ResourceNotFoundException;
import com.healthcaresystem.doctorservice.model.Doctor;
import com.healthcaresystem.doctorservice.model.DoctorStatus;
import com.healthcareystem.doctorservice.repository.DoctorServiceRepository;

/* Service class for applying business logic */

@Service
public class DoctorService {
	
	/* For Logging */
	private static final Logger logger = LoggerFactory.getLogger(DoctorService.class);
	
	@Autowired
	private DoctorServiceRepository doctorRepository;
	
	/* Retrieve all doctors in list 
	 * @return List of Doctor entity
	 * */
	public List<Doctor> getAllDoctors(){
		
		List<Doctor> doctorsList = doctorRepository.findAll();
		
		logger.debug("Number of doctors fetched: {}", doctorsList.size());
		return new ArrayList<>(doctorsList);
		
	}
	
	/* Retrieve doctor by Doctor ID 
	 * @param uuid
	 * @return Doctor entity
	 * */
	public Doctor getDoctorById(String id) throws ResourceNotFoundException{
		
		/* Convert String representation of id to UUID type id */
		UUID uuid = UUID.fromString(id);
		
		Optional<Doctor> doctor = doctorRepository.findById(uuid);
		
		if(doctor.isEmpty()) {
			logger.error("Doctor not found with id: {}", id);
			throw new ResourceNotFoundException("Doctor not found");
		}
		
		logger.info("Doctor fetched successfully with id: {}", id);
		return doctor.get();

	}
	
	/* Retrieve doctor by email 
	 * @param email 
	 * @return Doctor entity
	 * */
	public Doctor getDoctorByEmail(String email) throws ResourceNotFoundException{
		
		Optional<Doctor> doctor = doctorRepository.findByEmail(email);

		if(doctor.isEmpty()) {
			logger.error("Doctor not found with email: {}",email);
			throw new ResourceNotFoundException("Doctor not found");
		}
	
		logger.info("Doctor fecthed successfully with email: {}",email);
		return doctor.get();
	}
	
	/* Add Doctor 
	 * @param DoctorRequest DTO object
	 * @return created Doctor entity
	 * */
	public Doctor addDoctor(DoctorRequest doctorRequest) throws Exception{
		
		/* Retrieve doctor details with email 
		 * Check if doctor with same email already exists 
		 * if exists -> can't create new doctor account
		 * if doesn't exist -> can create new doctor account
		 * */
		Optional<Doctor> doctor = doctorRepository.findByEmail(doctorRequest.getEmail());
		
		if(doctor.isPresent()) {
			
			logger.error("Failed to add doctor : Doctor with email {} already exists", doctorRequest.getEmail());
			throw new Exception("A doctor with this email already exists");
			
		}
		
		/* Create doctor if it is new email */
		UUID uuid = UUID.randomUUID();
		Doctor newDoctor = new Doctor(
							uuid,
							doctorRequest.getFirstName(),
							doctorRequest.getLastName(),
							doctorRequest.getEmail(),
							doctorRequest.getPhone(),
							doctorRequest.getSpeciality(),
							doctorRequest.getYearsOfExperience(),
							DoctorStatus.fromJsonToEnum(doctorRequest.getStatus())
							);
		
		logger.info("Saved doctor with id: "+ uuid);
		return doctorRepository.save(newDoctor);
		
	}
	
	/* Update doctor by Doctor ID 
	 * @param id, DoctorRequest DTO object
	 * @return updated Doctor entity
	 * */
	public Doctor updateDoctorById(String id, DoctorRequest doctorRequest) throws ResourceNotFoundException{
		
		/* Convert String representation of id to UUID type id */
		UUID uuid = UUID.fromString(id);
		
		Optional<Doctor> existingDoctor = doctorRepository.findById(uuid);
		
		if(existingDoctor.isEmpty()) {
			logger.error("Failed to update doctor: Doctor not found with id: {}", id);
			throw new ResourceNotFoundException("Doctor not found with id: " + id);
		}
		
		/* Get existing doctor entity */
		Doctor doctorToUpdate = existingDoctor.get();
		
		/* Update fields of existing doctor with new values */
		doctorToUpdate.setId(uuid);
		doctorToUpdate.setFirstName(doctorRequest.getFirstName());
		doctorToUpdate.setLastName(doctorRequest.getLastName());
		doctorToUpdate.setEmail(doctorRequest.getEmail());
		doctorToUpdate.setPhone(doctorRequest.getPhone());
		doctorToUpdate.setSpeciality(doctorRequest.getSpeciality());
		doctorToUpdate.setYearsOfExperience(doctorRequest.getYearsOfExperience());
		doctorToUpdate.setStatus(DoctorStatus.fromJsonToEnum(doctorRequest.getStatus()));
		
		logger.info("Updated doctor with id {}", id);
		return doctorRepository.save(doctorToUpdate);
		
	}
	
	/* Delete doctor by Doctor ID 
	 * We are just soft deleting, user might require this all information in future for report checking 
	 * We are just deactivating the status of doctor, every other data is retained in db
	 * @param UUID uuid
	 * */
	public void deleteDoctor(UUID uuid) throws ResourceNotFoundException{
		
		/* Retrieve doctor to be deleted */
		Optional<Doctor> doctor = doctorRepository.findById(uuid);
		
		if(doctor.isEmpty()) {
			logger.error("Failed to delete doctor: Doctor not found with Id: {}", uuid);
			throw new ResourceNotFoundException("Doctor not found with Id: "+ uuid);
		}
		
		Doctor doctorToBeDeleted = doctor.get();
		
		/* Change status to disabled */
		doctorToBeDeleted.setStatus(DoctorStatus.fromJsonToEnum("DISABLED"));
		doctorRepository.save(doctorToBeDeleted);
		logger.info("Doctor deleted successfully");
		
	}
}
