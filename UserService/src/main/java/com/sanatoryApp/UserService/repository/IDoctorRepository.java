package com.sanatoryApp.UserService.repository;

import com.sanatoryApp.UserService.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface IDoctorRepository extends JpaRepository<Doctor,Long> {
    List<Doctor> findDoctorByFirstNameIgnoreCase(String firstName);
    List<Doctor> findDoctorByLastNameIgnoreCase(String lastName);
    Optional<Doctor> findDoctorByDni(String dni);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByDni(String dni);

    @Modifying
    @Query("UPDATE Doctor d "+
            "SET d.enabled=false "+
            "WHERE d.dni=:dni ")
    void disableDoctorByDni(@Param("dni") String dni);

    @Modifying
    @Query("UPDATE Doctor d "+
            "SET d.enabled=true "+
            "WHERE d.dni=:dni ")
    void enableDoctorByDni(@Param("dni") String dni);

    Optional<Doctor> findByEmail(String email);
}