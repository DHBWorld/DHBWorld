package com.main.dhbworld.Calendar;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class CalenderSaver {
    public static void saveCalender(Context context, final String courseNameParam, final String courseDirector, final String urlStringParam) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Map<String, Object> courseInFirestore = new HashMap<>();

        new Thread(() -> {
            String courseName = courseNameParam;
            String urlString = urlStringParam;

            if(!courseName.isEmpty() && !urlString.isEmpty()){
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("CurrentURL", urlString);
                editor.apply();
                courseInFirestore.put("URL", urlString);
            }
            else if(urlString.isEmpty() && !courseName.isEmpty() && !courseDirector.isEmpty()){
                urlString = ("https://rapla.dhbw-karlsruhe.de/rapla?page=calendar&user=" + courseDirector + "&file=" + courseName);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("CurrentURL", urlString);
                editor.apply();
                courseInFirestore.put("CourseDirector", courseDirector.toUpperCase().charAt(0)+courseDirector.substring(1).toLowerCase());
                courseInFirestore.put("URL", urlString);
            }
            else if(!urlString.isEmpty()) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("CurrentURL", urlString);
                editor.apply();
                String partURLfile=urlString.substring(urlString.indexOf("file=")+5);
                if (partURLfile.contains("&")){
                    courseName=partURLfile.substring(0, partURLfile.indexOf("&"));
                } else {
                    courseName=partURLfile;
                }
                courseInFirestore.put("URL", urlString);
            }
            try {
                URL urlCheck = new URL(urlString);
                HttpsURLConnection connection = (HttpsURLConnection) urlCheck.openConnection();
                if (connection.getResponseCode() == 200) {
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore.collection("Courses").document(courseName.toLowerCase()).set(courseInFirestore, SetOptions.merge());
                }
            } catch (IOException | IllegalArgumentException ignored) {}
        }).start();
    }
}
