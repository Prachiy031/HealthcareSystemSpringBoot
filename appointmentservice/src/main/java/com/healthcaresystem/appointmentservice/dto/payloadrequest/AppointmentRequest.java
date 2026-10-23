package com.healthcaresystem.appointmentservice.dto.payloadrequest;

import java.time.LocalDateTime;
import java.util.UUID;
import com.healthcaresystem.appointmentservice.model.AppointmentStatus;
import com.healthcaresystem.appointmentservice.util.ValidEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/* Represents request payload for creating or updating an appointment*/

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AppointmentRequest {
	
	private String id;
	
	@NotNull(message = "Doctor ID must not be null")
	private UUID doctorId;
	
	@NotNull(message = "Patient ID must not be null")
	private UUID patientId;
	
	@NotNull(message = "Appointment time must not be null")
	private LocalDateTime appointmentTime;
	
	@NotNull(message = "Status must be provided")
	@ValidEnum(message = "Status must be valid", enumClass = AppointmentStatus.class)
	private String status;
	
	@NotBlank(message = "notes must not be blank")
	@Size(max = 200, message = "Notes must be 200 characters or less")
	private String notes;
	
	/* optional */
	@Size(max = 200, message = "Doctor comments must be 200 characters or less")
	private String doctorComments;
}
