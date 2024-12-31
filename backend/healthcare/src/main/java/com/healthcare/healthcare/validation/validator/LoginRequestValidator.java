package com.healthcare.healthcare.validation.validator;

import com.healthcare.healthcare.dto.request.LoginRequestDTO;

import com.healthcare.healthcare.validation.ValidLoginRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class LoginRequestValidator
        implements ConstraintValidator<ValidLoginRequest, LoginRequestDTO> {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    @Override
    public boolean isValid(LoginRequestDTO dto, ConstraintValidatorContext context) {
        boolean isValid = true;

        if (dto.getEmail() == null || !EMAIL_PATTERN.matcher(dto.getEmail()).matches()) {
            addConstraintViolation(context, "Email must be valid");
            isValid = false;
        }

        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            addConstraintViolation(context, "Password must be at least 6 characters long");
            isValid = false;
        }

        if (dto.getToken() == null || dto.getToken().trim().isEmpty()) {
            addConstraintViolation(context, "Token must not be empty");
            isValid = false;
        }

        return isValid;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
