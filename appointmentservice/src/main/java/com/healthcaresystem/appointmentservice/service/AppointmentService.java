package com.healthcaresystem.appointmentservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.healthcaresystem.appointmentservice.repository.AppointmentRepo;

/* Service class to handle appointment-related business logic 
 * Manages bookings, retrieval and updating of appointments
 */

@Service
public class AppointmentService {
	
	@Value("${app.environment}")
	private String environment;
	
	@Autowired
	private AppointmentRepo appointmentrepository;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${doctor.service.url}")
	private String doctorServiceUrl;
	
	@Value("${patient.service.url}")
	private String patientServiceUrl;
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	@Value("${spring.kafka.topic.name}")
	private String topicName;
	
    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

	
}
