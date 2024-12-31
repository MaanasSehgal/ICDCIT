package com.healthcare.healthcare.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    private String token;
    private String message;
    private Long userId;
    private String role;
    private Boolean success;
}

