package com.healthcaresystem.patientservice.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* Patient entity */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(value = "patients")
public class Patient {
	
	@Id
	@Field(targetType = FieldType.STRING)      //Id will be stored in document as a string
	private UUID id;
	
	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private String phone;
	
	private Integer age;
	
}
