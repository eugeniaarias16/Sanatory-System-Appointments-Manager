package com.sanatoryApp.AppointmentService.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class CountWorkingDay {

   //We get the end date based on a period of working days, excluding Saturdays and Sundays.

    public static LocalDate getEndDate(LocalDate startDate, int workingDays){
        LocalDate endDate=startDate;
        int countDays=0;

        while(countDays<workingDays){
            endDate=endDate.plusDays(1);

            DayOfWeek dayOfWeek=endDate.getDayOfWeek();
            if(dayOfWeek!=DayOfWeek.SATURDAY && dayOfWeek!=DayOfWeek.SUNDAY){
                countDays++;
            }
        }

        return endDate;
    }
}
