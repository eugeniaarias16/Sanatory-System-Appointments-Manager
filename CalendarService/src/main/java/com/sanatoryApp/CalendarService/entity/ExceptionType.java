package com.sanatoryApp.CalendarService.entity;

public enum ExceptionType {
    UNAVAILABLE("Unavailable"), //Generic - not available without specifying reason
    VACATION("Vacation"), //Planned vacation time
    SICK_LEAVE("Sick Leave"), //Doctor is ill
    HOLIDAY("Holiday"), //National/local holidays
    CONFERENCE("Conference/Training"), //Medical events, congresses, training
    EMERGENCY("Emergency"),//Family/personal emergencies
    PERSONAL("Personal Reason"),//Personal matters
    MAINTENANCE("Office Maintenance"),//Office repairs, relocations
    CUSTOM("Custom");//Special cases with custom description

    private final String description;
    ExceptionType(String description){
        this.description=description;
    }
    public String getDescription(){
        return description;
    }
}
