package com.main.dhbworld.Organizer;

import android.content.Context;

import com.main.dhbworld.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrganizerParser {
    static Course course;
    static Person person;
    static Room room;
    static ArrayList<Course> courses = new ArrayList<>();
    static ArrayList<Person> people = new ArrayList<>();
    static ArrayList<Room> rooms = new ArrayList<>();
    static Map<String, ArrayList> entryMap = new HashMap<>();
    static String text;
    static XmlPullParser parser;

    static ArrayList<String> courseStrings = new ArrayList<>();
    static ArrayList<String> personStrings = new ArrayList<>();
    static ArrayList<String> roomStrings = new ArrayList<>();

    public Map<String, ArrayList> getAllElements(Context context){
        try {
            URL url = new URL(context.getString(R.string.organizerURL));
            InputStream inputStream = url.openStream();
            parse(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entryMap;
    }

    public void parse(InputStream inputStream) throws Exception {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        parser = factory.newPullParser();
        parser.setInput(inputStream, null);

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tag = parser.getName();
            if(eventType == XmlPullParser.START_TAG) {
                switch (tag) {
                    case "kurs":
                        parseCourse(eventType);
                        break;
                    case "person":
                        parsePerson(eventType);
                        break;
                    case "raum":
                        parseRoom(eventType);
                        break;
                }
            }
            eventType = parser.next();
        }
        createMap();
    }

    public void parseCourse(int eventType) throws Exception {
        course = new Course();
        boolean stop = false;
        while(!stop) {
            String tag = parser.getName();
            switch (eventType) {
                case XmlPullParser.TEXT:
                    text = parser.getText();
                    break;
                case XmlPullParser.END_TAG:
                    if (tag.equalsIgnoreCase("name")) {
                        course.setName(text);
                    } else if (tag.equalsIgnoreCase("jahrgang")) {
                        course.setYear(Integer.parseInt(text));
                    } else if (tag.equalsIgnoreCase("studiengang")) {
                        course.setStudy(text);
                    } else if (tag.equalsIgnoreCase("resourceURL")){
                        course.setUrl(text);
                    } else if (tag.equalsIgnoreCase("kurs")) {
                        if(!courseStrings.contains(course.filterString())){
                            courseStrings.add(course.filterString());
                            courses.add(course);
                        }
                        stop = true;
                    }
                default:
                    break;
            }
            eventType = parser.next();
        }
    }

    public void parsePerson(int eventType) throws Exception{
        person = new Person();
        boolean stop = false;
        while(!stop) {
            String tag = parser.getName();
            switch (eventType) {
                case XmlPullParser.TEXT:
                    text = parser.getText();
                    break;
                case XmlPullParser.END_TAG:
                    if (tag.equalsIgnoreCase("name")) {
                        person.setName(text);
                    } else if (tag.equalsIgnoreCase("abteilung")) {
                        person.setField(text);
                    } else if (tag.equalsIgnoreCase("studiengang")) {
                        person.setStudy(text);
                    } else if (tag.equalsIgnoreCase("email")) {
                        person.setEmail(text);
                    } else if (tag.equalsIgnoreCase("telefon")) {
                        person.setPhoneNumber(text);
                    } else if (tag.equalsIgnoreCase("raumnr")) {
                        person.setRoomNo(text);
                    } else if (tag.equalsIgnoreCase("person")) {
                        if(!personStrings.contains(person.filterString())){
                            personStrings.add(person.filterString());
                            people.add(person);
                        }
                        stop = true;
                    }
                default:
                    break;
            }
            eventType = parser.next();
        }
    }

    public void parseRoom(int eventType) throws Exception{
        room = new Room();
        boolean stop = false;
        while(!stop) {
            String tag = parser.getName();
            switch (eventType) {
                case XmlPullParser.TEXT:
                    text = parser.getText();
                    break;
                case XmlPullParser.END_TAG:
                    if (tag.equalsIgnoreCase("name")) {
                        room.setName(text);
                    } else if (tag.equalsIgnoreCase("raumart")) {
                        room.setRoomType(text);
                    } else if (tag.equalsIgnoreCase("raumnr")) {
                        room.setRoomNo(text);
                    } else if (tag.equalsIgnoreCase("resourceURL")) {
                        room.setUrl(text);
                    } else if (tag.equalsIgnoreCase("raum")) {
                        if(!roomStrings.contains(room.filterString())){
                            roomStrings.add(room.filterString());
                            rooms.add(room);
                        }
                        stop = true;
                    }
                default:
                    break;
            }
            eventType = parser.next();
        }
    }

    public void createMap(){
        entryMap.put("courses", courses);
        entryMap.put("people",people);
        entryMap.put("rooms",rooms);
    }
}
