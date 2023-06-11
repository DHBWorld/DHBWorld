package com.main.dhbworld.Dualis.view.tabs.semester;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.main.dhbworld.Dualis.parser.DualisAPI;
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

public class DualisSemesterFragment extends Fragment implements DualisAPI.CourseDataListener {

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

    public DualisSemesterFragment() {
        this.arguments = "";
        this.cookies = new ArrayList<>();
    }
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

        cookieHandler = CookieManager.getDefault();

        dualisAPI = new DualisAPI(getContext(), arguments, cookieHandler);

        if (cookies.size() == 0) {
            if (getActivity() != null) {
                Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.error_getting_kvv_data), BaseTransientBottomBar.LENGTH_SHORT).show();
            }
            return;
        }

        CookieManager cookieManager = new CookieManager();
        try {
            cookieManager.getCookieStore().add(new URI("dualis.dhbw.de"), cookies.get(0));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        makeCourseRequest();
    }

    public void makeCourseRequest() {
        mainLayout.setVisibility(View.INVISIBLE);
        mainProgressIndicator.setVisibility(View.VISIBLE);
        dualisAPI.requestClass(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dualis_semester_view, container, false);
    }

    @Override
    public void onCourseDataLoaded(JSONObject data) {
        ArrayList<String> items = new ArrayList<>();
        try {
            DualisAPI.compareAndSave(getContext(), data);
            for (int i=0; i<data.getJSONArray("semester").length(); i++) {
                JSONObject semester = data.getJSONArray("semester").getJSONObject(i);
                items.add(semester.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_list_item, items);
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

    @Override
    public void onError(Exception error) {
        Snackbar.make(DualisSemesterFragment.this.getActivity().findViewById(android.R.id.content), getString(R.string.error_with_message, error.toString()), BaseTransientBottomBar.LENGTH_LONG).show();
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
