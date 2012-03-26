package com.monkeylabs.morningcookie;

public class Helper {
    public static String ampmChanger(String time) {
        int hour = Integer.parseInt(time.substring(0,2));
        String timeAppendage;
        
        if (hour > 12) { // For 1 PM and on
          hour -= 12; 
          timeAppendage = " PM"; 
        }
        else if (hour == 12) timeAppendage = " PM";
        else timeAppendage = " AM";

        
        return String.format("%02d", hour) + time.substring(2) + timeAppendage;
    }
}
