package com.healthcaresystem.appointmentservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.healthcaresystem.appointmentservice.dto.payloadrequest.external.NotificationRequest;
import com.healthcaresystem.appointmentservice.dto.payloadresponse.external.NotificationResponse;

import jakarta.validation.Valid;

/* Feign Client interface for calling rest apis of notifictaion service into appointment service class */

@FeignClient(name = "notification-service", url = "${notification.service.url}")
public interface NotificationServiceClient {
	 
	@GetMapping("api/v1/notifications/notification-confirmation")
	public ResponseEntity<NotificationResponse> sendNotification(@Valid @RequestBody NotificationRequest notificationRequest);
	
}
