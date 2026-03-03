package com.sanatoryApp.CalendarService.utils;

import com.sanatoryApp.CalendarService.entity.ExceptionScope;
import com.sanatoryApp.CalendarService.entity.ExceptionType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ValidateDtoFields {

     public static void validateScopeConsistency(ExceptionScope scope, Long doctorId, Long doctorCalendarId) {
        switch (scope) {
            case GLOBAL:
                if (doctorId != null || doctorCalendarId != null) {
                    throw new IllegalArgumentException(
                            "GLOBAL exceptions must not have doctorId or doctorCalendarId. Both must be null."
                    );
                }
                break;
            case SEMI_GLOBAL:
                if (doctorId == null) {
                    throw new IllegalArgumentException(
                            "SEMI_GLOBAL exceptions require doctorId"
                    );
                }
                if (doctorCalendarId != null) {
                    throw new IllegalArgumentException(
                            "SEMI_GLOBAL exceptions must not have doctorCalendarId. It must be null."
                    );
                }
                break;
            case SPECIFIC:
                if (doctorCalendarId == null) {
                    throw new IllegalArgumentException(
                            "SPECIFIC exceptions require doctorCalendarId"
                    );
                }
                break;
        }
    }

    public static void validateExceptionTypeAndReason(ExceptionType exceptionType, String reason) {
        if (exceptionType == ExceptionType.CUSTOM) {
            if (reason == null || reason.trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "When exception type is CUSTOM, a reason must be provided"
                );
            }
        }

        log.debug("Exception type {} validated successfully", exceptionType);
    }
}
