package com.main.dhbworld.Dualis.view;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.main.dhbworld.Dualis.view.tabs.documents.DualisDocumentFragment;
import com.main.dhbworld.Dualis.view.tabs.overall.DualisOverallFragment;
import com.main.dhbworld.Dualis.view.tabs.semester.DualisSemesterFragment;

import java.net.HttpCookie;
import java.util.List;

public class DualisFragmentAdapter extends FragmentStateAdapter {

    private final String arguments;
    private final List<HttpCookie> cookies;

    public DualisOverallFragment dualisOverallFragment;
    public DualisSemesterFragment dualisSemesterFragment;
    public DualisDocumentFragment dualisDocumentFragment;

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
                dualisOverallFragment = new DualisOverallFragment(arguments, cookies);
                return dualisOverallFragment;
            case 1:
                dualisSemesterFragment = new DualisSemesterFragment(arguments, cookies);
                return dualisSemesterFragment;
            case 2:
                dualisDocumentFragment = new DualisDocumentFragment(arguments, cookies);
                return dualisDocumentFragment;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
