package com.main.dhbworld.Organizer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.main.dhbworld.Dualis.DualisAPI;
import com.main.dhbworld.Dualis.OverallCourseAdapter;
import com.main.dhbworld.Dualis.OverallCourseModel;
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

public class OrganizerOverallFragment extends Fragment implements DualisAPI.OverallDataLoadedListener, DualisAPI.OverallErrorListener {

    private String arguments;
    private static CircularProgressIndicator mainProgressIndicator;
    private static LinearLayout mainLayout;
    private static ListView listView;

    private static DualisAPI dualisAPI;
    private static CookieHandler cookieHandler;

    View mainView;

    public OrganizerOverallFragment() {
        this.arguments = arguments;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(mainView, savedInstanceState);
        mainView = this.getView();
        listView = mainView.findViewById(R.id.listviewitem);
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

    @Override
    public void onOverallDataLoaded(JSONObject data) {
        DualisAPI.setAlarmManager(getContext());

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
        Toast.makeText(getContext(), "Error: " + error.toString(), Toast.LENGTH_LONG).show();
    }
}
}
