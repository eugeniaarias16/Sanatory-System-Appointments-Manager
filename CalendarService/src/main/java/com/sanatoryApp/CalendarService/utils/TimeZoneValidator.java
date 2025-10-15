package com.sanatoryApp.CalendarService.utils;

import java.time.DateTimeException;
import java.time.ZoneId;

public final class TimeZoneValidator {
    private TimeZoneValidator(){
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void validateTimeZone(String timeZone){
        try{
            ZoneId.of(timeZone);
        }catch (DateTimeException ex){
            throw new IllegalArgumentException(
                    "Invalid time zone: " + timeZone +
                            ". Use zones like 'America/Argentina/Buenos_Aires', 'UTC', etc."
            );
        }
    }
}
