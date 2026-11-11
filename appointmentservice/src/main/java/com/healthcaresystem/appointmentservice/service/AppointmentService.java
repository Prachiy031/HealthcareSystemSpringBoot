package com.healthcaresystem.appointmentservice.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.healthcaresystem.appointmentservice.dto.payloadrequest.AppointmentRequest;
import com.healthcaresystem.appointmentservice.dto.payloadresponse.GenericResponse;
import com.healthcaresystem.appointmentservice.exception.ResourceNotFoundException;
import com.healthcaresystem.appointmentservice.feignclient.DoctorServiceClient;
import com.healthcaresystem.appointmentservice.feignclient.PatientServiceClient;
import com.healthcaresystem.appointmentservice.model.Appointment;
import com.healthcaresystem.appointmentservice.model.AppointmentStatus;
import com.healthcaresystem.appointmentservice.model.Doctor;
import com.healthcaresystem.appointmentservice.model.Patient;
import com.healthcaresystem.appointmentservice.repository.AppointmentRepository;


/* Service class to handle appointment-related business logic 
 * Manages bookings, retrieval and updating of appointments
 */

@Service
public class AppointmentService {
	
	@Value("${app.environment}")
	private String environment;
	
	@Autowired
	private AppointmentRepository appointmentRepository;
	
	@Value("${doctor.service.url}")
	private String doctorServiceUrl;
	
	@Value("${patient.service.url}")
	private String patientServiceUrl;
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	@Value("${spring.kafka.topic.name}")
	private String topicName;
	
