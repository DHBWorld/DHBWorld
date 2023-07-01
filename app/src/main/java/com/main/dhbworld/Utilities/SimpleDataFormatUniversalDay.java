package com.main.dhbworld.Utilities;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class SimpleDataFormatUniversalDay extends SimpleDateFormat {
   public SimpleDataFormatUniversalDay(){
       super("dd.MM.yyyy", Locale.GERMANY);
   }
}
