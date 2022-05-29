package com.main.dhbworld.Cantine;

import java.util.Calendar;
import java.util.Date;

public class CantineUtilities {

    public static Date[] generateCurrentWeek()  {
        Date date= new Date();
        Date[] currentWeek = new Date[5];
        Calendar c= Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        c.setTime(date);
        Integer dayOfWeek = c.get(Calendar.DAY_OF_WEEK); // dayOfWeek= 2 ->Mo
        dayOfWeek=dayOfWeek-2; // dayOfWeek= 0 ->Mo
        // Am Samstag
        if ( (dayOfWeek>4)){
            dayOfWeek=0;
            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            c.add(Calendar.DATE, 7);
        }
        //Am Sonntag
        if ((dayOfWeek<0) ){
            dayOfWeek=0;
            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        }
        for (int q=dayOfWeek; q<5;q++){
            currentWeek[q]=c.getTime();
            c.add(Calendar.DATE, 1);
        }
        c.add(Calendar.DATE, 2);
        for (int q=0; q<dayOfWeek;q++){
            currentWeek[q]=c.getTime();
            c.add(Calendar.DATE, 1);
        }

        return currentWeek;
    }

}
