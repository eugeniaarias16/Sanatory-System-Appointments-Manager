package com.sanatoryApp.shared_security.service;

import com.sanatoryApp.shared_security.model.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("securityService")
public class SecurityService {

    //Check whether it is SECRETARY or whether the userID matches the one requested.

    public boolean isSecretaryOrSelf(Long requestedId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (hasRole(auth, "ROLE_SECRETARY")) {
            return true;
        }

        Long currentUserId = getCurrentUserId(auth);
        return currentUserId != null && currentUserId.equals(requestedId);
    }

    //Check whether they are a SECRETARY or a DOCTOR by accessing their own data.

    public boolean isSecretaryOrDoctor(Long doctorId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (hasRole(auth, "ROLE_SECRETARY")) {
            return true;
        }

        if (hasRole(auth, "ROLE_DOCTOR")) {
            Long currentUserId = getCurrentUserId(auth);
            return currentUserId != null && currentUserId.equals(doctorId);
        }

        return false;
    }

    //Check whether it is SECRETARY or PATIENT by accessing their own data.

    public boolean isSecretaryOrPatient(Long patientId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (hasRole(auth, "ROLE_SECRETARY")) {
            return true;
        }

        if (hasRole(auth, "ROLE_PATIENT")) {
            Long currentUserId = getCurrentUserId(auth);
            return currentUserId != null && currentUserId.equals(patientId);
        }

        return false;
    }

    private boolean hasRole(Authentication auth, String role) {
        if (auth == null || auth.getAuthorities() == null) {
            return false;
        }
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }

    private Long getCurrentUserId(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUserId();
        }
        return null;
    }
}
