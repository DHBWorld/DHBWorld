package com.main.dhbworld.Organizer;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class ScreenSlidePagerAdapter extends FragmentStateAdapter {
    private final static int NUM_PAGES = 3;
    public ScreenSlidePagerAdapter(FragmentActivity fa) {
        super(fa);
    }

    @Override
    public Fragment createFragment(int position) {
        return new organizerTabFragment();
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
