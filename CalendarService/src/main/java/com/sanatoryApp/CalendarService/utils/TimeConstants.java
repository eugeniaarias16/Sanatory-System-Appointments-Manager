package com.sanatoryApp.CalendarService.utils;

import java.time.LocalTime;

public final class TimeConstants {
    private TimeConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    public static final LocalTime START_OF_DAY=LocalTime.of(0,0,0);
    public static final LocalTime END_OF_DAY=LocalTime.of(23,59,59);
}
