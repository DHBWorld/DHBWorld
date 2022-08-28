package com.main.dhbworld.Dualis;

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

import com.android.volley.VolleyError;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.main.dhbworld.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class DualisOverallFragment extends Fragment implements DualisAPI.OverallDataLoadedListener, DualisAPI.OverallErrorListener {

    private final String arguments;
    private final List<HttpCookie> cookies;

    private static CircularProgressIndicator mainProgressIndicator;
    private static LinearLayout mainLayout;
    private RecyclerView coursesRecyclerView;

    private TextView totalGPATextView;
    private TextView majorGPATextView;
    private TextView totalCreditsTextView;

    private static DualisAPI dualisAPI;
    private static CookieHandler cookieHandler;

    View mainView;

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
        super.onViewCreated(mainView, savedInstanceState);

        mainView = this.getView();

        mainProgressIndicator = mainView.findViewById(R.id.progress_main);
        mainLayout = mainView.findViewById(R.id.main_layout);
        coursesRecyclerView = mainView.findViewById(R.id.recycler_view_courses);

        totalGPATextView = mainView.findViewById(R.id.total_gpa_tv);
        majorGPATextView = mainView.findViewById(R.id.major_gpa_tv);
        totalCreditsTextView = mainView.findViewById(R.id.total_credits_tv);

        dualisAPI = new DualisAPI();
        dualisAPI.setOnOverallDataLoadedListener(this);
        dualisAPI.setOnOverallErrorListener(this);

        CookieManager cookieManager = new CookieManager();
        try {
            cookieManager.getCookieStore().add(new URI("dualis.dhbw.de"), cookies.get(0));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        cookieHandler = CookieManager.getDefault();

        makeOverallRequest(getContext(), arguments);

    }

    static void makeOverallRequest(Context context, String arguments) {
        hideLayout();
        dualisAPI.makeOverallRequest(context, arguments, cookieHandler);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dualis_overall_view, container, false);
    }

    private static void showLayout() {
        mainLayout.setVisibility(View.VISIBLE);
        mainProgressIndicator.setVisibility(View.GONE);
    }

    private static void hideLayout() {
        mainLayout.setVisibility(View.INVISIBLE);
        mainProgressIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onOverallDataLoaded(JSONObject data) {
        ArrayList<OverallCourseModel> overallCourseModels = new ArrayList<>();
        String totalGPA = "";
        String majorCourseGPA = "";
        String totalSum = "";
        String totalSumNeeded = "";
        try {
            totalGPA = data.getString("totalGPA");
            majorCourseGPA = data.getString("majorCourseGPA");
            totalSum = data.getString("totalSum");
            totalSumNeeded = data.getString("totalSumNeeded");

            JSONArray coursesArray = data.getJSONArray("courses");
            for (int i=0; i<coursesArray.length(); i++) {
                JSONObject course = coursesArray.getJSONObject(i);

                OverallCourseModel overallCourseModel = new OverallCourseModel(
                        course.getString("moduleID"),
                        course.getString("moduleName"),
                        course.getString("credits"),
                        course.getString("grade"),
                        course.getBoolean("passed")
                );

                overallCourseModels.add(overallCourseModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //SpannableString totalGPASpannable = new SpannableString(totalGPA);
        //totalGPASpannable.setSpan(new UnderlineSpan(), 0, totalGPA.length(), 0);
        //totalGPASpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, totalGPA.length(), 0);

        totalGPATextView.setText(totalGPA);
        majorGPATextView.setText(majorCourseGPA);
        totalCreditsTextView.setText(getResources().getString(R.string.values_credits, totalSum, totalSumNeeded));

        OverallCourseAdapter overallCourseAdapter = new OverallCourseAdapter(getContext(), overallCourseModels);
        coursesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        coursesRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        coursesRecyclerView.setAdapter(overallCourseAdapter);
        overallCourseAdapter.notifyItemRangeInserted(0, overallCourseModels.size());

        showLayout();
    }

    @Override
    public void onOverallError(VolleyError error) {
        Snackbar.make(DualisOverallFragment.this.getActivity().findViewById(android.R.id.content), getString(R.string.error_with_message, error.toString()), BaseTransientBottomBar.LENGTH_LONG).show();
    }
}
