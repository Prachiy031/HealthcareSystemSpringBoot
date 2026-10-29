package com.healthcaresystem.doctorservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.healthcaresystem.doctorservice.exception.InvalidDoctorStatusException;

/* Enum to represent doctor statuses */

public enum DoctorStatus {
	
	AVAILABLE,
	NON_AVAILABLE,
	DISABLED;
	
	/* Converts json string value to Doctor Status enum value */
	
	@JsonCreator 		// Converts Json string value to enum 
	public static DoctorStatus fromJsonToEnum(String value) {
		
		if(value == null || value.isBlank()) {		//check if value is null or blank
			throw new InvalidDoctorStatusException("Status field is required");
		}
		
		for(DoctorStatus status: DoctorStatus.values()) {
			if(status.name().equalsIgnoreCase(value)) {     //check if any status matches with input value
				return status;
			}
		}
		
		throw new InvalidDoctorStatusException("Invalid doctor status value "+value);
		
	}
	
	/* Converts Doctor Status Enum value to Json String */
	
	@JsonValue      //converts enum value to json string
	public String fromEnumToJson() {
		return this.name();
	}
}
