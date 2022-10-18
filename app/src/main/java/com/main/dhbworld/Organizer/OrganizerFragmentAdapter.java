package com.main.dhbworld.Organizer;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.Map;

public class OrganizerFragmentAdapter extends FragmentStateAdapter {
    String currentQuery = "";
    OrganizerOverallFragment courseFragment;
    OrganizerOverallFragment personFragment;
    OrganizerOverallFragment roomFragment;
    int position;
    Map<String, ArrayList> entryMap;

    public OrganizerFragmentAdapter(@NonNull FragmentActivity fragmentActivity, Map<String, ArrayList> entryMap) {
        super(fragmentActivity);
        this.entryMap = entryMap;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        this.position = position;
        switch (position){
            case 0:
                courseFragment = new OrganizerOverallFragment(position,currentQuery, entryMap);
                return courseFragment;
            case 1:
                personFragment = new OrganizerOverallFragment(position, currentQuery, entryMap);
                return personFragment;
            case 2:
                roomFragment = new OrganizerOverallFragment(position, currentQuery, entryMap);
                return roomFragment;
            default:
                return createFragment(0);
        }
    }


    @Override
    public int getItemCount() {
        return 3;
    }

    public void setQuery(String query){
        currentQuery = query;

        courseFragment.setQuery(query);
        personFragment.setQuery(query);
        roomFragment.setQuery(query);
    }

    public void updateData(Map<String, ArrayList> map) {
        courseFragment.updateData(map);
        personFragment.updateData(map);
        roomFragment.updateData(map);
    }
}
