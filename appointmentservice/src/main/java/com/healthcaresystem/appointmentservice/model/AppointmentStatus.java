package com.healthcaresystem.appointmentservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.healthcaresystem.appointmentservice.exception.InvalidAppointmentStatusException;

/* Enum to represent appointment statuses */

public enum AppointmentStatus {
	
	PENDING, 
	CONFIRMED,
	REJECTED,
	COMPLETED;
	
	/* Converts string value to Appointment status enum*/
	
	@JsonCreator        //Converts json string to java object/enum
	public static AppointmentStatus convertJsonToEnum(String value) {
		
		
		if(value == null || value.isBlank()) {    //If Value is null or blank, return exception
			throw new InvalidAppointmentStatusException("Status field is required");
		}
		
		
		for(AppointmentStatus status : AppointmentStatus.values()) { //check if any enum value matches with input value
			if(status.name().equalsIgnoreCase(value)) {      //.name() extracts exact value of enum
				return status;
			}
		}
		
		throw new InvalidAppointmentStatusException("Invalid appointment Status : " + value);
	
	}
	
	/* Convert Appointment Status enum value to string value*/
	
	@JsonValue    //converts java object/enum to json String
	public String convertEnumToJson() {
		return this.name();
	}
}
