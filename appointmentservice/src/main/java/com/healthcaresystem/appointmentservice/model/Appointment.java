package com.healthcaresystem.appointmentservice.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* Represents Appointment entity*/

@NoArgsConstructor
@Setter
@Getter
@Document(collection = "appointments")
public class Appointment {
	
	@Id
	@Field(targetType = FieldType.STRING) /*Stored as string*/
	private UUID id;   /* UUID : Universally Unique Identifier [32 hexadecimal characters separated by 4 hyphens -> it can be converted to String]{to uniquely identify id, to harder to guess it}*/
	
	private Patient patient;
	private Doctor doctor;
	private LocalDateTime appointmentTime;
	private AppointmentStatus status; //CONFIRMED, CANCELED, PENDING, REJECTED
	private String notes;
	private String doctorComments;
}
