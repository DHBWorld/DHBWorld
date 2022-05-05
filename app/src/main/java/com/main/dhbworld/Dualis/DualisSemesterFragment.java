package com.main.dhbworld.Dualis;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.android.volley.VolleyError;
import com.google.android.material.progressindicator.CircularProgressIndicator;
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
import java.util.concurrent.TimeUnit;

public class DualisSemesterFragment extends Fragment implements DualisAPI.CourseDataLoadedListener, DualisAPI.CourseErrorListener {

    private final String arguments;
    private final List<HttpCookie> cookies;

    private AutoCompleteTextView semesterDropdown;
    private String currentSemester = "";
    private List<VorlesungModel> vorlesungModels = new ArrayList<>();
    private VorlesungAdapter vorlesungAdapter;
    private static CircularProgressIndicator mainProgressIndicator;
    private static LinearLayout mainLayout;

    private static DualisAPI dualisAPI;
    private static CookieHandler cookieHandler;

    View mainView;

    public DualisSemesterFragment(String arguments, List<HttpCookie> cookies) {
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

        semesterDropdown = mainView.findViewById(R.id.autoComplete);
        mainProgressIndicator = mainView.findViewById(R.id.progress_main);
        mainLayout = mainView.findViewById(R.id.main_layout);

        dualisAPI = new DualisAPI();
        dualisAPI.setOnCourseDataLoadedListener(this);
        dualisAPI.setOnCourseErrorListener(this);

        CookieManager cookieManager = new CookieManager();
        try {
            cookieManager.getCookieStore().add(new URI("dualis.dhbw.de"), cookies.get(0));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        cookieHandler = CookieManager.getDefault();

        makeCourseRequest(getContext(), arguments);
    }

    static void makeCourseRequest(Context context, String arguments) {
        mainLayout.setVisibility(View.INVISIBLE);
        mainProgressIndicator.setVisibility(View.VISIBLE);
        dualisAPI.makeClassRequest(context, arguments, cookieHandler);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dualis_semester_view, container, false);
    }

    @Override
    public void onCourseDataLoaded(JSONObject data) {
        setAlarmManager(getContext());
        DualisAPI.compareAndSave(getContext(), data);

        ArrayList<String> items = new ArrayList<>();
        try {
            for (int i=0; i<data.getJSONArray("semester").length(); i++) {
                JSONObject semester = data.getJSONArray("semester").getJSONObject(i);
                items.add(semester.getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.dualis_semester_list_item, items);
        semesterDropdown.setAdapter(arrayAdapter);
        if (items.contains(currentSemester)) {
            semesterDropdown.setText(currentSemester, false);
        } else {
            semesterDropdown.setText(items.get(0), false);
        }

        vorlesungModels = new ArrayList<>();
        RecyclerView mRecyclerView = mainView.findViewById(R.id.recycler_view);
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
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        vorlesungAdapter = new VorlesungAdapter(vorlesungModels, getContext());
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

    static void setAlarmManager(Context context) {
        SharedPreferences settingsPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        if (!sharedPref.getBoolean("saveCredentials", false) || !settingsPref.getBoolean("sync", true)) {
            return;
        }

        int time = Integer.parseInt(settingsPref.getString("sync_time", "15"));

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(BackgroundWorker.class, time, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();

        WorkManager workManager = WorkManager.getInstance(context.getApplicationContext());
        workManager.enqueueUniquePeriodicWork("DualisNotifier", ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);
    }

    @Override
    public void onCourseError(VolleyError error) {
        Toast.makeText(getContext(), "Error: " + error.toString(), Toast.LENGTH_LONG).show();
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
