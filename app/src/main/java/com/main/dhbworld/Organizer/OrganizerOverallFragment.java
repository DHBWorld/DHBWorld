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
import java.util.HashMap;
import java.util.Map;

public class OrganizerOverallFragment extends Fragment{

    int position;
    ListView listView;
    ArrayList<Course> courses;
    ArrayList<Person> people;
    ArrayList<Room> rooms;

    public OrganizerOverallFragment(int position) {
        this.position = position;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseThread.start();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        LayoutInflater inflater  = getLayoutInflater();
        View organizerTab = inflater.inflate(R.layout.organizertab, null);
        super.onViewCreated(organizerTab,savedInstanceState);
        listView = organizerTab.findViewById(R.id.listviewitem);

        try {
            parseThread.join();
            OrganizerCourseAdapter courseAdapter = new OrganizerCourseAdapter(this.getContext(),courses);
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
        return inflater.inflate(R.layout.organizer_layout, container, false);
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
}

