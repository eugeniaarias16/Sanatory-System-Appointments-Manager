package com.sanatoryApp.AppointmentService.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculateAmountToPay {
   public static BigDecimal calculateAmountToPay(BigDecimal cost, BigDecimal coverPercentage) {
        BigDecimal coverPer = coverPercentage.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        BigDecimal coverAmount = cost.multiply(coverPer);
        BigDecimal amountToPay = cost.subtract(coverAmount);
        return amountToPay.setScale(2, RoundingMode.HALF_UP);
    }
}
