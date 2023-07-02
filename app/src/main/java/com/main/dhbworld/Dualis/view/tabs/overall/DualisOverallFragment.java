package com.main.dhbworld.Dualis.view.tabs.overall;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.main.dhbworld.Dualis.parser.api.DualisAPI;
import com.main.dhbworld.Dualis.parser.htmlparser.gpa.DualisGPA;
import com.main.dhbworld.Dualis.parser.htmlparser.overall.DualisOverallData;
import com.main.dhbworld.R;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class DualisOverallFragment extends Fragment implements DualisAPI.OverallDataListener {

    private final String arguments;
    private final List<HttpCookie> cookies;

    private Context context;
    private Activity activity;

    private CircularProgressIndicator mainProgressIndicator;
    private LinearLayout mainLayout;
    private RecyclerView coursesRecyclerView;

    private TextView totalGPATextView;
    private TextView majorGPATextView;
    private TextView totalCreditsTextView;

    private DualisAPI dualisAPI;
    private CookieHandler cookieHandler;

    public DualisOverallFragment() {
        this.arguments = "";
        this.cookies = new ArrayList<>();
    }

    public DualisOverallFragment(String arguments, List<HttpCookie> cookies) {
        this.arguments = arguments;
        this.cookies = cookies;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        context = getContext();
        activity = getActivity();
        if (context == null || activity == null) {
            return;
        }

        setupViews();

        if (!setupCookies()) return;

        setupDualisAPI();
    }

    private void setupViews() {
        if (this.getView() == null) {
            return;
        }

        mainProgressIndicator = this.getView().findViewById(R.id.progress_main);
        mainLayout = this.getView().findViewById(R.id.main_layout);
        coursesRecyclerView = this.getView().findViewById(R.id.recycler_view_courses);

        totalGPATextView = this.getView().findViewById(R.id.total_gpa_tv);
        majorGPATextView = this.getView().findViewById(R.id.major_gpa_tv);
        totalCreditsTextView = this.getView().findViewById(R.id.total_credits_tv);
    }

    private boolean setupCookies() {
        cookieHandler = CookieManager.getDefault();
        if (cookies.size() == 0) {
            Snackbar.make(activity.findViewById(android.R.id.content), getResources().getString(R.string.error_getting_kvv_data), BaseTransientBottomBar.LENGTH_SHORT).show();
            return false;
        }

        CookieManager cookieManager = new CookieManager();
        try {
            cookieManager.getCookieStore().add(new URI("dualis.dhbw.de"), cookies.get(0));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void setupDualisAPI() {
        dualisAPI = new DualisAPI(context, arguments, cookieHandler);
        makeOverallRequest();
    }

    public void makeOverallRequest() {
        hideLayout();
        dualisAPI.requestOverall(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dualis_overall_view, container, false);
    }

    private void showLayout() {
        mainLayout.setVisibility(View.VISIBLE);
        mainProgressIndicator.setVisibility(View.GONE);
    }

    private void hideLayout() {
        mainLayout.setVisibility(View.INVISIBLE);
        mainProgressIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onOverallDataLoaded(DualisGPA dualisGPA, DualisOverallData dualisOverallData) {
        totalGPATextView.setText(dualisGPA.getTotalGPA());
        majorGPATextView.setText(dualisGPA.getMajorCourseGPA());
        totalCreditsTextView.setText(getResources().getString(R.string.values_credits, dualisOverallData.getEarnedCredits(), dualisOverallData.getNeededCredits()));

        OverallCourseAdapter overallCourseAdapter = new OverallCourseAdapter(context, dualisOverallData.getCourses());
        coursesRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        coursesRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        coursesRecyclerView.setAdapter(overallCourseAdapter);
        overallCourseAdapter.notifyItemRangeInserted(0, dualisOverallData.getCourses().size());

        showLayout();
    }

    @Override
    public void onError(Exception error) {
        Snackbar.make(activity.findViewById(android.R.id.content), getString(R.string.error_with_message, error.toString()), BaseTransientBottomBar.LENGTH_LONG).show();
    }
}
