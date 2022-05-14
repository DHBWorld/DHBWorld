package com.main.dhbworld.Organizer;

import android.os.Bundle;
import android.widget.ListView;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.main.dhbworld.Navigation.NavigationUtilities;
import com.main.dhbworld.R;


public class OrganizerActivity extends FragmentActivity {
    ListView listView;
    ViewPager2 viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_layout);
        NavigationUtilities.setUpNavigation(this, R.id.navigationView);
        createView();
    }

    public void createView() {
        TabLayout tabLayout = findViewById(R.id.organizerTabLayout);
        viewPager = findViewById(R.id.organizerViewPager);
        listView = findViewById(R.id.listviewitem);
        OrganizerFragmentAdapter organizerFragmentAdapter = new OrganizerFragmentAdapter(this);
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
}


