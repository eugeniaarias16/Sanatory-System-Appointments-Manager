package com.sanatoryApp.AppointmentService.entity;



public enum AppointmentStatus {
    SCHEDULED("Scheduled"),
    CONFIRMED("Confirmed"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed");

    private final String description;
    AppointmentStatus(String description){
        this.description=description;
    }
    public String getDescription(){
        return description;
    }

    public static  AppointmentStatus fromString(String status){
        for (AppointmentStatus appointmentStatus: AppointmentStatus.values()){
            if (appointmentStatus.name().equalsIgnoreCase(status)){
                return appointmentStatus;
            }
        }
        throw new IllegalArgumentException("Invalid appointment status "+status);
    }

}
