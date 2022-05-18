package com.main.dhbworld.Organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.main.dhbworld.R;
import java.util.ArrayList;
import java.util.Map;

public class OrganizerOverallFragment extends Fragment{

    int position;
    String currentQuery;
    RecyclerView recyclerView;
    ArrayList<Course> courses;
    ArrayList<Person> people;
    ArrayList<Room> rooms;
    View view;
    OrganizerCourseAdapter courseAdapter;
    OrganizerRoomAdapter roomAdapter;
    OrganizerPersonAdapter personAdapter;

    CourseDataHandler courseDataHandler;
    PersonDataHandler personDataHandler;
    RoomsDataHandler roomsDataHandler;



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
        super.onViewCreated(view,savedInstanceState);
        setHasOptionsMenu(true);
        recyclerView = view.findViewById(R.id.org_recylclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        try {
            parseThread.join();
            courseAdapter = new OrganizerCourseAdapter(courses);
            personAdapter = new OrganizerPersonAdapter(people);
            roomAdapter = new OrganizerRoomAdapter(rooms);
            switch(position) {
                case 0:
                    recyclerView.setAdapter(courseAdapter);
                    break;
                case 1:
                    recyclerView.setAdapter(personAdapter);
                    break;
                case 2:
                    recyclerView.setAdapter(roomAdapter);
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
            courseDataHandler = new CourseDataHandler(courses);
            people = entryMap.get("people");
            personDataHandler = new PersonDataHandler(people);
            rooms = entryMap.get("rooms");
            roomsDataHandler = new RoomsDataHandler(rooms);
        }});

    public void setQuery(String query){
        currentQuery = query;
        courses.clear();
        courses.addAll(courseDataHandler.filter(query));

        people.clear();
        people.addAll(personDataHandler.filter(query));

        rooms.clear();
        rooms.addAll(roomsDataHandler.filter(query));

        courseAdapter.notifyDataSetChanged();
        personAdapter.notifyDataSetChanged();
        roomAdapter.notifyDataSetChanged();

    }
}

