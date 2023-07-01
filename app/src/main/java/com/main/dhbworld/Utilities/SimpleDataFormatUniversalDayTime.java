package com.main.dhbworld.Utilities;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class SimpleDataFormatUniversalDayTime extends SimpleDateFormat {
    public SimpleDataFormatUniversalDayTime(){
        super("dd.MM.yyyy hh:mm", Locale.GERMANY);
    }
}
