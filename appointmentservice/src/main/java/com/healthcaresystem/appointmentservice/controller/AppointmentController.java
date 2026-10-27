package com.healthcaresystem.appointmentservice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.healthcaresystem.appointmentservice.dto.payloadrequest.AppointmentRequest;
import com.healthcaresystem.appointmentservice.model.Appointment;
import com.healthcaresystem.appointmentservice.service.AppointmentService;

import jakarta.validation.Valid;

/*
 * Controller for managing appointments
 * providing endpoints for booking, updating and retrieving appointments based on criterias
 */
@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {
	
	private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);
	
	@Autowired
	private AppointmentService appointmentService;
	
	/* Books new Appointment */
	@GetMapping("/create")
	public ResponseEntity<String> bookAppointment(@Valid @RequestBody AppointmentRequest appointmentRequest){
		
		logger.info("Booking Appointment: {}", appointmentRequest);
		
		try {
			String result = appointmentService.bookAppointment(appointmentRequest);
			logger.info("Appointment booked successfully {}",result);
			return ResponseEntity.ok("Appointment booked successfully. ID: " + result);
		}catch(Exception e) {
			logger.error("Error booking appointment: {}", e.getMessage(), e);
			return ResponseEntity.internalServerError().body("Unable to book appointment.");
		}
		
	}
	
	/* Retrieves appointments for a specific doctor by doctor ID 
	 * @param : doctorId
	 * @return : response entity containing list of appointments or error message 
	 * */
	@GetMapping("/doctor/{doctorId}")
	public ResponseEntity<?> getAppointmentsByDoctorId(@PathVariable String doctorId) { //response body type is unspecified -> it could return list or string{error}
		
		logger.info("Fetching appointments for doctor ID: {}", doctorId);
		
		try {
			List<Appointment> appointments = appointmentService.getByDoctorId(doctorId);
			logger.info("Appointment fetched successfully for doctor ID: {}", doctorId);
			return ResponseEntity.ok(appointments);
		}catch(Exception e) {
			logger.error("Error fetching appointments for doctor ID {}: {} ", doctorId, e.getMessage(), e); //Logging frameworks such as SLF4J support passing a Throwable as the final argument without requiring a corresponding {} placeholder
			return ResponseEntity.internalServerError().body("Unable to fetch appointments.");
		}
		
	}
	
	/* Retrieves appointments for a specific patient by patient ID 
	 * @param : patientId
	 * @return : response entity containing list of appointments or error message 
	 */
	@GetMapping("/patient/{patientId}")
	public ResponseEntity<?> getAppointmentsByPatientId(@PathVariable String patientId) { //response body type is unspecified -> it could return list or string{error}
		
		logger.info("Fetching appointments for patient ID: {}", patientId);
		
		try {
			List<Appointment> appointments = appointmentService.getByPatientId(patientId);
			logger.info("Appointment fetched successfully for patient ID: {}", patientId);
			return ResponseEntity.ok(appointments);
		}catch(Exception e) {
			logger.error("Error fetching appointments for patient ID {}: {} ", patientId, e.getMessage(), e); //Logging frameworks such as SLF4J support passing a Throwable as the final argument without requiring a corresponding {} placeholder
			return ResponseEntity.internalServerError().body("Unable to fetch appointments.");
		}
		
	}
	
	/* Retrieves all appointments 
	 * @return : response entity containing list of appointments or error message
	 */
	@GetMapping("/all")
	public ResponseEntity<?> getAllAppointments(){
		
		logger.info("Fetching all appointments");
		
		try {
			List<Appointment> appointments = appointmentService.getAllAppointments();
			logger.info("All appointments have fetached successfully.");
			return ResponseEntity.ok(appointments);
		}catch(Exception e){
			logger.error("Error fetching all appointments: {}", e.getMessage());
			return ResponseEntity.internalServerError().body("Unable to fetch appointments.");
		}
		
	}
	
	/* Update existing appointment 
	 * @param : appointmentRequest the updated appointment details
	 * @return : response entity with updated appointment's ID 
	 */
	@PutMapping
	public ResponseEntity<String> updateAppointment(@Valid @RequestBody AppointmentRequest appointmentRequest){
		
		logger.info("Updating appointment: {}", appointmentRequest);
		try {
			String appointmentId = appointmentService.updateAppointment(appointmentRequest);
			logger.info("Appointment updated successfully: {}",appointmentId);
			return ResponseEntity.ok("Appointment updated successfully. ID: "+ appointmentId);
		}catch(Exception e) {
			logger.error("Error updating appointment: {}", e.getMessage());
			return ResponseEntity.internalServerError().body("Unable to update appointment.");
		}
		
	}
	
}
