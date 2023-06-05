package com.main.dhbworld.Organizer;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.main.dhbworld.DashboardActivity;
import com.main.dhbworld.Navigation.NavigationUtilities;
import com.main.dhbworld.NetworkAvailability;
import com.main.dhbworld.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class OrganizerActivity extends AppCompatActivity {
    ViewPager2 viewPager;
    OrganizerFragmentAdapter organizerFragmentAdapter;
    SearchViewModel searchViewModel;
    Map<String, ArrayList> entryMap;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (NetworkAvailability.check(OrganizerActivity.this)) {
            ArrayList<Course> courses = new ArrayList<>();
            ArrayList<Person> persons = new ArrayList<>();
            ArrayList<Room> rooms = new ArrayList<>();
            createMockData(courses, persons, rooms);

            entryMap = new HashMap<>();
            entryMap.put("courses", courses);
            entryMap.put("people", persons);
            entryMap.put("rooms", rooms);

            intitalSetup();
            createView();
        } else {
            Snackbar.make(this.findViewById(android.R.id.content), getResources().getString(R.string.problemsWithInternetConnection), BaseTransientBottomBar.LENGTH_LONG).show();
            Intent intent = new Intent(OrganizerActivity.this, DashboardActivity.class);
            startActivity(intent);
        }
    }

    private void intitalSetup(){
        setContentView(R.layout.organizer_layout);
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        NavigationUtilities.setUpNavigation(this, R.id.organizer);
        parseThread.start();
    }

    Thread parseThread = new Thread(new Runnable() {
        @Override
        public void run() {
            OrganizerParser organizerParser = new OrganizerParser();
            entryMap = organizerParser.getAllElements(OrganizerActivity.this);

            System.out.println("Number of Rooms: " + Objects.requireNonNull(entryMap.get("rooms")).size());

            runOnUiThread(() -> {
                if (!entryMap.isEmpty()) {
                    organizerFragmentAdapter.updateData(entryMap);
                } else {
                    Snackbar.make(OrganizerActivity.this.findViewById(android.R.id.content), R.string.network_error, BaseTransientBottomBar.LENGTH_LONG).show();
                }
                searchConfigViewPager();
            });
        }});

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.organizer_top_app_bar, menu);
        addSearchBar(menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void addSearchBar(Menu menu){
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.organizerSearchIcon).getActionView();
        searchView.setQueryHint("Search in all Tabs");
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                searchViewModel.setQuery(newText);
                return true;
            }
        });
        searchView.setOnCloseListener(() -> {
            searchViewModel.setQuery("");
            return false;
        });
    }

    private void searchConfigViewPager(){
        // When viewPager changes tab, searchViewModel clears current Query.
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                searchViewModel.setQuery("");
                //force close keyboard
                if (getCurrentFocus() != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
        });
    }

    public void createView() {
        TabLayout tabLayout = findViewById(R.id.organizerTabLayout);
        viewPager = findViewById(R.id.organizerViewPager);
        organizerFragmentAdapter = new OrganizerFragmentAdapter(this, entryMap);
        viewPager.setAdapter(organizerFragmentAdapter);
        viewPager.setOffscreenPageLimit(2);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position){
                case 0:
                    tab.setText(R.string.Courses);
                    tab.setIcon(R.drawable.ic_baseline_class_24);
                    break;
                case 1:
                    tab.setText(R.string.people);
                    tab.setIcon(R.drawable.ic_baseline_person_24);
                    break;
                case 2:
                    tab.setText(R.string.rooms);
                    tab.setIcon(R.drawable.ic_baseline_room_24);
                    break;
                default:
                    break;
            }
        });
        tabLayoutMediator.attach();
    }

    private void createMockData(ArrayList<Course> courses,ArrayList<Person> persons,ArrayList<Room> rooms){
        String mock1 = "██████████████";
        String mock2 = "████████████████████████████";
        for (int i=0; i<10; i++) {
            Course course = new Course();
            Room room = new Room();
            Person person = new Person();

            room.setName(mock1);
            room.setRoomType(mock2);

            course.setName(mock1);
            course.setStudy(mock2);

            person.setName(mock1);
            person.setStudy(mock2);

            rooms.add(room);
            persons.add(person);
            courses.add(course);
        }
    }

    @Override
    public void onBackPressed(){
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }
}


