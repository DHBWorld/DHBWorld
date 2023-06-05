package com.main.dhbworld;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.tabs.TabLayout;
import org.json.JSONException;

import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.main.dhbworld.Cantine.CantineParser;
import com.main.dhbworld.Cantine.CantineUtilities;
import com.main.dhbworld.Cantine.Meal;
import com.main.dhbworld.Cantine.MealDailyPlan;
import com.main.dhbworld.Navigation.NavigationUtilities;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.net.ssl.HttpsURLConnection;


public class CantineActivity extends AppCompatActivity {
    private LinearLayout layoutMealCardsBasic;
    private LinearLayout layoutMealCardsExtra;
    private String inputFromApi;
    private  ProgressIndicator indicator;
    TabLayout tabLayout;
    Date[] currentWeek;
    static String inputForDashboard;
    private TextView pageTitleBasic;
    private TextView pageTitleExtra;
    private  TextView todayIs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cantine);
        layoutMealCardsBasic= findViewById(R.id.layoutMealCardsBasic);
        layoutMealCardsExtra= findViewById(R.id.layoutMealCardsExtra);
        tabLayout= findViewById(R.id.tabTags);
        pageTitleBasic = findViewById(R.id.textView_pageTitleBasic_canteen);
        pageTitleExtra = findViewById(R.id.textView_pageTitleExtra_canteen);
        todayIs = findViewById(R.id.textView_todayIs_canteen);


        NavigationUtilities.setUpNavigation(this,R.id.cantine);

        indicator= new ProgressIndicator(CantineActivity.this, layoutMealCardsBasic);
        indicator.show();
        if (!NetworkAvailability.check(CantineActivity.this)){
            loadDisplay(getResources().getString(R.string.problemsWithInternetConnection));
        }else{
            currentWeek= CantineUtilities.generateCurrentWeek();
            showTabs();
            fillTabsWithData();
        }
    }



    private void showTabs (){
        SimpleDateFormat f =new SimpleDateFormat("dd");
        tabLayout.getTabAt(0).setText(getResources().getString(R.string.mo)+" ("+f.format(currentWeek[0])+")");
        tabLayout.getTabAt(1).setText(getResources().getString(R.string.di)+" ("+f.format(currentWeek[1])+")");
        tabLayout.getTabAt(2).setText(getResources().getString(R.string.mi)+" ("+f.format(currentWeek[2])+")");
        tabLayout.getTabAt(3).setText(getResources().getString(R.string.dn)+" ("+f.format(currentWeek[3])+")");
        tabLayout.getTabAt(4).setText(getResources().getString(R.string.fr)+" ("+f.format(currentWeek[4])+")");
        Calendar c= Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        if (((c.get(Calendar.DAY_OF_WEEK)-2)<5) &&((c.get(Calendar.DAY_OF_WEEK)-2)>=0)){
            tabLayout.selectTab(tabLayout.getTabAt((c.get(Calendar.DAY_OF_WEEK)-2)));
            try {
                mealPlanFromOpenMensa(currentWeek[(c.get(Calendar.DAY_OF_WEEK)-2)]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            tabLayout.selectTab(tabLayout.getTabAt(0));
            try {
                mealPlanFromOpenMensa(currentWeek[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void fillTabsWithData(){
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                try {
                    switch (tab.getPosition()){
                        case 0:
                            indicator.show();
                            mealPlanFromOpenMensa(currentWeek[0]);
                            break;
                        case 1:
                            indicator.show();
                            mealPlanFromOpenMensa(currentWeek[1]);
                            break;
                        case 2:
                            indicator.show();
                            mealPlanFromOpenMensa(currentWeek[2]);
                            break;
                        case 3:
                            indicator.show();
                            mealPlanFromOpenMensa(currentWeek[3]);
                            break;
                        case 4:
                            indicator.show();
                            mealPlanFromOpenMensa(currentWeek[4]);
                            break;
                    }
                } catch ( IOException e) {
                    loadDisplay(getResources().getString(R.string.dataLoadProblem));
                    e.printStackTrace();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private void loadLayout (MealDailyPlan mealDailyPlan, Date today){
        pageTitleExtra.setVisibility(View.VISIBLE);
        pageTitleBasic.setVisibility(View.VISIBLE);
        todayIs.setVisibility(View.VISIBLE);
        layoutMealCardsBasic.removeAllViews();
        layoutMealCardsExtra.removeAllViews();
        SimpleDateFormat format =new SimpleDateFormat("dd.MM.yyyy");
        todayIs.setText(format.format(today));
        for (int i=0;i<mealDailyPlan.getMeal().length; i++){
            MaterialCardView mealCard= new MaterialCardView(CantineActivity.this);
            mealCard.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mealCard.setStrokeColor(getResources().getColor(R.color.grey_dark));
            TypedValue value = new TypedValue();
            this.getTheme().resolveAttribute(R.attr.dashboardCardBackground, value, true);
            mealCard.setBackgroundColor(value.data);
            //mealCard.setElevation(0);

            if((mealDailyPlan.getMainCourses().contains(mealDailyPlan.getMeal()[i]))) { // Trennung: Hauptgericht oder Sonstiges
                layoutMealCardsBasic.addView(mealCard);
            }else{
                layoutMealCardsExtra.addView(mealCard);
            }

            LinearLayout cardLayout = new LinearLayout(CantineActivity.this);
            cardLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            cardLayout.setOrientation(LinearLayout.HORIZONTAL);
            cardLayout.setPadding(0,20,0,20);
            mealCard.addView(cardLayout);
            LinearLayout mealLayout = new LinearLayout(CantineActivity.this);
            LinearLayout.LayoutParams mealLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            mealLayoutParams.weight = 1f;
            mealLayout.setLayoutParams(mealLayoutParams);
            mealLayout.setOrientation(LinearLayout.VERTICAL);
            mealLayout.setPadding(0,0,10,0);
            cardLayout.addView(mealLayout);
            TextView mealView = new TextView(CantineActivity.this);
            mealView.setTextSize(15);
            //mealView.setTextColor(getResources().getColor(R.color.black));
            mealView.setText(mealDailyPlan.getMeal()[i].getName());
            mealView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mealLayout.addView(mealView);
            HorizontalScrollView chipScroll = new HorizontalScrollView(CantineActivity.this);
            chipScroll.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            chipScroll.setHorizontalScrollBarEnabled(false);
            chipScroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
            mealLayout.addView(chipScroll);
            LinearLayout chipLayout = new LinearLayout(CantineActivity.this);
            chipLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            chipLayout.setOrientation(LinearLayout.HORIZONTAL);
            chipScroll.addView(chipLayout);
            if (mealDailyPlan.getMeal()[i].getNotes().size()==0){
                Chip chip = new Chip(CantineActivity.this);
                chip.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                chip.setTextSize(12);
                chip.setText(" ");
                chip.setVisibility(View.INVISIBLE);
                chipLayout.addView(chip);
            }
            for (int j=0; j<mealDailyPlan.getMeal()[i].getNotes().size(); j++){
                Chip chip = new Chip(CantineActivity.this);
                chip.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                chip.setText(mealDailyPlan.getMeal()[i].getNotes().get(j));
                chip.setTextSize(12);
                chip.setGravity(Gravity.CENTER);
                chip.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                chip.setCheckable(false);
                chip.setClickable(false);
                chipLayout.addView(chip);
            }
            LinearLayout preisLayout = new LinearLayout(CantineActivity.this);
            preisLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            preisLayout.setPadding(10, 0, 10, 0);
            preisLayout.setOrientation(LinearLayout.VERTICAL);

            TypedValue valuePreisBg = new TypedValue();
            CantineActivity.this.getTheme().resolveAttribute(R.attr.dashboardCardBackgroundElevated, valuePreisBg, true);
            preisLayout.setBackgroundColor(valuePreisBg.data);
            preisLayout.setGravity(Gravity.CENTER);
            cardLayout.addView(preisLayout);
            TextView preisView = new TextView(CantineActivity.this);
            preisView.setTextSize(18);
            //preisView.setTextColor(getResources().getColor(R.color.grey_dark));
            preisView.setText(formatMealPrice(mealDailyPlan.getMeal()[i]));
            preisView.setGravity(Gravity.CENTER);
            preisView.setMaxLines(1);
            preisView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            preisLayout.addView(preisView);
            }

    }

    private String formatMealPrice(Meal meal) {
        try {
            double price = Double.parseDouble(meal.getPrice());
            NumberFormat format = NumberFormat.getCurrencyInstance();
            format.setCurrency(Currency.getInstance("EUR"));
            return format.format(price);
        } catch (NumberFormatException e) {
            return "-";
        }
    }

    private void mealPlanFromOpenMensa(Date date) throws IOException {
        indicator.show();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        new CantineParser().requestDay(calendar).setResultListener(new CantineParser.ResultListener() {
            @Override
            public void onSuccess(String response) {
                layoutMealCardsBasic.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MealDailyPlan mealDailyPlan = new MealDailyPlan(response);
                            loadLayout(mealDailyPlan, date);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            loadDisplay(getResources().getString(R.string.problemsWithOpenMensa));
                        }
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
                    layoutMealCardsBasic.post(new Runnable() {
                        @Override
                        public void run() {
                            loadDisplay(getResources().getString(R.string.dataLoadProblem));
                        }
                    });
                    return;
                }

                layoutMealCardsBasic.post(new Runnable() {
                    @Override
                    public void run() {
                        SimpleDateFormat f =new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
                        loadDisplay(getString(R.string.thereIsNoData)+" "+f.format(date)+" "+getString(R.string.cantineIsProbablyClosed));
                    }
                });
            }
        }).startAsync();
    }

    private void loadDisplay(String message)  {
        pageTitleExtra.setVisibility(View.GONE);
        pageTitleBasic.setVisibility(View.GONE);
        todayIs.setVisibility(View.GONE);
        layoutMealCardsBasic.removeAllViews();
        layoutMealCardsExtra.removeAllViews();
        TextView errorView = new TextView(CantineActivity.this);
        errorView.setTextSize(18);
        errorView.setText(message);
        errorView.setPadding(0,0,0,15);
        errorView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutMealCardsBasic.addView(errorView);
    }

    public static List<String> loadDataForDashboard() {
        List <String> meals= new ArrayList<>();

        String result = new CantineParser().requestDay(Calendar.getInstance()).setResultListener(new CantineParser.ResultListener() {
            @Override
            public void onSuccess(String response) { }

            @Override
            public void onFailure(int resultCode, Exception e) {
                System.out.println(resultCode);
                if (e != null) {
                    e.printStackTrace();
                }
            }
        }).start();

        try {
            MealDailyPlan plan= new MealDailyPlan(result);
            meals= plan.getMainCourseNamesWithSalat();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return meals;
    }

    public void refreshClick(@NonNull MenuItem item) throws NullPointerException{
        indicator.show();
        if (!NetworkAvailability.check(CantineActivity.this)){
            loadDisplay(getResources().getString(R.string.problemsWithInternetConnection));
        }else{
            currentWeek= CantineUtilities.generateCurrentWeek();
            showTabs();
            fillTabsWithData();
        }
    }
}