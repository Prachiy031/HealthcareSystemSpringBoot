/**
 * 
 */
package com.healthcaresystem.appointmentservice.util;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;


@Retention(RetentionPolicy.RUNTIME)  //when annotation metadata is available {retained and available at runtime}
@Target({ElementType.ANNOTATION_TYPE, 
		ElementType.METHOD, 
		ElementType.PARAMETER, 
		ElementType.FIELD})         //where validation can be applied
@Constraint(validatedBy = EnumValidator.class)     //where to check validation for annotation {validator class}
public @interface ValidEnum {

	/* Default error message when value is invalid */
	String message() default "Invalid value for enum";

	/* Enum class that the annotation validates against */
	Class<? extends Enum<?>> enumClass();
	
	/* Used to specify validation groups */
    Class<?>[] groups() default {};

    /* Used to specify custom payload objects */
    Class<? extends Payload>[] payload() default {};
	
}
