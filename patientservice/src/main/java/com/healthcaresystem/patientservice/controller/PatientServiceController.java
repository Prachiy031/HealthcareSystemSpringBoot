package com.healthcaresystem.patientservice.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.healthcaresystem.patientservice.dto.payloadrequest.PatientRequest;
import com.healthcaresystem.patientservice.dto.payloadresponse.GenericResponse;
import com.healthcaresystem.patientservice.exception.ResourceNotFoundException;
import com.healthcaresystem.patientservice.model.Patient;
import com.healthcaresystem.patientservice.service.PatientService;

import jakarta.validation.Valid;

/* Controller class for handling rest apis - web logic */

@RestController
@RequestMapping("/api/v1/patient")
public class PatientServiceController {

	/* For Logging */
	private static final Logger logger = LoggerFactory.getLogger(PatientServiceController.class);
	
	@Autowired
	private PatientService patientService;
	/* Retrieve all patients 
	 * @return ResponseEntity of Generic response containing patient entity list
	 */
	@GetMapping("/all")
	public ResponseEntity<GenericResponse<List<Patient>>> getAllPatients(){
		
		try {
			List<Patient> patientList = patientService.getAllPatients();
			logger.info("Fetched all patients successfully");
			return ResponseEntity.ok().body(new GenericResponse<>("Fetched all patients successfully", patientList));
		}catch(Exception e) {
			logger.error("Error fetching all patient data {}", e.getMessage());
			return ResponseEntity.internalServerError().body(new GenericResponse<>("Unable to fetch all patients successfully", null));
		}
		
	}
	
	/* Retrieve patient using id 
	 * @param String patient Id  
	 * @return response entity involving generic response of patient entity type in it
	 */
	@GetMapping("/{id}")
	public ResponseEntity<GenericResponse<Patient>> getPatientById(@PathVariable String id){
		
		try {
			Patient patient = patientService.getPatientById(id);
			logger.info("Fetched patient successfully with Id {}", id);
			return ResponseEntity.ok().body(new GenericResponse<>("Fetched patient successfully", patient));
		}catch(ResourceNotFoundException e) {           // if resource is not found
			logger.error("Patient not found with id: {}", id);
			return ResponseEntity.status(404).body(new GenericResponse<>("Unable to fetch patient details with id: "+id, null));
		}catch(Exception e){                  //other exception
			logger.error("Error fetching patient");
			return ResponseEntity.internalServerError().body(new GenericResponse<>("Unable to fetch patient's details", null));
		}
		
	}
	
	/* Retrieve patient using email 
	 * @param String email 
	 * @return Response entity involving generic response of patient entity type
	 */
	@GetMapping("/email/{email}")
	public ResponseEntity<GenericResponse<Patient>> getPatientByEmail(@PathVariable String email){
		
		try {
			Patient patient = patientService.getPatientByEmail(email);
			logger.info("Fetched patient successfully with email {}", email);
			return ResponseEntity.ok().body(new GenericResponse<>("Fetched patient successfully", patient));
		}catch(ResourceNotFoundException e) {           // if resource is not found
			logger.error("Patient not found with email: {}", email);
			return ResponseEntity.status(404).body(new GenericResponse<>("Unable to fetch patient details with email: "+ email, null));
		}catch(Exception e){                  //other exception
			logger.error("Error fetching patient");
			return ResponseEntity.internalServerError().body(new GenericResponse<>("Unable to fetch patient's details", null));
		}
		
	}
	
	/* Add patient 
	 * @param Patient Request DTO
	 * @return Response entity involving generic response of patient entity type
	 */
	@PostMapping
	public ResponseEntity<GenericResponse<Patient>> savePatient(@Valid @RequestBody PatientRequest patientRequest){
		
		try {
			Patient newPatient = patientService.addPatient(patientRequest);
			logger.info("Created patient successfully");
			return ResponseEntity.ok().body(new GenericResponse<>("Patient saved successfully", newPatient));
		}catch(Exception e){
			logger.error("Error saving patient: {}", e.getMessage(), e);
			return ResponseEntity.internalServerError().body(new GenericResponse<>("Error saving patient", null));
		}
	}
	
	/* Update patient 
	 * @param String patient id, Patient Request DTO - updated object
	 * @return Response entity involving generic response of patient entity type
	 */
	@PutMapping("/updatepatient/{id}")
	public ResponseEntity<GenericResponse<Patient>> updatePatient(@PathVariable String id, @Valid @RequestBody PatientRequest patientRequest){
		
		try {
			Patient updatedPatient = patientService.updatePatientById(id, patientRequest);
			logger.info("Updated patient successfully");
			return ResponseEntity.ok().body(new GenericResponse<>("Updated patient successfully witn id "+ id, updatedPatient));
		}catch(Exception e){
			logger.error("Error updating patient: {}", e.getMessage(), e);
			return ResponseEntity.internalServerError().body(new GenericResponse<>("Unable to update patient",null));
		}
		
	}
	
	/* Delete patient 
	 * @param string patient id
	 */
	@DeleteMapping("/deletepatient/{id}")
	public ResponseEntity<GenericResponse<String>> deletePatient(@PathVariable String id){
		
		try {
			 patientService.deletePatient(id);
			 logger.info("Deleted patient details successfully using Id: {}", id);
			 return ResponseEntity.ok().body(new GenericResponse<>("Deleted patient successfully", null));
		}catch(Exception e){
			logger.error("Error deleting patient details");
			return ResponseEntity.internalServerError().body(new GenericResponse<>("Unable to delete patient", null));
		}
	}
	
}
