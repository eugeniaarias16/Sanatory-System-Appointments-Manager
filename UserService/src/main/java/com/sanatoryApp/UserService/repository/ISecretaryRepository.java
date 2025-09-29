package com.sanatoryApp.UserService.repository;

import com.sanatoryApp.UserService.entity.Secretary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ISecretaryRepository extends JpaRepository<Secretary,Long> {
    Optional<Secretary> findSecretaryByEmail(String email);
    boolean existsByEmail(String email);
}
