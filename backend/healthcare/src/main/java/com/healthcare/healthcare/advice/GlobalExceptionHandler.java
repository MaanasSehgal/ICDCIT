package com.healthcare.healthcare.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();

        // Collecting all validation error messages
        System.out.println("ex: " + ex.getBindingResult().getAllErrors());
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            System.out.println("Error:" + error);
            errors.add(error.getDefaultMessage());
        });

        // Building the response
        Map<String, Object> response = Map.of(
                "success", false,
                "errors", errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
