package com.healthcaresystem.patientservice.dto.payloadresponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* Generic Response containing message and data of any type T */

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GenericResponse<T> {
	
	private String message;
	
	private T data;

	public GenericResponse(String message) {
		
		this.message = message;
		
	}

}
