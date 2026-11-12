package com.healthcaresystem.notificationservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.healthcaresystem.notificationservice.dto.payloadrequest.NotificationRequest;
import com.healthcaresystem.notificationservice.dto.payloadresponse.NotificationResponse;
import com.healthcaresystem.notificationservice.service.NotificationService;

import jakarta.validation.Valid;

/* Controller for handling rest APIs related to email notification */

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationServiceController {
	
	private final NotificationService notificationService;

	public NotificationServiceController(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@PostMapping("/notification-confirmation")
	public ResponseEntity<NotificationResponse> sendNotification(@Valid @RequestBody NotificationRequest notificationRequest){
		
		NotificationResponse notificationResponse = notificationService.triggerEmailNotification(notificationRequest);
		
		return ResponseEntity.ok(notificationResponse);
		
	}
	
}
