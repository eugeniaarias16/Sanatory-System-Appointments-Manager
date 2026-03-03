package com.sanatoryApp.CalendarService.entity;

/**
 * Defines the scope/reach of a CalendarException
 */
public enum ExceptionScope {
    /**
     * Applies to ALL calendars of ALL doctors in the system
     * Example: Christmas, New Year, National Holidays
     * - doctorCalendar: NULL
     * - doctorId: NULL
     */
    GLOBAL,

    /**
     * Applies to ALL calendars of a SPECIFIC doctor
     * Example: Doctor vacation, Doctor conference
     * - doctorCalendar: NULL
     * - doctorId: NOT NULL
     */
    SEMI_GLOBAL,

    /**
     * Applies to ONE specific calendar only
     * Example: Clinic closure, Equipment maintenance at specific location
     * - doctorCalendar: NOT NULL
     * - doctorId: doctorCalendar.getDoctorId()
     */
    SPECIFIC
}
