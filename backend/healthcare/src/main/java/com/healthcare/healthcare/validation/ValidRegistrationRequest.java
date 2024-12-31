package com.healthcare.healthcare.validation;

import com.healthcare.healthcare.validation.validator.RegistrationRequestValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = RegistrationRequestValidator.class)
@Target(ElementType.TYPE) // Applies to class-level validations
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRegistrationRequest {
    String message() default "Invalid registration details";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
