package com.healthcaresystem.appointmentservice.exception;

/* Exception to be thrown when invalid appointment status is encountered*/

public class InvalidAppointmentStatusException extends RuntimeException{

	public InvalidAppointmentStatusException(String message) {
		super(message);
	}
	
}
