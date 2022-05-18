package com.main.dhbworld.Organizer;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class OrganizerFragmentAdapter extends FragmentStateAdapter {
    String currentQuery = "";
    OrganizerOverallFragment fragment;
    OrganizerOverallFragment fragment2;
    OrganizerOverallFragment fragment3;

    public OrganizerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                fragment = new OrganizerOverallFragment(position,currentQuery);
                return fragment;
            case 1:
                fragment2 = new OrganizerOverallFragment(position, currentQuery);
                return fragment2;
            case 2:
                fragment3 = new OrganizerOverallFragment(position, currentQuery);
                return fragment3;
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
        fragment.setQuery(query);
    }

}
