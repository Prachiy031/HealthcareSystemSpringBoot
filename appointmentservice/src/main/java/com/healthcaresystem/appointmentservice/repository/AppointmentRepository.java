package com.healthcaresystem.appointmentservice.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.healthcaresystem.appointmentservice.model.Appointment;


/* Repository for managing appointments in the database*/

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, UUID>{
	
	/* Used Derived queries => based upon names queries will get generated */
	
	/* Find appointments by Doctor ID, sorted as specified*/
	
	List<Appointment> findByDoctorId(String doctorId, Sort sort);
	
	/* Find appointments by Patient ID, sorted as specified*/
	List<Appointment> findByPatientId(String patientId, Sort sort);
	
	/* Retrieves all appointments sorted by appointment time in ascending order*/
	List<Appointment> findAllByOrderByAppointmentTimeAsc();
	
}
