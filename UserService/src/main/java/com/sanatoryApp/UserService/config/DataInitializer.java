package com.sanatoryApp.UserService.config;

import com.sanatoryApp.UserService.entity.Secretary;
import com.sanatoryApp.UserService.repository.ISecretaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class DataInitializer implements CommandLineRunner {

    private final ISecretaryRepository secretaryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initializeDefaultSecretary();
    }

    /* Create a default secretary if none exists. */
    private void initializeDefaultSecretary(){
        if(secretaryRepository.count()>0){
            log.info("Secretaries already exist. Skipping default secretary creation.");
            return;
        }

        //Create default Secretary
        Secretary secretary = new Secretary(
                null,                    // id (null porque se autogenera)
                "00000000",              // dni
                "Admin",                 // firstName
                "Secretary",             // lastName
                "admin@sanatory.com",    // email
                passwordEncoder.encode("00000000"),  // password hasheado
                true,                    // enabled
                false,                    // credentialsExpired
                false                    // accountLocked

        );
        secretaryRepository.save(secretary);

        // Confirmation log
        log.warn("╔══════════════════════════════════════════════════════════════╗");
        log.warn("║           DEFAULT SECRETARY CREATED                          ║");
        log.warn("║──────────────────────────────────────────────────────────────║");
        log.warn("║  Email:    admin@sanatory.com                                ║");
        log.warn("║  Password: 00000000                                          ║");
        log.warn("║──────────────────────────────────────────────────────────────║");
        log.warn("║  ⚠️  CHANGE PASSWORD IMMEDIATELY AFTER FIRST LOGIN           ║");
        log.warn("╚══════════════════════════════════════════════════════════════╝");
    }


}
