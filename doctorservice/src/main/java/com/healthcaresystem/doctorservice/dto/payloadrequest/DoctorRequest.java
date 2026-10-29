package com.healthcaresystem.doctorservice.dto.payloadrequest;

import com.healthcaresystem.doctorservice.model.DoctorStatus;
import com.healthcaresystem.doctorservice.util.ValidEnum;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/* Request DTO for doctor*/

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DoctorRequest {
	
	@NotBlank(message = "First name must not be blank")
	@Size(max = 15, message = "First name must be of 15 characters or less")
	private String firstName;
	
	@NotBlank(message = "Last name must not be blank")
	@Size(max = 15, message = "Last name must be of 15 characters or less")
	private String lastName;
	
	@NotBlank(message = "Email must not be blank")
	@Email
	private String email;
	
	@NotBlank(message = "Phone must not be blank")
	private String phone;
	
	@NotBlank(message = "Speciality must not be blank")
	@Size(max = 200, message = "Speciality must be of 200 characters or less")
	private String speciality;
	
	@NotNull(message = "Years must be provided")
	@Min(value = 0L, message = "Years must be positive")
	private Integer yearsOfExperience;
	
	@NotNull(message = "Status must be provided")
	@ValidEnum(message = "Status must be valid", enumClass = DoctorStatus.class)
	private String status;	
	
}
