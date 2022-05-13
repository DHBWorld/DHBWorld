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

    public OrganizerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new OrganizerOverallFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
