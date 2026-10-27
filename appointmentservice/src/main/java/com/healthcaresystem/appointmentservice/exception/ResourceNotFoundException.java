package com.healthcaresystem.appointmentservice.exception;

/* Exception to be thrown when Expected resource is not found */
public class ResourceNotFoundException extends RuntimeException{

	public ResourceNotFoundException(String message) {
		super(message);
	}
	
}
