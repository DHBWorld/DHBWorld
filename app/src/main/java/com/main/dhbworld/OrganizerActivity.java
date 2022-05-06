package com.main.dhbworld;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.main.dhbworld.Navigation.NavigationUtilities;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OrganizerActivity extends AppCompatActivity {
    private ArrayList<Course> courses = new ArrayList<>();



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.schedule_layout);
        NavigationUtilities.setUpNavigation(this, R.id.Calendar);
    }

    public List parse() throws Exception {
        URL url = new URL("https://rapla.dhbw-karlsruhe.de/rapla?key=2llRzrjV9Yj0yY4JKsO9cneRD8XIxxCqFeg5tRpzABg");

        InputStream in = url.openStream();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(in, null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tag = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tag.equalsIgnoreCase("kurs")) {
                            Course course = new Course();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (tag.equalsIgnoreCase("kurs")) {
                            courses.add(course);
                        }

                }
            }
        } finally {
            in.close();
        }
        return x;
    }




//
//    public static String convertStreamToString(InputStream is) throws Exception {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//        StringBuilder sb = new StringBuilder();
//        String line = null;
//        while ((line = reader.readLine()) != null) {
//            sb.append(line).append("\n");
//        }
//        reader.close();
//        return sb.toString();
//    }
//
//
//    public static String getStringFromFile (String filePath) throws Exception {
//        File fl = new File(filePath);
//        FileInputStream fin = new FileInputStream(fl);
//        String ret = convertStreamToString(fin);
//        //Make sure you close all streams.
//        fin.close();
//        return ret;
//    }
}
