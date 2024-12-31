package com.healthcare.healthcare.service.auth;

import com.healthcare.healthcare.config.JwtTokenProvider;
import com.healthcare.healthcare.dto.request.DoctorRegistrationRequestDTO;
import com.healthcare.healthcare.dto.request.LoginRequestDTO;
import com.healthcare.healthcare.dto.request.RegistrationRequestDTO;
import com.healthcare.healthcare.dto.response.LoginResponseDTO;
import com.healthcare.healthcare.dto.response.RegistrationResponseDTO;
import com.healthcare.healthcare.model.Doctor;
import com.healthcare.healthcare.model.DoctorProficiency;
import com.healthcare.healthcare.model.Proficiency;
import com.healthcare.healthcare.model.Users;
import com.healthcare.healthcare.repo.DoctorProficiencyRepository;
import com.healthcare.healthcare.repo.DoctorRepository;
import com.healthcare.healthcare.repo.ProficiencyRepository;
import com.healthcare.healthcare.repo.UserRepository;
import com.healthcare.healthcare.service.GeminiApiService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ProficiencyRepository proficiencyRepository;

    @Autowired
    private DoctorProficiencyRepository doctorProficiencyRepository;

    @Autowired
    private GeminiApiService geminiApiService;

    @Value("${doctor.registration.redirection.url}")
    private String doctorRegistrationRedirectionUrl;

    @Value("${photo.upload.url}")
    private String photoUploadUrl;

    public ResponseEntity<LoginResponseDTO> getUserAfterLogin(LoginRequestDTO loginRequestDTO) {
        if (loginRequestDTO.token != null && !loginRequestDTO.token.isEmpty()) {
            if (jwtTokenProvider.validateToken(loginRequestDTO.token)) {
                String email = jwtTokenProvider.getUsernameFromToken(loginRequestDTO.token);
                Optional<Users> user = userRepository.findByEmail(email);

                if (user.isPresent()) {
                    return new ResponseEntity<LoginResponseDTO>(LoginResponseDTO.builder()
                            .token(loginRequestDTO.token)
                            .message("Login successful via token")
                            .userId(user.get().getUserId())
                            .role(user.get().getRole().name())
                            .success(true)
                            .build(), HttpStatus.OK);
                }
                return new ResponseEntity<LoginResponseDTO>(LoginResponseDTO.builder()
                        .message("Invalid token: User not found.")
                        .success(false)
                        .build(), HttpStatusCode.valueOf(401));
            }
            return new ResponseEntity<LoginResponseDTO>(LoginResponseDTO.builder()
                    .message("Invalid or expired token.")
                    .success(false)
                    .build(), HttpStatusCode.valueOf(401));
        } else {
            Optional<Users> user = userRepository.findByEmail(loginRequestDTO.email);

            if (user.isPresent() && bCryptPasswordEncoder.matches(loginRequestDTO.password, user.get().getPassword())) {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequestDTO.email, loginRequestDTO.password)
                );

                String token = jwtTokenProvider.generateToken((UserDetails) authentication.getPrincipal());

                return new ResponseEntity<LoginResponseDTO>(LoginResponseDTO.builder()
                        .token(token)
                        .message("Login successful")
                        .userId(user.get().getUserId())
                        .role(user.get().getRole().name())
                        .success(true)
                        .build(), HttpStatus.OK);
            } else {
                return new ResponseEntity<LoginResponseDTO>(LoginResponseDTO.builder()
                        .message("Invalid Credentials")
                        .success(false)
                        .build(), HttpStatusCode.valueOf(401));
            }
        }
    }


    public ResponseEntity<RegistrationResponseDTO> registerNormalUser(RegistrationRequestDTO request) {
        try {

            if(userRepository.findByEmail(request.getEmail()).isPresent()) {
                return new ResponseEntity<RegistrationResponseDTO>(RegistrationResponseDTO
                        .builder()
                        .success(false)
                        .message("User Already registered!")
                        .build(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            Users user = Users.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .userName(request.getUserName())
                    .email(request.getEmail())
                    .password(hashPassword(request.getPassword()))
                    .role(request.getRole())
                    .build();
            userRepository.save(user);
            return new ResponseEntity<RegistrationResponseDTO>(RegistrationResponseDTO
                    .builder()
                    .success(true)
                    .message("User Registration Started.")
                    .userId(user.getUserId())
                    .photoUploadUrl(photoUploadUrl)
                    .build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<RegistrationResponseDTO>(RegistrationResponseDTO
                    .builder()
                    .success(false)
                    .message("User Registration failed. Internal Server Error!")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<RegistrationResponseDTO> startDoctorRegistration(RegistrationRequestDTO request) {
        try {

            if(userRepository.findByEmail(request.getEmail()).isPresent()) {
                return new ResponseEntity<RegistrationResponseDTO>(RegistrationResponseDTO
                        .builder()
                        .success(false)
                        .message("User Already registered!")
                        .build(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            Users user = Users.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .userName(request.getUserName())
                    .email(request.getEmail())
                    .password(hashPassword(request.getPassword()))
                    .role(request.getRole())
                    .build();
            userRepository.save(user);
            return new ResponseEntity<RegistrationResponseDTO>(RegistrationResponseDTO
                    .builder()
                    .success(true)
                    .message("Doctor Registration Started.")
                    .userId(user.getUserId())
                    .doctorRegistrationUrl(doctorRegistrationRedirectionUrl)
                    .build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<RegistrationResponseDTO>(RegistrationResponseDTO
                    .builder()
                    .success(false)
                    .message("Doctor Registration failed. Internal Server Error!")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<RegistrationResponseDTO> completeDoctorRegistration(DoctorRegistrationRequestDTO request) {
        try {
            // Verify certificate with Gemini API
            boolean isVerified = geminiApiService.verifyCertificate(request.getCertificate());
            if (!isVerified) {
                return new ResponseEntity<RegistrationResponseDTO>(RegistrationResponseDTO
                        .builder()
                        .success(false)
                        .message("Doctor Registration failed. Certificate Validation Error!")
                        .build(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            Optional<Users> user = userRepository.findById(request.getUserId());
            if (user.isPresent()) {
                Users doctor = user.get();
                doctor.setBio(request.getBio());
                userRepository.save(doctor);
            }

            // Save Doctor details
            Doctor doctor = Doctor.builder()
                    .userId(request.getUserId())
                    .build();
            doctorRepository.save(doctor);

            // Save Proficiencies
            request.getProficiencies().forEach(proficiencyName -> {
                Proficiency proficiency = proficiencyRepository.findByProficiencyName(proficiencyName)
                        .orElseGet(() -> {
                            Proficiency newProficiency = Proficiency.builder()
                                    .proficiencyName(proficiencyName)
                                    .build();
                            proficiencyRepository.save(newProficiency);
                            return newProficiency;
                        });

                DoctorProficiency doctorProficiency = DoctorProficiency.builder()
                        .doctorId(doctor.getDoctorId())
                        .proficiencyId(proficiency.getProficiencyId())
                        .build();
                doctorProficiencyRepository.save(doctorProficiency);
            });

            return new ResponseEntity<RegistrationResponseDTO>(RegistrationResponseDTO
                    .builder()
                    .success(true)
                    .message("Doctor Registration Success.")
                    .userId(doctor.getUserId())
                    .photoUploadUrl(photoUploadUrl)
                    .build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<RegistrationResponseDTO>(RegistrationResponseDTO
                    .builder()
                    .success(false)
                    .message(e.getLocalizedMessage())
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String hashPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }
}
