package com.healthcare.healthcare.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.healthcare.dto.request.DoctorRegistrationRequestDTO;
import com.healthcare.healthcare.dto.request.LoginRequestDTO;
import com.healthcare.healthcare.dto.request.RegistrationRequestDTO;
import com.healthcare.healthcare.dto.response.LoginResponseDTO;
import com.healthcare.healthcare.dto.response.RegistrationResponseDTO;
import com.healthcare.healthcare.eum.Role;
import com.healthcare.healthcare.service.UserService;
import com.healthcare.healthcare.service.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO loginResponseDTO;
        try {
            return authService.getUserAfterLogin(loginRequestDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid RegistrationRequestDTO request) {
        ResponseEntity<RegistrationResponseDTO> response;
        if (request.getRole() == Role.USER) {
            response = authService.registerNormalUser(request);
        } else if (request.getRole() == Role.DOCTOR) {
            response = authService.startDoctorRegistration(request);
        } else {
            return new ResponseEntity<RegistrationResponseDTO>(RegistrationResponseDTO
                    .builder().message("Invalid Role Selected.").success(false).build(), HttpStatus.OK);
        }
        return response;
    }

    @PostMapping("register/doctor")
    public ResponseEntity<?> completeDoctorRegistration(@RequestParam("doctorRegistrationBody") String doctorRegistrationBody, @RequestParam("certificate") MultipartFile certificate) throws JsonProcessingException {

        try {
            List<String> ALLOWED_FILE_TYPES = Arrays.asList("image/jpeg", "image/jpg", "image/png");
            long MAX_FILE_SIZE = 5 * 1024 * 1024;

            @Valid
            DoctorRegistrationRequestDTO doctorRegistrationRequestDTO = null;

            if (!doctorRegistrationBody.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                doctorRegistrationRequestDTO = objectMapper.readValue(doctorRegistrationBody, DoctorRegistrationRequestDTO.class);
            }
            // Validate MultipartFile
            if (certificate == null || certificate.isEmpty()) {
                return ResponseEntity.badRequest().body("Profile photo must not be empty");
            }

            if (!ALLOWED_FILE_TYPES.contains(certificate.getContentType())) {
                return ResponseEntity.badRequest().body("Profile photo must be a valid image (JPG, JPEG, or PNG)");
            }

            if (certificate.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.badRequest().body("Profile photo must not exceed 5MB");
            }

            doctorRegistrationRequestDTO.setCertificate(certificate);
            System.out.println("DTO: " + doctorRegistrationRequestDTO);
            return authService.completeDoctorRegistration(doctorRegistrationRequestDTO);

        } catch (Exception e) {
            return new ResponseEntity<>(RegistrationResponseDTO
                    .builder()
                    .success(false)
                    .message(e.getLocalizedMessage())
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/upload-profile-photo/{userId}")
    public ResponseEntity<?> uploadProfilePhoto(
            @PathVariable Long userId,
            @RequestParam("profilePhoto") MultipartFile profilePhoto) {
        List<String> ALLOWED_FILE_TYPES = Arrays.asList("image/jpeg", "image/jpg", "image/png");
        long MAX_FILE_SIZE = 5 * 1024 * 1024;
        // Validate MultipartFile
        if (profilePhoto == null || profilePhoto.isEmpty()) {
            return ResponseEntity.badRequest().body("Profile photo must not be empty");
        }

        if (!ALLOWED_FILE_TYPES.contains(profilePhoto.getContentType())) {
            return ResponseEntity.badRequest().body("Profile photo must be a valid image (JPG, JPEG, or PNG)");
        }

        if (profilePhoto.getSize() > MAX_FILE_SIZE) {
            return ResponseEntity.badRequest().body("Profile photo must not exceed 5MB");
        }

        try {
            // Save the profile photo
            userService.saveProfilePhoto(userId, profilePhoto);
            return ResponseEntity.ok("Profile photo uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload profile photo: " + e.getMessage());
        }
    }

}
