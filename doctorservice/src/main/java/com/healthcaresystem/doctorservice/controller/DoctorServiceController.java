package com.healthcaresystem.doctorservice.controller;

import java.util.List;
import java.util.UUID;

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

import com.healthcaresystem.doctorservice.dto.payloadrequest.DoctorRequest;
import com.healthcaresystem.doctorservice.dto.payloadresponse.GenericResponse;
import com.healthcaresystem.doctorservice.model.Doctor;
import com.healthcaresystem.doctorservice.service.DoctorService;

import jakarta.validation.Valid;

/* Controller for handling rest endpoints */

@RestController
@RequestMapping("/api/v1/doctor")
public class DoctorServiceController {
	/* For Logging */
	private static final Logger logger = LoggerFactory.getLogger(DoctorService.class);
	
	@Autowired
	private DoctorService doctorService;
	
	/* Retrieves doctor data by Id 
	 * @param string id 
	 * @return response entity of generic response of doctor entity type as data
	 */
	@GetMapping("/{id}")
	public ResponseEntity<GenericResponse<Doctor>> getDoctorById(@PathVariable String id){
		
		try {
			Doctor doctor = doctorService.getDoctorById(id);
			logger.info("Doctor data fetched successfully using id {}", id);
			return ResponseEntity.ok(new GenericResponse<>("Doctor fetched successfully", doctor));
		}catch(Exception e) {
			logger.error("Error fetching doctor with Id: {} {}", id, e.getMessage());
			return ResponseEntity.internalServerError().body(new GenericResponse<>("Unable to fetch doctor details", null)); //here null is generic response body
		}
		
	} 
	
	/*Retrieves doctor data by email
	 * @param string email 
	 * @return response entity of generic response of doctor entity type as data
	 */
	@GetMapping("/email/{email}")
	public ResponseEntity<GenericResponse<Doctor>> getDoctorByEmail(@PathVariable String email){
		
		try {
			Doctor doctor = doctorService.getDoctorByEmail(email);
			logger.info("Doctor data fetched successfully using email {}", email);
			return ResponseEntity.ok(new GenericResponse<>("Fetched Doctor data successfully", doctor));
		}catch(Exception e) {
			logger.error("Error fetching doctor with email {} {}", email, e.getMessage());
			return ResponseEntity.internalServerError().body(new GenericResponse<>("Unable to fetch doctor details", null));
		}
		
	}
	
	/*Retrieves all doctors
	 * @return response entity of any type
	 */
	@GetMapping("/all")
	public ResponseEntity<GenericResponse<List<Doctor>>> getAllDoctors(){
		
		try {
			List<Doctor> doctorList = doctorService.getAllDoctors();
			logger.info("Fetched all doctors sucessfully");
			return ResponseEntity.ok().body(new GenericResponse<>("Fetched doctors successfully", doctorList));
		}catch(Exception e) {
			logger.error("Error fetching all doctors: {}", e.getMessage());
			return ResponseEntity.internalServerError().body(new GenericResponse<>("Unable to fetch all doctors sucessfully", null));
		}
		
	}
	
	/* Save doctor added 
	 * @param Doctor request dto
	 * @return response entity of created doctor entity as a data
	 */
	@PostMapping
	public ResponseEntity<GenericResponse<Doctor>> saveDoctor(@Valid @RequestBody DoctorRequest doctorRequest){
		
		try {
			Doctor createdDoctor = doctorService.addDoctor(doctorRequest);
			logger.info("Saved doctor sucsessfully");
			return ResponseEntity.ok().body(new GenericResponse<>("Saved doctor successfully", createdDoctor));
		}catch(Exception e) {
			logger.error("Error saving doctor's details: ", e.getMessage());
			return ResponseEntity.internalServerError().body(new GenericResponse<>("Unable to save doctor details", null));
		}
	}
	
	/* Update doctor details and save it
	 * @param Updated Doctor request dto
	 * @return response entity of updated doctor entity as a data
	 */
	@PutMapping("/updatedoctor/{id}")
	public ResponseEntity<GenericResponse<Doctor>> updateDoctor(@Valid @RequestBody DoctorRequest doctorRequest, @PathVariable String id ){
		
		try {
			Doctor updatedDoctor = doctorService.updateDoctorById(id, doctorRequest);
			logger.info("Updated doctor sucsessfully");
			return ResponseEntity.ok().body(new GenericResponse<>("Updated doctor successfully of id "+ id, updatedDoctor));
		}catch(Exception e) {
			logger.error("Error updating doctor's details: {}", e.getMessage());
			return ResponseEntity.internalServerError().body(new GenericResponse<>("Unable to update doctor details", null));
		}
	}
	
	/* Delete doctor details using id
	 * @param String id
	 * @return Response entity of generic response of string type {message}
	 */
	@DeleteMapping("/deletedoctor/{id}")
	public ResponseEntity<GenericResponse<String>> deleteDoctor(@Valid @PathVariable String id){
		
		try {
			UUID uuid = UUID.fromString(id);
			doctorService.deleteDoctor(uuid);
			logger.info("Deleted doctor details successfully using Id: {}", id);
			return ResponseEntity.ok().body((new GenericResponse<>("Deleted doctor details sucessfully",null)));
		}catch(Exception e) {
			logger.error("Error deleting doctor's details");
			return ResponseEntity.internalServerError().body(new GenericResponse<>("Unable to delete doctor's details",null));
		}
		
	}
	
	
}
