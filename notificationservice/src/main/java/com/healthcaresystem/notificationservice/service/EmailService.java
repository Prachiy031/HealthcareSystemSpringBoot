package com.healthcaresystem.notificationservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;



/* Custom class for sending simple message to patient or doctor*/

@Service
public class EmailService {
	
	/* Handles sending email - interface which provides inbuilt methods to send email */
	private final JavaMailSender mailSender;
	
	/* From email address */
	@Value("${spring.mail.username}")
	private String FROM_ADDRESS;
	
	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	/* Create simple message for sending in email -> custom method for sending message {No attachments supported}*/
	public void sendSimpleMessage(String to, String subject, String text) {
		
		try {
			/* Class provided by spring framework for setting mail message using inbuilt methods */
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(FROM_ADDRESS);
			message.setTo(to);
			message.setSubject(subject);
			message.setText(text);
			
			mailSender.send(message);
			
		}catch(MailException exception) {
			exception.printStackTrace();
		}
		
	}
}
