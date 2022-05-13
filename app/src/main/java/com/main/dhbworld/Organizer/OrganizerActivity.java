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
    private ArrayList<Course> courses = new ArrayList<>();
    static ArrayList<Person> people = new ArrayList<>();
    static ArrayList<Room> rooms = new ArrayList<>();
    public Map<String,ArrayList> entryMap = new HashMap<>();

    organizerListAdapter adapter;
    ListView listView;
    private ViewPager2 viewPager;
    private ScreenSlidePagerAdapter fragmentStateAdapter;

    private static final int NUM_PAGES = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_layout);
        NavigationUtilities.setUpNavigation(this, R.id.navigationView);
        createView();

        listView = findViewById(R.id.listviewitem);
        parseThread.start();
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

    Thread parseThread = new Thread(new Runnable() {
        @Override
        public void run() {
            OrganizerParser organizerParser = new OrganizerParser();
            entryMap = organizerParser.getAllElements();
            courses = entryMap.get("courses");
            people = entryMap.get("people");
            rooms = entryMap.get("rooms");
        }});

}


