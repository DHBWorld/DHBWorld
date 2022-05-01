package com.main.dhbworld.Dualis;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.net.HttpCookie;
import java.util.List;

public class DualisFragmentAdapter extends FragmentStateAdapter {

    private final String arguments;
    private final List<HttpCookie> cookies;

    public DualisFragmentAdapter(@NonNull FragmentActivity fragmentActivity, String arguments, List<HttpCookie> cookies) {
        super(fragmentActivity);
        this.arguments = arguments;
        this.cookies = cookies;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new DualisOverallFragment(arguments, cookies);
            case 1:
                return new DualisSemesterFragment(arguments, cookies);
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
