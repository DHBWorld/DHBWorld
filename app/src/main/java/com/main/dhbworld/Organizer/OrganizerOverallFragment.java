package com.main.dhbworld.Organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.main.dhbworld.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrganizerOverallFragment extends Fragment{

    private String arguments;
    static ListView listView;
    private Map<String,ArrayList> entryMap = new HashMap<>();
    ArrayList courses;
    ArrayList<Person> people;
    ArrayList<Room> rooms;

    public OrganizerOverallFragment() {
        this.arguments = arguments;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        LayoutInflater inflater  = getLayoutInflater();
        View organizerTab = inflater.inflate(R.layout.organizertab,null);
        super.onViewCreated(organizerTab,savedInstanceState);
        listView = organizerTab.findViewById(R.id.listviewitem);
        parseThread.start();





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
            entryMap = organizerParser.getAllElements();
            courses = entryMap.get("courses");
            people = entryMap.get("people");
            rooms = entryMap.get("rooms");
        }});
}

