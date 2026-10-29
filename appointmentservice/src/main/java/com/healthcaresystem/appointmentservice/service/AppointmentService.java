package com.healthcaresystem.appointmentservice.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
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
import com.healthcaresystem.appointmentservice.model.Appointment;
import com.healthcaresystem.appointmentservice.model.AppointmentStatus;
import com.healthcaresystem.appointmentservice.model.Doctor;
import com.healthcaresystem.appointmentservice.model.Patient;
import com.healthcaresystem.appointmentservice.repository.AppointmentRepo;


/* Service class to handle appointment-related business logic 
 * Manages bookings, retrieval and updating of appointments
 */

@Service
public class AppointmentService {
	
	@Value("${app.environment}")
	private String environment;
	
	@Autowired
	private AppointmentRepo appointmentRepository;
	
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
	
	/* For logging */
    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

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
    		
    		/* Fetch and validate doctor details */
    		Doctor doctor = fetchDoctorDetails(appointment.getDoctorId().toString());
    		logger.info("Fetched doctor details inside book appointment: {}", objectMapper.writeValueAsString(doctor)); // {} is a placeholder, slf4j replaces it with value you pass as next argument   
    		
    		/* Fetch and validate patient's details */
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
    		
    		/* Fetch and validate Doctor details */
    		Map<String, Object> doctor = restTemplate.getForObject(doctorServiceUrl + "/" + appointment.getDoctorId(), Map.class);    //Retrieve a representation by doing a GET on the mentioned URL.The response in the given format is converted and returned {class is expected} -> JSON to Expected java Object  
    		if(doctor == null || doctor.isEmpty()) {
    			throw new ResourceNotFoundException("Doctor not found with ID: "+appointment.getDoctorId());
    		}
    		
    		/* Fetch and validate Patient details */
    		Map<String, Object> patient = restTemplate.getForObject(patientServiceUrl + "/" + appointment.getPatientId(), Map.class);    //Retrieve a representation by doing a GET on the mentioned URL.The response in the given format is converted and returned {class is expected} -> JSON to Expected java Object
    		if(patient == null || patient.isEmpty()) {
    			throw new ResourceNotFoundException("Patient not found with ID: "+appointment.getPatientId());
    		}
    		
    		Map<String, Object> doctorData = (Map<String, Object>) doctor.get("data"); //retrieved doctor data from response we've got from doctorResponse DTO {message and data mentioned in response}.. converted from json->object
    		
    		/*Manual conversion can be done using Doctor DTO class directly as {Only if Doctor DTO and actual Doctor entity class have same structure}:
    		 Doctor doctor1 = restTemplate.getForObject(patientServiceUrl + "/" + appointment.getPatientId(), Doctor.class);    
    		*/
    		
    		/* Manually create Doctor Object from the map */
    		Doctor doctor1 = new Doctor();
    		doctor1.setId((String) doctorData.get("id"));
    		doctor1.setFirstName((String) doctorData.get("firstName"));
    		doctor1.setLastName((String) doctorData.get("lastName"));
    		doctor1.setEmail((String) doctorData.get("email"));
    		doctor1.setPhone((String) doctorData.get("phone"));
    		doctor1.setSpeciality((String) doctorData.get("speciality"));
    		doctor1.setYearsOfExperience((Integer) doctorData.get("yearsOfExperience"));
    		doctor1.setStatus((String) doctorData.get("status"));
    		
    		Map<String, Object> patientData = (Map<String, Object>) patient.get("data");

            /* Manually create a Patient object from the Map */
            Patient patient1 = new Patient();
            patient1.setId((String) patientData.get("id"));
            patient1.setFirstName((String) patientData.get("firstName"));
            patient1.setLastName((String) patientData.get("lastName"));
            patient1.setEmail((String) patientData.get("email"));
            patient1.setPhone((String) patientData.get("phone"));
            patient1.setAge((Integer) patientData.get("age"));

            /* Update appointment details */
            existingAppointment.setDoctor(doctor1);
            existingAppointment.setPatient(patient1);
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
    	
    	/* Deserialzed to Java Object */
    	ResponseEntity<GenericResponse<Doctor>> responseEntity = restTemplate.exchange(
    																					doctorServiceUrl +"/" + doctorId,  //URL to fetch from
    																					HttpMethod.GET,  //Http method
    																					null,            //requestentity
    																					new ParameterizedTypeReference<GenericResponse<Doctor>>() {}); //responseEntity
    	/* ParameterizedTypeReference preserves the full generic type so Spring can deserialize the response into GenericResponse<Doctor> correctly */
    	
    	GenericResponse<Doctor> getDoctorResponse = responseEntity.getBody(); 
    	
    	if(getDoctorResponse != null && getDoctorResponse.getData() != null) {
    		return getDoctorResponse.getData();
    	}
    	
    	throw new ResourceNotFoundException("Doctor not found with ID : " + doctorId);
    	
    }
    
    /* Fetches Patient details */
    private Patient fetchPatientDetails(String patientId) {
    	
    	if(isDevelopmentEnvironment()) {
    		/* Return Mock Patient Object, as we can develop and test appointment service without patient service {if it is not built yet} */
    		return new Patient(patientId, "Ana", "Smith" ,"ana23smith@gmail.com", "9080788760", 34);
    	}
    	
    	/* Deserialzed to Java Object */
    	ResponseEntity<GenericResponse<Patient>> responseEntity = restTemplate.exchange(
    																					patientServiceUrl +"/" + patientId,  //URL to fetch from
    																					HttpMethod.GET,  //Http method
    																					null,            //requestentity
    																					new ParameterizedTypeReference<GenericResponse<Patient>>() {}); //responseEntity
    	/* ParameterizedTypeReference preserves the full generic type so Spring can deserialize the response into GenericResponse<Doctor> correctly */
    	
    	GenericResponse<Patient> getPatientResponse = responseEntity.getBody(); 
    	
    	if(getPatientResponse != null && getPatientResponse.getData() != null) {
    		return getPatientResponse.getData();
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
