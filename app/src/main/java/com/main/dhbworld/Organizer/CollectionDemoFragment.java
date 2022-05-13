package com.main.dhbworld.Organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.main.dhbworld.R;

public class CollectionDemoFragment extends Fragment {
    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    DemoCollectionAdapter demoCollectionAdapter;
    ViewPager2 viewPager;
    String[] names = new String[3];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        names[0] = "Courses";
        names[1] = "People";
        names[2] = "Rooms";

        return inflater.inflate(R.layout.organizer_layout, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TabLayout tabLayout = view.findViewById(R.id.organizerTabLayout);
        viewPager = view.findViewById(R.id.organizerViewPager);
        viewPager.setAdapter(demoCollectionAdapter);
        demoCollectionAdapter = new DemoCollectionAdapter(this);
        new TabLayoutMediator(tabLayout,viewPager,
                (tab, position) -> tab.setText(names[position])).attach();
        }
    }

