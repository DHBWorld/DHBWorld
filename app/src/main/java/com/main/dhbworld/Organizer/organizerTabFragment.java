package com.main.dhbworld.Organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.main.dhbworld.R;

import java.util.Objects;

public class organizerTabFragment extends Fragment {
    ViewPager2 viewPager2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.organizertab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TabLayout tabLayout = view.findViewById(R.id.organizerTabLayout);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch(position){
            case 0:
                tab.setText("Courses");
            case 1:
                tab.setText("Professors");
            case 2:
                tab.setText("Rooms");
            }
        }).attach();
    }
}

