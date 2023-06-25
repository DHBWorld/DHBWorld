package com.main.dhbworld;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.main.dhbworld.Cantine.Cantine;
import com.main.dhbworld.Cantine.CantineParser;
import com.main.dhbworld.Cantine.CantineUtilities;
import com.main.dhbworld.Cantine.Meal;
import com.main.dhbworld.Cantine.MealCard;
import com.main.dhbworld.Cantine.MealDailyPlan;
import com.main.dhbworld.Utilities.RefreshCounter;
import com.main.dhbworld.Dashboard.DashboardRefresh;
import com.main.dhbworld.Navigation.NavigationUtilities;
import com.main.dhbworld.Utilities.ProgressIndicator;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class CantineActivity extends AppCompatActivity {
    private LinearLayout layoutMealCardsBasic;
    private LinearLayout layoutMealCardsExtra;
    private ProgressIndicator indicator;
    private TabLayout tabLayout;
    private Date[] currentWeek;
    private TextView pageTitleBasic;
    private TextView pageTitleExtra;
    private TextView todayIs;

    private Cantine cantine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cantine);
        setup();
        NavigationUtilities.setUpNavigation(this, R.id.cantine);
        startActivityLogic();
    }

    private void setup() {
        layoutMealCardsBasic = findViewById(R.id.layoutMealCardsBasic);
        layoutMealCardsExtra = findViewById(R.id.layoutMealCardsExtra);
        tabLayout = findViewById(R.id.tabTags);
        pageTitleBasic = findViewById(R.id.textView_pageTitleBasic_canteen);
        pageTitleExtra = findViewById(R.id.textView_pageTitleExtra_canteen);
        todayIs = findViewById(R.id.textView_todayIs_canteen);

        indicator = new ProgressIndicator(CantineActivity.this, layoutMealCardsBasic);
        cantine=new Cantine();

    }

    private void startActivityLogic() {
        indicator.show();
        if (!NetworkAvailability.check(CantineActivity.this)) {
            loadDisplay(getResources().getString(R.string.problemsWithInternetConnection));
        } else {
            currentWeek = CantineUtilities.generateCurrentWeek();
            showTabs();
            fillTabsWithData();
        }
    }
    private void labelTabs() {
        SimpleDateFormat f = new SimpleDateFormat("dd");

        String[] day = {getResources().getString(R.string.mo), getResources().getString(R.string.di),
                getResources().getString(R.string.mi), getResources().getString(R.string.dn),
                getResources().getString(R.string.fr)};

        for (int i = 0; i < 5; i++) {
            Objects.requireNonNull(tabLayout.getTabAt(i)).setText(day[i] + " (" + f.format(currentWeek[i]) + ")");
        }
    }

    private void showTabs() {
        labelTabs();
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        if (((c.get(Calendar.DAY_OF_WEEK) - 2) < 5) && ((c.get(Calendar.DAY_OF_WEEK) - 2) >= 0)) {
            tabLayout.selectTab(tabLayout.getTabAt((c.get(Calendar.DAY_OF_WEEK) - 2)));
            try {
                mealPlanFromOpenMensa(currentWeek[(c.get(Calendar.DAY_OF_WEEK) - 2)]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            tabLayout.selectTab(tabLayout.getTabAt(0));
            try {
                mealPlanFromOpenMensa(currentWeek[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void fillTabsWithData() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                try {
                    indicator.show();
                    mealPlanFromOpenMensa(currentWeek[tab.getPosition()]);
                } catch (IOException e) {
                    loadDisplay(getResources().getString(R.string.dataLoadProblem));
                    e.printStackTrace();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private void loadLayout(MealDailyPlan mealDailyPlan, Date today) {
        prepareLayout();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        todayIs.setText(format.format(today));
        for (Meal meal:mealDailyPlan.getMeals()) {

            if ((mealDailyPlan.getMainCourses().contains(meal))) { // Trennung: Hauptgericht oder Sonstiges
                layoutMealCardsBasic.addView(new MealCard(CantineActivity.this, meal));
            } else {
                layoutMealCardsExtra.addView(new MealCard(CantineActivity.this,meal));
            }

        }

    }

    private void prepareLayout(){
        pageTitleExtra.setVisibility(View.VISIBLE);
        pageTitleBasic.setVisibility(View.VISIBLE);
        todayIs.setVisibility(View.VISIBLE);
        layoutMealCardsBasic.removeAllViews();
        layoutMealCardsExtra.removeAllViews();
    }

    private void mealPlanFromOpenMensa(Date date) throws IOException {
        indicator.show();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        new CantineParser().requestDay(calendar).setResultListener(new CantineParser.ResultListener() {
            @Override
            public void onSuccess(String response) {
                layoutMealCardsBasic.post(() -> {
                    try {
                        MealDailyPlan mealDailyPlan = new MealDailyPlan(response);
                        loadLayout(mealDailyPlan, date);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        loadDisplay(getResources().getString(R.string.problemsWithOpenMensa));
                    }
                });
            }

            @Override
            public void onFailure(int resultCode, Exception e) {
                System.out.println(resultCode);
                if (e != null) {
                    e.printStackTrace();
                }

                if (resultCode != CantineParser.SUCCESS && resultCode != CantineParser.NO_DATA_AVAILABLE) {
                    layoutMealCardsBasic.post(() -> loadDisplay(getResources().getString(R.string.dataLoadProblem)));
                    return;
                }

                layoutMealCardsBasic.post(() -> {
                    SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
                    loadDisplay(getString(R.string.thereIsNoData) + " " + f.format(date) + " " + getString(R.string.cantineIsProbablyClosed));
                });
            }
        }).startAsync();
    }

    private void loadDisplay(String message) {
        pageTitleExtra.setVisibility(View.GONE);
        pageTitleBasic.setVisibility(View.GONE);
        todayIs.setVisibility(View.GONE);
        layoutMealCardsBasic.removeAllViews();
        layoutMealCardsExtra.removeAllViews();
        TextView errorView = new TextView(CantineActivity.this);
        errorView.setTextSize(18);
        errorView.setText(message);
        errorView.setPadding(0, 0, 0, 15);
        errorView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutMealCardsBasic.addView(errorView);
    }

    public static List<String> loadDataForDashboard() {
        List<String> meals = new ArrayList<>();
        String result = new CantineParser().requestDay(Calendar.getInstance()).setResultListener(new CantineParser.ResultListener() {
            @Override
            public void onSuccess(String response) {
            }

            @Override
            public void onFailure(int resultCode, Exception e) {
                System.out.println(resultCode);
                if (e != null) {
                    e.printStackTrace();
                }
            }
        }).start();

        try {
            MealDailyPlan plan = new MealDailyPlan(result);
            meals = plan.getMainCourseNamesWithSalat();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return meals;
    }

    public void refreshClick(@NonNull MenuItem item) throws NullPointerException {
       boolean doRefresh = DashboardRefresh.statusCheck(false, cantine.getRefreshStatus(), this.findViewById(android.R.id.content), this);

        if (doRefresh) {
            new RefreshCounter(cantine);
            startActivityLogic();
        }

    }

}