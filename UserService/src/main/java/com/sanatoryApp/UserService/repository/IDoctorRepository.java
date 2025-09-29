package com.sanatoryApp.UserService.repository;

import com.sanatoryApp.UserService.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IDoctorRepository extends JpaRepository<Doctor,Long> {
    Optional<List<Doctor>> findDoctorByFirstNameIgnoreCase(String firstName);
    Optional<List<Doctor>> findDoctorByLastNameIgnoreCase(String lastName);
    boolean existByEmail(String email);
    boolean existByPhoneNumber(String phoneNumber);
}
