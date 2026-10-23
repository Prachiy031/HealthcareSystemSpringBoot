package com.healthcaresystem.appointmentservice.util;

import java.util.Arrays;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/* custom validator to check if a string value matches any constant of specified enum class */

public class EnumValidator implements ConstraintValidator<ValidEnum, String>{

	
	private Class<? extends Enum<?>> enumClass; //Enum class to validate against

	@Override
	public void initialize(ValidEnum constraintAnnotation) { //initialize the validator with the enum class provided in the annotation
		this.enumClass = constraintAnnotation.enumClass();
	}
	
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) { //validates if provided value matches any constant in specific enum class
		
		if(value == null || value.isEmpty()) {
			return true;
		}
		
		return Arrays.stream(enumClass.getEnumConstants())
					.anyMatch(e -> e.name().equalsIgnoreCase(value));
	}

}
