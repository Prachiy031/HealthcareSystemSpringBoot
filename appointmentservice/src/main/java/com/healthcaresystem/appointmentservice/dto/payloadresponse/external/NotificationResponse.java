package com.healthcaresystem.appointmentservice.dto.payloadresponse.external;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/* Response DTO for sending response after sending notification */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class NotificationResponse {
	
	private UUID appointmentId;
	
	private String message;
	
}
