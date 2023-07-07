package com.main.dhbworld.Dualis.view.tabs.semester;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import com.main.dhbworld.Dualis.parser.api.DualisAPI;
import com.main.dhbworld.Dualis.parser.htmlparser.semesters.course.DualisSemesterCourse;
import com.main.dhbworld.Dualis.parser.htmlparser.semesters.semester.DualisSemester;
import com.main.dhbworld.R;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class DualisSemesterFragment extends Fragment implements DualisAPI.SemesterDataListener {

    private final String arguments;
    private final List<HttpCookie> cookies;

    private Context context;
    private Activity activity;

    private AutoCompleteTextView semesterDropdown;
    private String currentSemester = "";
    private List<DualisSemesterCourse> vorlesungModels = new ArrayList<>();
    private VorlesungAdapter vorlesungAdapter;
    private CircularProgressIndicator mainProgressIndicator;
    private LinearLayout mainLayout;
    private RecyclerView mRecyclerView;

    private DualisAPI dualisAPI;
    private CookieHandler cookieHandler;

    private boolean secondTry = false;

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

        semesterDropdown = this.getView().findViewById(R.id.autoComplete);
        mainProgressIndicator = this.getView().findViewById(R.id.progress_main);
        mainLayout = this.getView().findViewById(R.id.main_layout);
        mRecyclerView = this.getView().findViewById(R.id.recycler_view);
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
        makeCourseRequest(false);
    }

    public void makeCourseRequest(boolean secondTry) {
        this.secondTry = secondTry;
        mainLayout.setVisibility(View.INVISIBLE);
        mainProgressIndicator.setVisibility(View.VISIBLE);
        dualisAPI.requestSemesters(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dualis_semester_view, container, false);
    }

    @Override
    public void onSemesterDataLoaded(ArrayList<DualisSemester> dualisSemesters) {
        boolean success = DualisAPI.compareSaveNotification(context, dualisSemesters, secondTry);
        if (!success) {
            makeCourseRequest(true);
            return;
        }

        ArrayList<String> semesterNames = getSemesterNames(dualisSemesters);
        setupSemesterDropdown(semesterNames, dualisSemesters);
        setupVorlesungModelsList(dualisSemesters, semesterNames);

        setupRecyclerView();

        mainProgressIndicator.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
    }

    private ArrayList<String> getSemesterNames(ArrayList<DualisSemester> dualisSemesters) {
        ArrayList<String> semesters = new ArrayList<>();
        for (DualisSemester dualisSemester : dualisSemesters) {
            semesters.add(dualisSemester.getName());
        }
        return semesters;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setupSemesterDropdown(ArrayList<String> semesterNames, ArrayList<DualisSemester> dualisSemesters) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.dropdown_list_item, semesterNames);
        semesterDropdown.setAdapter(arrayAdapter);
        if (semesterNames.contains(currentSemester)) {
            semesterDropdown.setText(currentSemester, false);
        } else {
            semesterDropdown.setText(semesterNames.get(0), false);
        }
        semesterDropdown.setOnItemClickListener((adapterView, view, i, l) -> {
            currentSemester = semesterDropdown.getText().toString();
            updateList(dualisSemesters.get(i).getDualisSemesterCourses());
            vorlesungAdapter.notifyDataSetChanged();
        });
        semesterDropdown.setEnabled(true);
    }

    private void setupVorlesungModelsList(ArrayList<DualisSemester> dualisSemesters, ArrayList<String> semesterNames) {
        vorlesungModels = new ArrayList<>();
        if (semesterNames.contains(currentSemester)) {
            updateList(dualisSemesters.get(semesterNames.indexOf(currentSemester)).getDualisSemesterCourses());
        } else {
            updateList(dualisSemesters.get(0).getDualisSemesterCourses());
        }
    }

    private void setupRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        vorlesungAdapter = new VorlesungAdapter(vorlesungModels, context);
        mRecyclerView.setAdapter(vorlesungAdapter);
    }

    @Override
    public void onError(Exception error) {
        Snackbar.make(activity.findViewById(android.R.id.content), getString(R.string.error_with_message, error.toString()), BaseTransientBottomBar.LENGTH_LONG).show();
    }

    void updateList(ArrayList<DualisSemesterCourse> dualisSemesterCourses) {
        vorlesungModels.clear();
        vorlesungModels.addAll(dualisSemesterCourses);
    }
}
