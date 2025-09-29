package com.sanatoryApp.HealthInsuranceService.exception;

public class BadRequest extends RuntimeException {
    public BadRequest(String message) {
        super(message);
    }
}
