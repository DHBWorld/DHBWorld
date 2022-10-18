package com.main.dhbworld.Organizer;

import android.app.SearchManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.main.dhbworld.Navigation.NavigationUtilities;
import com.main.dhbworld.R;

import java.util.ArrayList;
import java.util.Map;


public class OrganizerActivity extends AppCompatActivity {
    ListView listView;
    ViewPager2 viewPager;
    OrganizerFragmentAdapter organizerFragmentAdapter;
    MaterialToolbar toolbar;
    SearchViewModel searchViewModel;
    Map<String, ArrayList> entryMap;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intitalSetup();
    }

    private void intitalSetup(){
        setContentView(R.layout.organizer_layout);
        toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        NavigationUtilities.setUpNavigation(this, R.id.organizer);
        parseThread.start();
    }


    Thread parseThread = new Thread(new Runnable() {
        @Override
        public void run() {
            OrganizerParser organizerParser = new OrganizerParser();
            entryMap = organizerParser.getAllElements(OrganizerActivity.this);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    checkNetwork();
                    searchConfigViewPager();
                }
            });
        }});

    private void checkNetwork(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            cm.getActiveNetworkInfo().isConnected();
                createView();
        }
        catch (Exception e){
            Snackbar.make(this.findViewById(android.R.id.content), "Network Error! Couldn't fetch data from Server.", BaseTransientBottomBar.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

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
        listView = findViewById(R.id.org_recylclerview);
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


