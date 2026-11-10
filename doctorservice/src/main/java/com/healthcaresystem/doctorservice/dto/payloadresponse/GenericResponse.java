package com.healthcaresystem.doctorservice.dto.payloadresponse;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* Generic Response DTO class containing some message and data */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GenericResponse<T> {
	
	private String message;
	private T data;
	
	public GenericResponse(String message) {
		this.message = message;
	}
	
}
