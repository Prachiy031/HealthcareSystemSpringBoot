package com.healthcaresystem.appointmentservice.dto.payloadresponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* Generic Response Payload which contains message and data associated with it*/

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponse<T> {
	
	private String message;
	
	private T data;
	
	public GenericResponse(String message) {
		this.message = message;
	}
}
