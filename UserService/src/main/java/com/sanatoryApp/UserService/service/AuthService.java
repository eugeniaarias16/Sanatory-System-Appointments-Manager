package com.sanatoryApp.UserService.service;

import com.sanatoryApp.UserService.dto.Request.ValidateCredentialsRequest;
import com.sanatoryApp.UserService.dto.Response.ValidateCredentialsResponse;
import com.sanatoryApp.UserService.entity.Doctor;
import com.sanatoryApp.UserService.entity.Patient;
import com.sanatoryApp.UserService.entity.Secretary;
import com.sanatoryApp.UserService.repository.IDoctorRepository;
import com.sanatoryApp.UserService.repository.IPatientRepository;
import com.sanatoryApp.UserService.repository.ISecretaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final IPatientRepository patientRepository;
    private final IDoctorRepository doctorRepository;
    private final ISecretaryRepository secretaryRepository;
    private final PasswordEncoder passwordEncoder;

    public ValidateCredentialsResponse validateCredentials(ValidateCredentialsRequest request){
        String email= request.username();
        String rawPassword= request.password();

        log.debug("Attempting to validate credentials for email {}", email);
        Optional<Patient>patient= patientRepository.findPatientByEmail(email);
        if(patient.isPresent()){
            return validateUser(
                    patient.get().getId(),
                    patient.get().getEmail(),
                    patient.get().getPassword(),
                    patient.get().isEnabled(),
                    patient.get().isAccountLocked(),
                    patient.get().isCredentialsExpired(),
                    rawPassword,
                    "ROLE_PATIENT"
            );
        }
        // Search in Doctor
        Optional<Doctor> doctor = doctorRepository.findByEmail(email);
        if (doctor.isPresent()) {
            return validateUser(
                    doctor.get().getId(),
                    doctor.get().getEmail(),
                    doctor.get().getPassword(),
                    doctor.get().isEnabled(),
                    doctor.get().isAccountLocked(),
                    doctor.get().isCredentialsExpired(),
                    rawPassword,
                    "ROLE_DOCTOR"
            );
        }

        // Search in Secretary
        Optional<Secretary> secretary = secretaryRepository.findSecretaryByEmail(email);
        if (secretary.isPresent()) {
            return validateUser(
                    secretary.get().getId(),
                    secretary.get().getEmail(),
                    secretary.get().getPassword(),
                    secretary.get().isEnabled(),
                    secretary.get().isAccountLocked(),
                    secretary.get().isCredentialsExpired(),
                    rawPassword,
                    "ROLE_SECRETARY"
            );
        }

        //User not found in any table
        log.warn("User not found with email: {}", email);
        return ValidateCredentialsResponse.invalid();
    }


    private ValidateCredentialsResponse validateUser(
            Long userId,
            String email,
            String hashedPassword,
            boolean enabled,
            boolean accountLocked,
            boolean accountExpired,
            String rawPassword,
            String role
    ){
        // Check account status
        if (!enabled) {
            log.warn("Account disabled for email: {}", email);
            return ValidateCredentialsResponse.invalid();
        }

        if (accountLocked) {
            log.warn("Account locked for email: {}", email);
            return ValidateCredentialsResponse.invalid();
        }

        if (accountExpired) {
            log.warn("Account expired for email: {}", email);
            return ValidateCredentialsResponse.invalid();
        }

        // Validate password with BCrypt
        if (!passwordEncoder.matches(rawPassword, hashedPassword)) {
            log.warn("Invalid password for email: {}", email);
            return ValidateCredentialsResponse.invalid();
        }

        log.info("Successfully validated credentials for email: {}", email);
        return new ValidateCredentialsResponse(
                true,
                userId,
                email,
                List.of(role)  //Role according to the table
        );
    }
}





