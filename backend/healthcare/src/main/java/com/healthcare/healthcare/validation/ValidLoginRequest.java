package com.healthcare.healthcare.validation;

import com.healthcare.healthcare.validation.validator.LoginRequestValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = LoginRequestValidator.class)
@Target(ElementType.TYPE) // Applies to class-level validations
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLoginRequest {
    String message() default "Invalid login details";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
