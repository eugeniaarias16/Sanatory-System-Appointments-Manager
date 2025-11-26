package com.sanatoryApp.UserService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "doctors")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String dni;
    @Column(unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private boolean enabled=true;
    @Column(nullable = false)
    private boolean credentialsExpired=false;
    @Column(nullable = false)
    private boolean accountLocked=false;
}
