package com.healthcaresystem.patientservice.dto.payloadrequest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* Patient Request DTO */

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PatientRequest {
	
	@NotBlank(message = "First Name must not be blank")
	@Size(max = 15, message = "First Name must be of 15 characters or less")
	private String firstName;
	
	@NotBlank(message = "Last Name must not be blank")
	@Size(max = 15, message = "Last Name must be of 15 characters or less")
	private String lastName;
	
	@NotBlank(message = "Email must not be blank")
	@Email
	private String email;
	
	@NotBlank(message = "Phone must not be blank")
	private String phone;
	
	@NotNull
	@Min(value = 0, message = "Age can't be below 0")
	@Max(value = 100, message = "Age can't be above 100")
	private Integer age;
	
}
