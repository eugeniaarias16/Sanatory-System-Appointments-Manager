package com.sanatoryApp.CalendarService.utils;

import com.sanatoryApp.CalendarService.exception.InvalidTimeRangeException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;

@Slf4j
public final class TimeValidationUtils {

    // Private constructor to avoid instantiation
    private TimeValidationUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }


    //Validate that the time range is valid
    /*
     * Rules:
     * - If both are null: valid (full day exception)
     * - If only one is null: invalid (must be both or neither)
     * - If both are present: endTime must be after startTime
     * */
    public static void validateTimeRange(LocalTime startTime, LocalTime endTime){
        log.debug("Validating time range: start={}, end={}", startTime, endTime);
        if(startTime==null && endTime==null){
            log.debug("No time range specified - full day exception");
            return;
        }

        if (startTime == null || endTime == null) {
            log.error("Partial time range provided: start={}, end={}", startTime, endTime);
            throw new InvalidTimeRangeException(
                    "Both start time and end time must be provided, or both must be null for full-day exceptions. " +
                            "Got start=" + startTime + ", end=" + endTime
            );
        }

        log.debug("Validating if end time is after start time...");
        if(endTime.isBefore(startTime)|| endTime.equals(startTime)){
            throw new InvalidTimeRangeException("End time must be after start time.");
        }
        log.debug("End time is correct.");
    }

    //Verify that the range represents a full day (without specific times).
    public static boolean isFullDayRange(LocalTime startTime,LocalTime endTime){
        return startTime==null && endTime==null;
    }
}
