package com.main.dhbworld.Dualis;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
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
import java.util.ArrayList;
import java.util.List;

public class LoggedInView implements DualisAPI.DataLoadedListener, DualisAPI.ErrorListener {

    private final Activity activity;
    private final String arguments;
    private final List<HttpCookie> cookies;

    private AutoCompleteTextView semesterDropdown;
    private String currentSemester = "";
    private List<VorlesungModel> vorlesungModels = new ArrayList<>();
    private VorlesungAdapter vorlesungAdapter;
    private CircularProgressIndicator mainProgressIndicator;
    private LinearLayout mainLayout;

    public LoggedInView(Activity activity, String arguments, List<HttpCookie> cookies) {
        this.activity = activity;
        this.arguments = arguments;
        this.cookies = cookies;
    }

    public void createView() {
        activity.setContentView(R.layout.activity_dualis);
        NavigationUtilities.setUpNavigation(activity, R.id.dualis);

        semesterDropdown = activity.findViewById(R.id.autoComplete);
        mainProgressIndicator = activity.findViewById(R.id.progress_main);
        mainLayout = activity.findViewById(R.id.main_layout);
        mainLayout.setVisibility(View.GONE);

        DualisAPI dualisAPI = new DualisAPI();
        dualisAPI.setOnDataLoadedListener(this);
        dualisAPI.setOnErrorListener(this);

        CookieManager cookieManager = new CookieManager();
        try {
            cookieManager.getCookieStore().add(new URI("dualis.dhbw.de"), cookies.get(0));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        CookieHandler cookieHandler = CookieManager.getDefault();

        dualisAPI.makeRequest(activity, arguments, cookieHandler);

        Toolbar toolbar = activity.findViewById(R.id.topAppBar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.dualis_refresh) {
                mainLayout.setVisibility(View.GONE);
                mainProgressIndicator.setVisibility(View.VISIBLE);
                dualisAPI.makeRequest(activity, arguments, cookieHandler);
                return true;
            }
            return false;
        });
    }

    @Override
    public void onDataLoaded(JSONObject data) {
        ArrayList<String> items = new ArrayList<>();
        try {
            for (int i=0; i<data.getJSONArray("semester").length(); i++) {
                JSONObject semester = data.getJSONArray("semester").getJSONObject(i);
                items.add(semester.getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity, R.layout.dualis_semester_list_item, items);
        semesterDropdown.setAdapter(arrayAdapter);
        if (items.contains(currentSemester)) {
            semesterDropdown.setText(currentSemester, false);
        } else {
            semesterDropdown.setText(items.get(0), false);
        }


        vorlesungModels = new ArrayList<>();
        RecyclerView mRecyclerView = activity.findViewById(R.id.recycler_view);
        try {
            if (items.contains(currentSemester)) {
                updateList(data, items.indexOf(currentSemester));
            } else {
                updateList(data, 0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
        mRecyclerView.setLayoutManager(mLayoutManager);
        vorlesungAdapter = new VorlesungAdapter(vorlesungModels, activity);
        mRecyclerView.setAdapter(vorlesungAdapter);



        semesterDropdown.setOnItemClickListener((adapterView, view, i, l) -> {
            currentSemester = semesterDropdown.getText().toString();
            try {
                updateList(data, i);
                vorlesungAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });


        semesterDropdown.setEnabled(true);
        mainProgressIndicator.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onError(VolleyError error) {
        Toast.makeText(activity, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
    }

    void updateList(JSONObject data, int position) throws JSONException {
        vorlesungModels.clear();
        JSONArray vorlesungen = data.getJSONArray("semester").getJSONObject(position).getJSONArray("Vorlesungen");
        for (int k = 0; k < vorlesungen.length(); k++) {
            JSONObject vorlesung = vorlesungen.getJSONObject(k);
            vorlesungModels.add(new VorlesungModel(vorlesung.getString("name"),
                    vorlesung.getJSONArray("pruefungen"),
                    vorlesung.getString("credits"),
                    vorlesung.getString("note")));
        }
    }
}
