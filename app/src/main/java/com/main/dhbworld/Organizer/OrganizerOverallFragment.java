package com.main.dhbworld.Organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.main.dhbworld.R;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

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
    Map<String, ArrayList> entryMap;



    public OrganizerOverallFragment(int position, String currentQuery, Map<String, ArrayList> entryMap) {
        this.position = position;
        this.currentQuery = currentQuery;
        this.entryMap = entryMap;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getElements();
    }
    private void getElements(){
        courses = entryMap.get("courses");
        courseDataHandler = new CourseDataHandler(courses);
        people = entryMap.get("people");
        personDataHandler = new PersonDataHandler(people);
        rooms = entryMap.get("rooms");
        roomsDataHandler = new RoomsDataHandler(rooms);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        setHasOptionsMenu(true);

        SearchViewModel searchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        searchViewModel.getQuery().observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        setQuery(s);
                    }
                });

        recyclerView = view.findViewById(R.id.org_recylclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);


            courseAdapter = new OrganizerCourseAdapter(getContext(), courses);
            personAdapter = new OrganizerPersonAdapter(getContext(), people);
            roomAdapter = new OrganizerRoomAdapter(getActivity(), rooms);
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.organizertab,container,false);
        return view;
    }


    public void setQuery(String query){
        currentQuery = query;
        switch(position) {
            case 0:
                courses.clear();
                courses.addAll(courseDataHandler.filter(query));
                System.out.println(courses);
                courseAdapter.notifyDataSetChanged();
                break;
            case 1:
                people.clear();
                people.addAll(personDataHandler.filter(query));
                System.out.println(people);
                personAdapter.notifyDataSetChanged();
                break;
            case 2:
                rooms.clear();
                rooms.addAll(roomsDataHandler.filter(query));
                System.out.println(rooms);
                roomAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }
}

