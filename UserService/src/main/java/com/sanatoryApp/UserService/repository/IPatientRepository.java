package com.sanatoryApp.UserService.repository;

import com.sanatoryApp.UserService.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IPatientRepository extends JpaRepository<Patient,Long> {

    Optional<Patient>findPatientByDni(String dni);
    Optional<Patient>findPatientByEmail(String email);
    boolean existsByDni(String dni);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    @Modifying
    @Query( "UPDATE Patient p "+
            "SET p.enabled=false "+
            "WHERE p.dni=:dni ")
    void disablePatientByDni(@Param("dni") String dni);

    @Modifying
    @Query( "UPDATE Patient p "+
            "SET p.enabled=true "+
            "WHERE p.dni=:dni")
    void enablePatientByDni(@Param("dni") String dni);
}