package com.main.dhbworld.Organizer;

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

    public Map<String, ArrayList> getAllElements(){
        try {
            URL url = new URL("https://rapla.dhbw-karlsruhe.de/rapla?key=2llRzrjV9Yj0yY4JKsO9cneRD8XIxxCqFeg5tRpzABg");
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
                if(tag.equals("kurs")){
                    parseCourse(tag,eventType);
                } else if(tag.equals("person")){
                    parsePerson(tag, eventType);
                }else if(tag.equals("raum")){
                    parseRoom(tag, eventType);
                }
            }
            eventType = parser.next();
        }
        createMap();
    }

    public void parseCourse(String tag, int eventType) throws Exception {
        course = new Course();
        boolean stop = false;
        while(!stop) {
            tag = parser.getName();
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
                    } else if (tag.equalsIgnoreCase("kurs")) {
                        courses.add(course);
                        stop = true;
                    }
                default:
                    break;
            }
            eventType = parser.next();
        }
    }

    public void parsePerson(String tag, int eventType) throws Exception{
        person = new Person();
        boolean stop = false;
        while(!stop) {
            tag = parser.getName();
            switch (eventType) {
                case XmlPullParser.TEXT:
                    text = parser.getText();
                    break;
                case XmlPullParser.END_TAG:
                    if (tag.equalsIgnoreCase("name")) {
                        person.setName(text);
                    } else if (tag.equalsIgnoreCase("abteilung")) {
                        person.setAbteilung(text);
                    } else if (tag.equalsIgnoreCase("studiengang")) {
                        person.setStudiengang(text);
                    } else if (tag.equalsIgnoreCase("email")) {
                        person.setEmail(text);
                    } else if (tag.equalsIgnoreCase("telefon")) {
                        person.setPhoneNumber(text);
                    } else if (tag.equalsIgnoreCase("raumnr")) {
                        person.setRoomNo(text);
                    } else if (tag.equalsIgnoreCase("person")) {
                        people.add(person);
                        stop = true;
                    }
                default:
                    break;
            }
            eventType = parser.next();
        }
    }

    public void parseRoom(String tag, int eventType) throws Exception{
        room = new Room();
        boolean stop = false;
        while(!stop) {
            tag = parser.getName();
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
                    } else if (tag.equalsIgnoreCase("raum")) {
                        rooms.add(room);
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
