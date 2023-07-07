package com.main.dhbworld.Dualis.view;

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

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private DualisFragmentAdapter dualisFragmentAdapter;
    private Toolbar toolbar;

    public LoggedInView(AppCompatActivity activity, String arguments, List<HttpCookie> cookies) {
        this.activity = activity;
        this.arguments = arguments;
        this.cookies = cookies;
    }

    public void createView() {
        activity.setContentView(R.layout.activity_dualis);
        NavigationUtilities.setUpNavigation(activity, R.id.dualis);

        setupViews();

        setupViewPager();
        setupTabs();
        setupToolbar();
    }

    private void setupViews() {
        tabLayout = activity.findViewById(R.id.dualisTabLayout);
        viewPager2 = activity.findViewById(R.id.dualisViewPager);
        dualisFragmentAdapter = new DualisFragmentAdapter(activity, arguments, cookies);
        toolbar = activity.findViewById(R.id.topAppBar);
    }

    private void setupViewPager() {
        viewPager2.setAdapter(dualisFragmentAdapter);
        viewPager2.setOffscreenPageLimit(2);
    }

    private void setupTabs() {
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
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
        }).attach();
    }

    private void setupToolbar() {
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.dualis_refresh) {
                if (tabLayout.getSelectedTabPosition() == 0) {
                    dualisFragmentAdapter.dualisOverallFragment.makeOverallRequest();
                } else if (tabLayout.getSelectedTabPosition() == 1) {
                    dualisFragmentAdapter.dualisSemesterFragment.makeCourseRequest(false);
                } else {
                    dualisFragmentAdapter.dualisDocumentFragment.makeDocumentsRequest();
                }
                return true;
            }
            return false;
        });
    }
}
