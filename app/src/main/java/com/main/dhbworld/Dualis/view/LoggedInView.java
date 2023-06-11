package com.main.dhbworld.Dualis.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.main.dhbworld.Navigation.NavigationUtilities;
import com.main.dhbworld.R;

import java.net.HttpCookie;
import java.util.List;

public class LoggedInView {

    private final AppCompatActivity activity;
    private final String arguments;
    private final List<HttpCookie> cookies;
    private DualisFragmentAdapter dualisFragmentAdapter;

    public LoggedInView(AppCompatActivity activity, String arguments, List<HttpCookie> cookies) {
        this.activity = activity;
        this.arguments = arguments;
        this.cookies = cookies;
    }

    public void createView() {
        activity.setContentView(R.layout.activity_dualis);
        NavigationUtilities.setUpNavigation(activity, R.id.dualis);

        TabLayout tabLayout = activity.findViewById(R.id.dualisTabLayout);

        ViewPager2 viewPager2 = activity.findViewById(R.id.dualisViewPager);
        dualisFragmentAdapter = new DualisFragmentAdapter(activity, arguments, cookies);
        viewPager2.setAdapter(dualisFragmentAdapter);
        viewPager2.setOffscreenPageLimit(2);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0) {
                    tab.setText(R.string.overview);
                    tab.setIcon(R.drawable.ic_baseline_dashboard_24);
                } else if (position == 1) {
                    tab.setText(R.string.Courses);
                    tab.setIcon(R.drawable.ic_baseline_book_24);
                } else {
                    tab.setText(R.string.documents);
                    tab.setIcon(R.drawable.ic_baseline_description_24);
                }
            }
        });

        tabLayoutMediator.attach();
        Toolbar toolbar = activity.findViewById(R.id.topAppBar);

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.dualis_refresh) {
                if (tabLayout.getSelectedTabPosition() == 0) {
                    dualisFragmentAdapter.dualisOverallFragment.makeOverallRequest();
                } else if (tabLayout.getSelectedTabPosition() == 1) {
                    dualisFragmentAdapter.dualisSemesterFragment.makeCourseRequest();
                } else {
                    dualisFragmentAdapter.dualisDocumentFragment.makeDocumentsRequest();
                }

                return true;
            }
            return false;
        });
    }
}
