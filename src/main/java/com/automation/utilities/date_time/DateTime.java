package com.automation.utilities.date_time;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTime {

    public String getDateTimeOfChosenFormat(String formatPattern){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(formatPattern);
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
}
