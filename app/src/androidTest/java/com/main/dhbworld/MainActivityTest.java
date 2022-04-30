package com.main.dhbworld;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import android.content.Context;
import android.content.SharedPreferences;


import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class MainActivityTest {



    public static final String MyPREFERENCES = "myPreferencesKey" ;
    public static final String Name = "nameKey";
    public static final String LibraryNumber = "libraryNumberKey";
    public static final String MatriculationNumber = "matriculationNumberKey";
    public static final String StudentMail= "studentMailKey";
    public static final String FreeNotes = "freeNotesKey";
    SharedPreferences  sp;


    @Test
    public void test_saveData(){
        Context context = InstrumentationRegistry.getContext();


        SharedPreferences mSharedPreferences = context.getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putString(Name, "Max Mustemann");
        editor.putString(LibraryNumber, "123456789012");
        editor.putString(MatriculationNumber, "909090");
        editor.putString(StudentMail, "mustemann.max@student.dhbw-karlsruhe.de");
        editor.putString(FreeNotes, "etwas");
        editor.apply();

        SharedPreferences mSharedPreferences2 = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        assertThat("Max Mustemann", is(mSharedPreferences2.getString(Name, null)));
        assertThat("123456789012", is(mSharedPreferences2.getString(LibraryNumber, null)));
        assertThat("909090", is(mSharedPreferences2.getString(MatriculationNumber, null)));
        assertThat("mustemann.max@student.dhbw-karlsruhe.de", is(mSharedPreferences2.getString(StudentMail, null)));
        assertThat("etwas", is(mSharedPreferences2.getString(FreeNotes, null)));





    }

    @Test
    public void control_matriculationNumber(){

        Boolean mistake=false;
        Context context = InstrumentationRegistry.getContext();
        SharedPreferences mSharedPreferences2 = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String mNumber = mSharedPreferences2.getString(MatriculationNumber, null);


        if((mNumber!=null) && (!mNumber.equals(""))){
            try{
                long matriculationNumberLength = Long.parseLong(mNumber);
                if(matriculationNumberLength>9999999){ //Can only contain 7 digits"
                    mistake=true;
                }
            }catch (NumberFormatException g){
                mistake=true;
            }
        }
        Assert.assertNotEquals(true, mistake);

    }

    @Test
    public void control_libraryNumber(){
        Boolean mistake=false;
        Context context = InstrumentationRegistry.getContext();
        SharedPreferences mSharedPreferences2 = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String lNumber = mSharedPreferences2.getString(LibraryNumber, null);

        if((lNumber!=null) && (!lNumber.equals(""))){
            try{
                long libraryNumberLength = Long.parseLong(lNumber);
                if(libraryNumberLength>999999999999l){ //Can only contain 12 digits

                   mistake=true;
                }
            }catch(NumberFormatException g){
                mistake=true;
            }
        }

        Assert.assertNotEquals(true, mistake);
    }

}