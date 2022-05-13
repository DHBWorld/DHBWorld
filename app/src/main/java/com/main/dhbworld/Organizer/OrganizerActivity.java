package com.main.dhbworld.Organizer;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.main.dhbworld.Dualis.DualisFragmentAdapter;
import com.main.dhbworld.Navigation.NavigationUtilities;
import com.main.dhbworld.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrganizerActivity extends FragmentActivity {
    ListView listView;
    ViewPager2 viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_layout);
        NavigationUtilities.setUpNavigation(this, R.id.navigationView);
        createView();
        viewPager = findViewById(R.id.organizerViewPager);


        listView = findViewById(R.id.listviewitem);
    }

    public void createView() {
        TabLayout tabLayout = findViewById(R.id.organizerTabLayout);
        ViewPager2 viewPager2 = findViewById(R.id.organizerViewPager);
        OrganizerFragmentAdapter organizerFragmentAdapter = new OrganizerFragmentAdapter(this);
        viewPager2.setAdapter(organizerFragmentAdapter);
        viewPager2.setOffscreenPageLimit(2);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0) {
                    tab.setText("Courses");
                    //tab.setIcon(R.drawable.ic_baseline_dashboard_24);
                } else if (position == 1) {
                    tab.setText("People");
                    // tab.setIcon(R.drawable.ic_baseline_book_24);
                } else {
                    tab.setText("Rooms");
                }
            }
        });
        tabLayoutMediator.attach();

    }

    @Override
    public void onBackPressed(){
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }


}


