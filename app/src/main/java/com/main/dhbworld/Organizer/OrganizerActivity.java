package com.main.dhbworld.Organizer;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.main.dhbworld.Navigation.NavigationUtilities;
import com.main.dhbworld.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrganizerActivity extends FragmentActivity {
    private ArrayList courses = new ArrayList<>();
    static ArrayList people = new ArrayList<>();
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
//        createTabs();

        listView = findViewById(R.id.listviewitem);
        parseThread.start();
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

//    public void createTabs(){
//        viewPager = findViewById(R.id.organizerViewPager);
//        fragmentStateAdapter = new ScreenSlidePagerAdapter(this);
//        viewPager.setAdapter(fragmentStateAdapter);
//        TabLayout tabLayout = findViewById(R.id.organizerTabLayout);
//    }

    Thread parseThread = new Thread(new Runnable() {
        @Override
        public void run() {
            OrganizerParser organizerParser = new OrganizerParser();
            entryMap = organizerParser.getAllElements();
            courses = entryMap.get("courses");
            people = entryMap.get("people");
            rooms = entryMap.get("rooms");
            displayCourses();
        }});

        public void displayCourses() {
            adapter = new organizerListAdapter(this, courses);
            setContentView(R.layout.organizertab);
            ListView listView = findViewById(R.id.listviewitem);
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listView.setAdapter(adapter);
                }
            });
            }

}


