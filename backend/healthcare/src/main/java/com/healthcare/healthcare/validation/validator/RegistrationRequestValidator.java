package com.healthcare.healthcare.validation.validator;

import com.healthcare.healthcare.dto.request.RegistrationRequestDTO;
import com.healthcare.healthcare.eum.Role;
import com.healthcare.healthcare.validation.ValidRegistrationRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class RegistrationRequestValidator
        implements ConstraintValidator<ValidRegistrationRequest, RegistrationRequestDTO> {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private List<Role> roleList = Arrays.asList(Role.USER, Role.DOCTOR);

    @Override
    public boolean isValid(RegistrationRequestDTO dto, ConstraintValidatorContext context) {
        boolean isValid = true;

        if (dto.getFirstName() == null || dto.getFirstName().trim().isEmpty()) {
            addConstraintViolation(context, "First name must not be empty");
            isValid = false;
        }

        if (dto.getLastName() == null || dto.getLastName().trim().isEmpty()) {
            addConstraintViolation(context, "Last name must not be empty");
            isValid = false;
        }

        if (dto.getUserName() == null || dto.getUserName().trim().isEmpty()) {
            addConstraintViolation(context, "Username must not be empty");
            isValid = false;
        }

        if (dto.getEmail() == null || !EMAIL_PATTERN.matcher(dto.getEmail()).matches()) {
            addConstraintViolation(context, "Email must be valid");
            isValid = false;
        }

        if (dto.getPassword() == null || dto.getPassword().length() < 8) {
            addConstraintViolation(context, "Password must be at least 8 characters long");
            isValid = false;
        }

        if (dto.getRole() == null || !roleList.contains(Role.valueOf(dto.getRole().name()))) {
            addConstraintViolation(context, "Role must not be null and should be doctor or user");
            isValid = false;
        }

        return isValid;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
