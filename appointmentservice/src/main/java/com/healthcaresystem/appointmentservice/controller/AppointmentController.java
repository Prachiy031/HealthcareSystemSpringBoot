package com.healthcaresystem.appointmentservice.controller;

import java.util.List;
import java.util.UUID;

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
import com.healthcaresystem.appointmentservice.dto.payloadresponse.GenericResponse;
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
	
	/* Books new Appointment 
	 * @param : Appointrequest request dto
	 * @return : Response entity of general response of string type
	 */
	@GetMapping("/create")
	public ResponseEntity<GenericResponse<String>> bookAppointment(@Valid @RequestBody AppointmentRequest appointmentRequest){
		
		logger.info("Booking Appointment: {}", appointmentRequest);
		
		try {
			String result = appointmentService.bookAppointment(appointmentRequest);
			logger.info("Appointment booked successfully {}",result);
			return ResponseEntity.ok().body(new GenericResponse<>("Appointment booked successfully of Id: "+ result, null));
		}catch(Exception e) {
			logger.error("Error booking appointment: {}", e.getMessage(), e);
			return ResponseEntity.internalServerError().body(new GenericResponse<>("Unable to book appointment", null));
		}
		
	}
	
	/* Retrieves appointments for a specific doctor by doctor Id 
	 * @param : doctorId
	 * @return : response entity containing generic response of list of appointments or null for error
	 * */
	@GetMapping("/doctor/{doctorId}")
	public ResponseEntity<GenericResponse<List<Appointment>>> getAppointmentsByDoctorId(@PathVariable String doctorId) { 
		
		logger.info("Fetching appointments for doctor Id: {}", doctorId);
		
		try {
			List<Appointment> appointmentList = appointmentService.getByDoctorId(doctorId);
			logger.info("Appointments fetched successfully");
			return ResponseEntity.ok().body(new GenericResponse<>("Appointments fetched successfully for doctor Id: "+ doctorId, appointmentList));
		}catch(Exception e) {
			logger.error("Error fetching appointments for doctor Id {}: {} ", doctorId, e.getMessage(), e); //Logging frameworks such as SLF4J support passing a Throwable as the final argument without requiring a corresponding {} placeholder
			return ResponseEntity.internalServerError().body(new GenericResponse<>("Unable to fetch appointments", null));
		}
		
	}
	
	/* Retrieves appointments for a specific patient by patient Id 
	 * @param : String patientId
	 * @return : response entity containing Generic Response of list of appointments or null for error
	 */
	@GetMapping("/patient/{patientId}")
	public ResponseEntity<GenericResponse<List<Appointment>>> getAppointmentsByPatientId(@PathVariable String patientId) { 
		
		logger.info("Fetching appointments for patient Id: {}", patientId);
		
		try {
			List<Appointment> appointmentList = appointmentService.getByPatientId(patientId);
			logger.info("Appointments fetched successfully");
			return ResponseEntity.ok().body(new GenericResponse<>("Appointments fetched successfully for patient Id: "+patientId, appointmentList));
		}catch(Exception e) {
			logger.error("Error fetching appointments for patient Id {}: {} ", patientId, e.getMessage(), e); //Logging frameworks such as SLF4J support passing a Throwable as the final argument without requiring a corresponding {} placeholder
			return ResponseEntity.internalServerError().body(new GenericResponse<>("Unable to fetch appointments", null));
		}
		
	}
	
	/* Retrieves all appointments 
	 * @return : response entity containing generic response of list of appointments or null for error
	 */
	@GetMapping("/all")
	public ResponseEntity<GenericResponse<List<Appointment>>> getAllAppointments(){
		
		logger.info("Fetching all appointments");
		
		try {
			List<Appointment> appointmentList = appointmentService.getAllAppointments();
			logger.info("All appointments have fetched successfully.");
			return ResponseEntity.ok().body(new GenericResponse<>("Appointments fetched successfully", appointmentList));
		}catch(Exception e){
			logger.error("Error fetching all appointments: {}", e.getMessage());
			return ResponseEntity.internalServerError().body(new GenericResponse<>("Unable to fetch appointments", null));
		}
		
	}
	
	/* Update existing appointment 
	 * @param : appointmentRequest the updated appointment details
	 * @return : response entity of Generic response of updated appointment's Id in string or error message for error
	 */
	@PutMapping("/{id}")
	public ResponseEntity<GenericResponse<String>> updateAppointment(@PathVariable UUID id, @Valid @RequestBody AppointmentRequest appointmentRequest){
		
		logger.info("Updating appointment: {}", appointmentRequest);
		
		try {
			String appointmentId = appointmentService.updateAppointment(id, appointmentRequest);
			logger.info("Appointment updated successfully: {}", appointmentId);
			return ResponseEntity.ok().body(new GenericResponse<>("Appointment with Id" + appointmentId + " updated successfully", null));
		}catch(Exception e) {
			logger.error("Error updating appointment: {}", e.getMessage());
			return ResponseEntity.internalServerError().body(new GenericResponse<>("Unable to update appointment", null));
		}
		
	}
	
}
