package com.healthcare.healthcare.dto.request;

import com.healthcare.healthcare.validation.ValidDoctorRegistrationRequest;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidDoctorRegistrationRequest
public class DoctorRegistrationRequestDTO {
    private Long userId;
    private MultipartFile certificate;
    private String bio;
    private List<String> proficiencies;
}

