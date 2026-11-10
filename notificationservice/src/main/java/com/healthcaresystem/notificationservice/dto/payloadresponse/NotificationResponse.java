package com.healthcaresystem.notificationservice.dto.payloadresponse;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* Response DTO for sending notification */

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NotificationResponse {
	
	private UUID appointmentId;
	
	private String message;
	
}
