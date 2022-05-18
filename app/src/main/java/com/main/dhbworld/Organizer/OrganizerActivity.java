package com.main.dhbworld.Organizer;

import android.app.SearchManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.main.dhbworld.Navigation.NavigationUtilities;
import com.main.dhbworld.R;


public class OrganizerActivity extends AppCompatActivity {
    ListView listView;
    ViewPager2 viewPager;
    OrganizerFragmentAdapter organizerFragmentAdapter;
    MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_layout);
        toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        NavigationUtilities.setUpNavigation(this, R.id.organizer);
        if(isNetworkConnected()){
            createView();
        }
        else {
            displayError();
        }

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.organizer_top_app_bar, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.organizerSearchIcon).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                organizerFragmentAdapter.setQuery(newText);
                return true;
            }
        });
       return super.onCreateOptionsMenu(menu);
    }

    public void createView() {
        TabLayout tabLayout = findViewById(R.id.organizerTabLayout);
        viewPager = findViewById(R.id.organizerViewPager);
        listView = findViewById(R.id.org_recylclerview);
        organizerFragmentAdapter = new OrganizerFragmentAdapter(this);
        viewPager.setAdapter(organizerFragmentAdapter);
        viewPager.setOffscreenPageLimit(2);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position){
                case 0:
                    tab.setText("Courses");
                    break;
                case 1:
                    tab.setText("People");
                    break;
                case 2:
                    tab.setText("Rooms");
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
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    public void displayError(){
        View drawer = findViewById(R.id.drawerLayout);
        Snackbar.make(drawer, (CharSequence)"Network Error! Couldn't fetch data from Server.", 6).show();
    }
}


