package com.healthcare.healthcare.validation;


import com.healthcare.healthcare.validation.validator.DoctorRegistrationRequestValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DoctorRegistrationRequestValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDoctorRegistrationRequest {
    String message() default "Invalid Doctor Registration Request";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
