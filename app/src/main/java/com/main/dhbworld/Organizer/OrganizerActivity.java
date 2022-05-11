package com.main.dhbworld.Organizer;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.main.dhbworld.CalendarActivity;
import com.main.dhbworld.Navigation.NavigationUtilities;
import com.main.dhbworld.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class OrganizerActivity extends AppCompatActivity {
    private ArrayList<Course> courses = new ArrayList<>();
    private Course course;
    private String text;
    InputStream in;
    organizerListAdapter adapter;
    ListView listView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_layout);
        NavigationUtilities.setUpNavigation(this, R.id.navigationView);
        listView = findViewById(R.id.listviewitem);
        t.start();
        parseXml.start();
    }

    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL("https://rapla.dhbw-karlsruhe.de/rapla?key=2llRzrjV9Yj0yY4JKsO9cneRD8XIxxCqFeg5tRpzABg");
                in = url.openStream();
                System.out.println(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }});



    Thread parseXml = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                t.join();
                System.out.println(in);
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(in, null);

                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String tag = parser.getName();
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            if (tag.equalsIgnoreCase("kurs")) {
                                course = new Course();
                            }
                            break;
                        case XmlPullParser.TEXT:
                            text = parser.getText();
                            break;
                        case XmlPullParser.END_TAG:
                            if (tag.equalsIgnoreCase("kurs")) {
                                courses.add(course);
                            } else if (tag.equalsIgnoreCase("name")) {
                                course.setName(text);
                            } else if (tag.equalsIgnoreCase("jahrgang")) {
                                course.setYear(Integer.parseInt(text));
                            } else if (tag.equalsIgnoreCase("studiengang")) {
                                course.setStudy(text);
                            }
//                        else if (tag.equalsIgnoreCase("raumnr")) {
//                            course.setRoomNo(text);
//                        }
                        default:
                            break;
                    }
                    eventType = parser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            displayCourses();
        }
    });





        public void displayCourses() {
            adapter = new organizerListAdapter(this, courses);
            ListView listView = findViewById(R.id.listviewitem);
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listView.setAdapter(adapter);
                }
            });
            }

}