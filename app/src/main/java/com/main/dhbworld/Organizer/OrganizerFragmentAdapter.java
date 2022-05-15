package com.main.dhbworld.Organizer;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.net.HttpCookie;
import java.util.List;

public class OrganizerFragmentAdapter extends FragmentStateAdapter {
    String currentQuery = "";
    OrganizerOverallFragment fragment;

    public OrganizerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        fragment = new OrganizerOverallFragment(position,currentQuery);
        return fragment;
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
