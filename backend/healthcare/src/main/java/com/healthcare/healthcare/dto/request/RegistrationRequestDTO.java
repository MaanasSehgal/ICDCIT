package com.healthcare.healthcare.dto.request;

import com.healthcare.healthcare.eum.Role;
import com.healthcare.healthcare.validation.ValidRegistrationRequest;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidRegistrationRequest
public class RegistrationRequestDTO {
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String password;
    private Role role;
}

