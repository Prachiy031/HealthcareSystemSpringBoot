package com.healthcaresystem.notificationservice.service;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.healthcaresystem.notificationservice.dto.payloadrequest.NotificationRequest;
import com.healthcaresystem.notificationservice.dto.payloadresponse.NotificationResponse;
import com.healthcaresystem.notificationservice.model.AppointmentStatus;

/* Service class including logic of message sending */

@Service
public class NotificationService {
	
	/* Subjects based on appointment status */
	private static final String APPOINTMENT_PENDING_SUBJECT = "Appointment pending approval";
	private static final String APPOINTMENT_CONFIRMED_SUBJECT = "Appointment confirmed";
	private static final String APPOINTMENT_REJECTED_SUBJECT = "Appointment rejected";
	private static final String APPOINTMENT_UPDATED_SUBJECT = "Appointment Update";
	private static final String APPOINTMENT_COMPLETED_SUBJECT = "Appointment Completed";

	/* Body based on appointment status */
	private static final String APPOINTMENT_PENDING_EMAIL_BODY = 
			"Dear Doctor,\\n\\nYour appointment with Patient: %s is currently pending approval.\\nAppointment Date and Time: %s\\nNotes: %s\\nDoctor's Comments: %s\\n\\nThank you,\\nTeam PositiveHealth";
	
	private static final String APPOINTMENT_CONFIRMED_EMAIL_BODY = 
			"Dear Patient,\\n\\nYour appointment with Doctor: %s has been confirmed.\\nAppointment Date and Time: %s\\nNotes: %s\\nDoctor's Comments: %s\\n\\nThank you,\\nTeam PositiveHealth";
	
	private static final String APPOINTMENT_REJECTED_EMAIL_BODY = 
			"Dear Patient,\\n\\nWe regret to inform you that your appointment with Doctor: %s has been rejected.\\nPlease contact support for further assistance.\\nNotes: %s\\nDoctor's Comments: %s\\n\\nThank you,\\nTeam PositiveHealth";
	
	private static final String APPOINTMENT_UPDATED_EMAIL_BODY = 
			"Dear Patient,\\n\\nYour appointment with Doctor: %s has been updated.\\nAppointment Date and Time: %s\\nNotes: %s\\nDoctor's Comments: %s\\n\\nThank you,\\nTeam PostiveHealth";
	
	private static final String APPOINTMENT_COMPLETED_EMAIL_BODY = 
			"Dear Patient,\\n\\nYour appointment with Doctor: %s has been successfully completed.\\nAppointment Date and Time: %s\\nNotes: %s\\nDoctor's Comments: %s\\n\\nThank you,\\nTeam PositiveHealth";
	
	/* Instead of printing ISO-formatted date time, we can use this format-> a: am or pm designator */
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MM yyyy, hh:mm a");
    
    @Autowired
    private EmailService emailService;
       
	/* Method for deciding notification data and sending email further */
	public NotificationResponse triggerEmailNotification(NotificationRequest notificationRequest) {
		
		String to, subject, text;
		String doctor = notificationRequest.getDoctorFirstName() + " " + notificationRequest.getDoctorLastName();
		String patient = notificationRequest.getPatientFirstName() + " " + notificationRequest.getPatientLastName();
		String doctorComments = notificationRequest.getDoctorComments();
		String appointementNotes = notificationRequest.getAppointmentNotes();
		AppointmentStatus appointmentStatus= notificationRequest.getAppointmentStatus();
		
		/* Switch case -> to decide which text to add in body as per appointment status, whom to send email and what subject of email has to be there */
		switch(appointmentStatus){
			
			/* Appointment is PENDING from doctor status
			 * email has to be send to doctor 
			 */
		
			case PENDING:
				to = notificationRequest.getDoctorEmail();
				subject = APPOINTMENT_PENDING_SUBJECT;
				text = String.format(
						APPOINTMENT_PENDING_EMAIL_BODY,
						patient,
						notificationRequest.getAppointmentTime().format(dateTimeFormatter),
						appointementNotes,
						doctorComments
						);
				break;
		
			/* Appointment is confirmed from doctor 
			 * email has to be send to patient
			 */
			
			case CONFIRMED:
				to = notificationRequest.getPatientEmail();
				subject = APPOINTMENT_CONFIRMED_SUBJECT;
				text = String.format(
						APPOINTMENT_CONFIRMED_EMAIL_BODY,
						doctor,
						notificationRequest.getAppointmentTime().format(dateTimeFormatter),
						appointementNotes,
						doctorComments
						);
				break;
				
			
			/* Appointment is rejected from doctor 
			* email has to be send to patient
			*/
				
			case REJECTED:
				to = notificationRequest.getPatientEmail();
				subject = APPOINTMENT_REJECTED_SUBJECT;
				text = String.format(
						APPOINTMENT_REJECTED_EMAIL_BODY,
						doctor,
						appointementNotes,
						doctorComments
						);
				break;	
				
			/* Appointment has completed 
			* email has to be send to patient
			*/
				
			case COMPLETED:
				to = notificationRequest.getPatientEmail();
				subject = APPOINTMENT_COMPLETED_SUBJECT;
				text = String.format(
						APPOINTMENT_COMPLETED_EMAIL_BODY,
						doctor,
						notificationRequest.getAppointmentTime().format(dateTimeFormatter),
						appointementNotes,
						doctorComments
						);
				break;
				
			/* Default case is appointment updated 
			 * Email has to be send to patient 
			 */
			
			default:
				to = notificationRequest.getPatientEmail();
				subject = APPOINTMENT_UPDATED_SUBJECT;
				text = String.format(
						APPOINTMENT_UPDATED_EMAIL_BODY,
						doctor,
						notificationRequest.getAppointmentTime().format(dateTimeFormatter),
						appointementNotes,
						doctorComments
						);
				break;
		}
		
		/* Send Email using EmailService class' object */
		emailService.sendSimpleMessage(to, subject, text);
		
		/* Send response DTO */
		return new NotificationResponse(notificationRequest.getAppointmentId(), "Email sent successfully");
		
	}
	
}