	/* For logging */
    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);
    
    /* Feign Client for calling rest apis of doctor service into this service class  ..calling rest apis in service class */
    @Autowired    
    private DoctorServiceClient doctorServiceClient;
    
    /* Feign Client for calling rest apis of pateint service into this service class  ..calling rest apis in service class */
    @Autowired
    private PatientServiceClient patientServiceClient;

    /* Books a new appointment by validating doctor and patient information */
    public String bookAppointment(AppointmentRequest appointment) {
    	
    	try {
    		
    		/* To Serialize appointment object to JSON 
    		 * ObjectMapper serializes object to json and vice versa
    		 * it provides regsiterModule method which provides additional support for Java 8 data types
    		 * during ser/deser
    		 * */
    		ObjectMapper objectMapper = new ObjectMapper();
    		objectMapper.registerModules(new JavaTimeModule());
    		
    		/* Fetch and validate doctor details using feign client*/
    		Doctor doctor = fetchDoctorDetails(appointment.getDoctorId().toString());
    		logger.info("Fetched doctor details inside book appointment: {}", objectMapper.writeValueAsString(doctor)); // {} is a placeholder, slf4j replaces it with value you pass as next argument   
    		
    		/* Fetch and validate patient's details using feign client*/
    		Patient patient =  fetchPatientDetails(appointment.getPatientId().toString());
    		logger.info("Fetched patient details inside book appointment: {}", objectMapper.writeValueAsString(patient)); //writeValueAsString -> serializes java obj into json 
    		
    		/* Create and save the appointment */
    		UUID generatedId = UUID.randomUUID();
    		Appointment newAppointment = new Appointment();
    		newAppointment.setId(generatedId);
    		newAppointment.setDoctor(doctor);
    		newAppointment.setPatient(patient);
    		newAppointment.setAppointmentTime(appointment.getAppointmentTime());
    		newAppointment.setDoctorComments(appointment.getDoctorComments());
    		newAppointment.setNotes(appointment.getNotes());
    		newAppointment.setStatus(AppointmentStatus.PENDING);
    		
    		/* Newly created Appointement ID */
    		String appointmentId = String.valueOf(appointmentRepository.save(newAppointment).getId());
    		
    		/* Serialize Appointment Object to JSON string for sending to Kafka */
            String appointmentJson = objectMapper.writeValueAsString(newAppointment);
            
            /* Message sent to Kafka */
    		sendEventToKafka(appointmentJson);
    		
    		return appointmentId;
    		
    	}catch(Exception e) {
    		
    		throw new ResourceNotFoundException("Error booking appointment");
    		
    	}
    }
    
    /* Retrieves appointments by Doctor ID, sorted by appointment time in ascending time*/
    public List<Appointment> getByDoctorId(String doctorId){
    	/*Sort.Direction.ASC for sorting in ascending order
    	* By Default ascending order will be there
    	* */
    	
    	return appointmentRepository.findByDoctorId(doctorId, Sort.by(Sort.Direction.ASC, "appointmentTime"));
    	
    }
    
    /* Retrieves appointments by patient ID, sorted by appointment time in ascending order */
    public List<Appointment> getByPatientId(String patientId){
    	
    	return appointmentRepository.findByPatientId(patientId, Sort.by(Sort.Direction.ASC, "appointmentTime"));
    	
    }
    
    /* Retrieves all appointments, sorted by appointment time in ascending order*/
    public List<Appointment> getAllAppointments(){
    	
    	return appointmentRepository.findAllByOrderByAppointmentTimeAsc();
    	
    }
    
    /* Update an existing appointment 
     * @param updated appointment id, updated appointment details
     * */
    public String updateAppointment(UUID id, AppointmentRequest appointment) {
    	
    	try {
    		
    		/* To Serialize Appointment object to JSON */
    		ObjectMapper objectMapper = new ObjectMapper();
    		objectMapper.registerModules(new JavaTimeModule());
    		
    		/* find existing appointment */
    		Appointment existingAppointment = appointmentRepository.findById(id)       //fromString -> convert UUID to string
    															   .orElseThrow(()-> new ResourceNotFoundException("Appointment Not Found with ID: " + id));
    		
    		/* Fetch and validate Doctor details - used feign client declared method inside fetchDetails method */
        	Doctor doctor = fetchDoctorDetails(appointment.getDoctorId().toString());
    		if(doctor == null) {
    			throw new ResourceNotFoundException("Doctor not found with ID: "+appointment.getDoctorId());
    		}
    		
    		/* Fetch and validate Patient details - used feign client declared method inside fetchDetails method */
        	Patient patient = fetchPatientDetails(appointment.getPatientId().toString());
    		if(patient == null) {
    			throw new ResourceNotFoundException("Patient not found with ID: "+appointment.getPatientId());
    		}


            /* Update appointment details */
            existingAppointment.setDoctor(doctor);
            existingAppointment.setPatient(patient);
            existingAppointment.setAppointmentTime(appointment.getAppointmentTime());
            existingAppointment.setNotes(appointment.getNotes());
            existingAppointment.setDoctorComments(appointment.getDoctorComments());
            existingAppointment.setStatus(AppointmentStatus.convertJsonToEnum(appointment.getStatus()));
    		
            /* Updated Appointment ID */
    		String idResponse = String.valueOf(appointmentRepository.save(existingAppointment).getId());
    		
    		/* Updated Version of Appointment Object into JSON String for sending to kafka */
    		String appointmentJson = objectMapper.writeValueAsString(existingAppointment);     
    		
    		/* Message sent to Kafka */
    		sendEventToKafka(appointmentJson);      
    		
    		return idResponse;
    		
    	}catch(Exception e) {
    		throw new ResourceNotFoundException("Error Updating Appointment: "+e.getMessage());
    	}
    	
    }
    
    /* Fetches Doctor details */
    private Doctor fetchDoctorDetails(String doctorId) {
    	
    	if(isDevelopmentEnvironment()) {
    		/* Return Mock Doctor Object, as we can develop and test appointment service without doctor service {if it is not built yet} */
    		return new Doctor(doctorId, "Ashok", "Seth" ,"ashokSethHealthCardio@gmail.com", "9080788760", "Cardiology", 10, "ACTIVE");
    	}
    	
    	/* Call Doctor service through feign client - doctorserviceclient*/
    	ResponseEntity<GenericResponse<Doctor>> responseEntity = doctorServiceClient.getDoctorById(doctorId);
    	
    	GenericResponse<Doctor> doctorResponse = responseEntity.getBody();
    	
    	if(doctorResponse != null && doctorResponse.getData() != null) {
    		return doctorResponse.getData();
    	}
    	
    	throw new ResourceNotFoundException("Doctor not found with ID : " + doctorId);
    	
    }
    
    /* Fetches Patient details */
    private Patient fetchPatientDetails(String patientId) {
    	
    	if(isDevelopmentEnvironment()) {
    		/* Return Mock Patient Object, as we can develop and test appointment service without patient service {if it is not built yet} */
    		return new Patient(patientId, "Ana", "Smith" ,"ana23smith@gmail.com", "9080788760", 34);
    	}
    	
    	/* call patient service using feign client- patientserviceclient*/
    	ResponseEntity<GenericResponse<Patient>> responseEntity = patientServiceClient.getPatientById(patientId);
       	
    	GenericResponse<Patient> patientResponse = responseEntity.getBody(); 
    	
    	if(patientResponse != null && patientResponse.getData() != null) {
    		return patientResponse.getData();
    	}
    	
    	throw new ResourceNotFoundException("Patient not found with ID : " + patientId);
    	
    }
    
    /* Sends event to Kafka */
	private void sendEventToKafka(String appointmentJson) {
				
		/* 1. Send message to kafka 
		 * 2. Send message to kafka and handle the callback
		 * 3. Send the message to kafka and get a completableFuture
		 * */
		
		/* sending Message to Kafka..
		   Completable Future -> Represents a result that may become available later which will complete asynchronously
		   SendResult<String, String> -> result of sending kafka record
		   appointmentJson is getting store into topicName
		 */
		CompletableFuture<SendResult<String, String>> completableFuture = kafkaTemplate.send(topicName, appointmentJson);
		
		/* Add callbacks to handle success and failure */
		
		completableFuture.whenComplete((result, exception) -> {
			if(exception == null) {
				/* Success case */
				RecordMetadata metadata = result.getRecordMetadata();
				logger.info("Message sent successfully to topic : {}", topicName);
				logger.info("Partition: {}, Offset: {}",metadata.partition(),metadata.offset());
			}else {
				/* Failure Case */
				logger.error("Failed to send message to topic: {}", topicName);
				logger.error(exception.getMessage());
			}
		});
		
	}
	
	/* Checks current env is dev env or not */
	private boolean isDevelopmentEnvironment() {
		
		return "dev".equalsIgnoreCase(environment);
		
	}
	
}
