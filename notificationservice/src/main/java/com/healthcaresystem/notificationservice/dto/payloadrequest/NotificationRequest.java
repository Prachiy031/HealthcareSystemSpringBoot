package com.healthcaresystem.notificationservice.dto.payloadrequest;

import java.time.LocalDateTime;
import java.util.UUID;

import com.healthcaresystem.notificationservice.model.AppointmentStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* Request DTO containing doctor, patient and appointment details */

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NotificationRequest {
	
	@NotNull
	private UUID appointmentId;
	
	@NotNull
	private LocalDateTime appointmentTime;
	
	@NotNull
	private AppointmentStatus appointmentStatus;
	
	@NotBlank
	@Size(max = 50, message = "Appointment notes must be of 50 characters or less")
	private String appointmentNotes;
	
	@NotBlank
	@Size(max = 15, message = "Doctor's first name must be of 15 characters or less")
	private String doctorFirstName;
	
	@NotBlank
	@Size(max = 15, message = "Doctor's last name must be of 15 characters or less")
	private String doctorLastName;
	
	@NotBlank(message = "Doctor's Email must be provided")
	@Email
	private String doctorEmail;
	
	/* Optional */
	@Size(max = 200, message = "Doctor's comments must be of 200 characters or less")
	private String doctorComments;
	
	@NotBlank
	@Size(max = 15, message = "Patient's last name must be of 15 characters or less")
	private String patientFirstName;
	
	@NotBlank
	@Size(max = 15, message = "Patient's last name must be of 15 characters or less")
	private String patientLastName;
	
	@NotBlank(message = "Patient's Email must be provided")
	@Email
	private String patientEmail;
	
	
}
