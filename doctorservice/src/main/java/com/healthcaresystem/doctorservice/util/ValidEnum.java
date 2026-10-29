package com.healthcaresystem.doctorservice.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.healthcaresystem.appointmentservice.util.EnumValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/* Custom annotation class */
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidator.class)
public @interface ValidEnum {
	
	String message() default "Invalid value for enum";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    // Optionally specify the enum class this annotation is validating
    Class<? extends Enum<?>> enumClass();
    
}
