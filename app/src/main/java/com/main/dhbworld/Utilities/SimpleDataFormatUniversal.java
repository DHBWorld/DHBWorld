package com.main.dhbworld.Utilities;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class SimpleDataFormatUniversal extends SimpleDateFormat {
   public SimpleDataFormatUniversal(){
       super("dd.MM.yyyy", Locale.GERMANY);
   }
}
