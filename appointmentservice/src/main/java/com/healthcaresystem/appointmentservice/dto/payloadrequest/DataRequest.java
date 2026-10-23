package com.healthcaresystem.appointmentservice.dto.payloadrequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* Generic Wrapper class for request payloads that include message and associated data
 * T is type of data being wrapped in request
 */

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DataRequest<T> {
	
	private String message; 
	
	private T data;
	
}
