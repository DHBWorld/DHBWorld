package com.main.dhbworld.Organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.main.dhbworld.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OrganizerOverallFragment extends Fragment{

    int position;
    String currentQuery;
    ListView listView;
    ArrayList<Course> courses;
    ArrayList<Person> people;
    ArrayList<Room> rooms;
    View view;
    OrganizerCourseAdapter courseAdapter;



    public OrganizerOverallFragment(int position, String currentQuery) {
        this.position = position;
        this.currentQuery = currentQuery;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseThread.start();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        LayoutInflater inflater  = getLayoutInflater();
        super.onViewCreated(view,savedInstanceState);
        setHasOptionsMenu(true);
        listView = view.findViewById(R.id.listviewitem);

        try {
            parseThread.join();
            courseAdapter = new OrganizerCourseAdapter(this.getContext(),courses);
            OrganizerPersonAdapter personAdapter = new OrganizerPersonAdapter(this.getContext(),people);
            OrganizerRoomAdapter roomAdapter = new OrganizerRoomAdapter(this.getContext(),rooms);
            switch(position) {
                case 0:
                    listView.setAdapter(courseAdapter);
                    courseAdapter.addAll(courses);
                    break;
                case 1:
                    listView.setAdapter(personAdapter);
                    personAdapter.addAll(people);
                    break;
                case 2:
                    listView.setAdapter(roomAdapter);
                    roomAdapter.addAll(rooms);
                    break;
                default:
                    break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.organizertab,container,false);
        return view;
    }

    Thread parseThread = new Thread(new Runnable() {
        @Override
        public void run() {
            OrganizerParser organizerParser = new OrganizerParser();
            Map<String, ArrayList> entryMap = organizerParser.getAllElements();
            courses = entryMap.get("courses");
            people = entryMap.get("people");
            rooms = entryMap.get("rooms");
        }});

    public void setQuery(String query){
        currentQuery = query;
        System.out.println(query);
        courseAdapter.filter(query);

    }


}

