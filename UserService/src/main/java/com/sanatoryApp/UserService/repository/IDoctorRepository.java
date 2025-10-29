package com.sanatoryApp.UserService.repository;

import com.sanatoryApp.UserService.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface IDoctorRepository extends JpaRepository<Doctor,Long> {
    List<Doctor> findDoctorByFirstNameIgnoreCase(String firstName);
    List<Doctor> findDoctorByLastNameIgnoreCase(String lastName);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}