package com.healthcare.healthcare.validation.validator;

import com.healthcare.healthcare.dto.request.DoctorRegistrationRequestDTO;
import com.healthcare.healthcare.validation.ValidDoctorRegistrationRequest;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class DoctorRegistrationRequestValidator
        implements ConstraintValidator<ValidDoctorRegistrationRequest, DoctorRegistrationRequestDTO> {

    @Override
    public boolean isValid(DoctorRegistrationRequestDTO dto, ConstraintValidatorContext context) {
        boolean isValid = true;

        if (dto.getUserId() == null) {
            addConstraintViolation(context, "User ID must not be null");
            isValid = false;
        }

        if (dto.getBio() == null || dto.getBio().trim().isEmpty()) {
            addConstraintViolation(context, "Bio must not be empty");
            isValid = false;
        }

        if (dto.getProficiencies() == null || dto.getProficiencies().isEmpty()) {
            addConstraintViolation(context, "At least one proficiency must be provided");
            isValid = false;
        }

        return isValid;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
