package com.sanatoryApp.UserService.repository;

import com.sanatoryApp.UserService.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IPatientRepository extends JpaRepository<Patient,Long> {

    Optional<Patient>findPatientByDni(String dni);
    Optional<Patient>findPatientByEmail(String email);
    boolean existsByDni(String dni);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}