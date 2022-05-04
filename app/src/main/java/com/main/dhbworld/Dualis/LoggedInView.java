package com.main.dhbworld.Dualis;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.VolleyError;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputLayout;
import com.main.dhbworld.DualisActivity;
import com.main.dhbworld.Navigation.NavigationUtilities;
import com.main.dhbworld.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class LoggedInView {

    private final AppCompatActivity activity;
    private final String arguments;
    private final List<HttpCookie> cookies;

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
        DualisFragmentAdapter dualisFragmentAdapter = new DualisFragmentAdapter(activity, arguments, cookies);
        viewPager2.setAdapter(dualisFragmentAdapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0) {
                    tab.setText("Übersicht");
                    tab.setIcon(R.drawable.ic_baseline_dashboard_24);
                } else {
                    tab.setText("Kurse");
                    tab.setIcon(R.drawable.ic_baseline_book_24);
                }
            }
        });

        tabLayoutMediator.attach();
        Toolbar toolbar = activity.findViewById(R.id.topAppBar);

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.dualis_refresh) {
                if (tabLayout.getSelectedTabPosition() == 0) {
                    DualisOverallFragment.makeOverallRequest(activity, arguments);
                } else {
                    DualisSemesterFragment.makeCourseRequest(activity, arguments);
                }

                return true;
            }
            return false;
        });
    }
}
