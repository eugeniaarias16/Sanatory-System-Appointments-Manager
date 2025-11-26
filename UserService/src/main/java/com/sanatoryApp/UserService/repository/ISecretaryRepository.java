package com.sanatoryApp.UserService.repository;

import com.sanatoryApp.UserService.entity.Secretary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ISecretaryRepository extends JpaRepository<Secretary,Long> {
    Optional<Secretary> findSecretaryByEmail(String email);
    Optional<Secretary> findSecretaryByDni(String dni);
    boolean existsByEmail(String email);
    boolean existsByDni(String dni);

    @Modifying
    @Query("UPDATE Secretary s " +
            "SET s.enabled = false " +
            "WHERE s.dni = :dni")
    void disableSecretaryByDni(@Param("dni") String dni);

    @Modifying
    @Query("UPDATE Secretary s " +
            "SET s.enabled = true " +
            "WHERE s.dni = :dni")
    void enableSecretaryByDni(@Param("dni") String dni);
}