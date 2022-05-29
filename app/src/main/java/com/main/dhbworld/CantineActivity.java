package com.main.dhbworld;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.tabs.TabLayout;
import org.json.JSONException;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.main.dhbworld.Cantine.MealDailyPlan;
import com.main.dhbworld.Navigation.NavigationUtilities;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
        generateCurrentWeek();
        showTabs();
        fillTabsWithData();
        }
    }

    private void generateCurrentWeek()  {
        Date date= new Date();
        currentWeek = new Date[5];
        Calendar c= Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        c.setTime(date);
        Integer dayOfWeek = c.get(Calendar.DAY_OF_WEEK); // dayOfWeek= 2 ->Mo
        dayOfWeek=dayOfWeek-2; // dayOfWeek= 0 ->Mo
        // Am Samstag
        if ( (dayOfWeek>4)){
            dayOfWeek=0;
            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            c.add(Calendar.DATE, 7);
           System.out.println(c.get(Calendar.DAY_OF_MONTH));
        }
        //Am Sonntag
        if ((dayOfWeek<0) ){
            dayOfWeek=0;
            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        }
        for (int q=dayOfWeek; q<5;q++){
            currentWeek[q]=c.getTime();
            c.add(Calendar.DATE, 1);
        }
        c.add(Calendar.DATE, 2);
        for (int q=0; q<dayOfWeek;q++){
            currentWeek[q]=c.getTime();
            c.add(Calendar.DATE, 1);
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
        //Scrollen zwischen den Tabs
       /* final AtomicReference<Float>[] x1 = new AtomicReference[]{new AtomicReference<>((float) 0)};
        final float[] x2 = {0};
        scroll.setOnTouchListener((v, event) -> {
            // TODO Auto-generated method stub
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x1[0].set(event.getX());
                    break;
                case MotionEvent.ACTION_UP:
                    x2[0] = event.getX();
                    System.out.println(x1[0] + "     " + x2[0]);
                    float deltaX = x2[0] - x1[0].get();
                    if (deltaX < -100) {
                        tabLayout.selectTab(tabLayout.getTabAt(tabLayout.getSelectedTabPosition()+1));
                        return true;
                    }else if(deltaX > 100){
                        tabLayout.selectTab(tabLayout.getTabAt(tabLayout.getSelectedTabPosition()-1));
                        return true;
                    }
                    break;
            }
            return false;
        });*/
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
        boolean basicMealOne=true;
        boolean basicMealTwo=true;
        boolean basicMealThree=true;
        for (int i=0;i<mealDailyPlan.getMeal().length; i++){
            MaterialCardView mealCard= new MaterialCardView(CantineActivity.this);
            mealCard.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mealCard.setStrokeColor(getResources().getColor(R.color.grey_dark));
            mealCard.setElevation(0);
            // "Künstliche Intelligenz" zur Erkennunung von Haupgerichten
            if((mealDailyPlan.getMeal()[i].getCategory().equals(getString(R.string.typeMealOne))) && basicMealOne){
                layoutMealCardsBasic.addView(mealCard);
                basicMealOne=false;
            }else if((mealDailyPlan.getMeal()[i].getCategory().equals(getString(R.string.typeMealTwo))) && basicMealTwo){
                layoutMealCardsBasic.addView(mealCard);
                basicMealTwo=false;
            }else if((mealDailyPlan.getMeal()[i].getCategory().equals(getString(R.string.typeMealThree))) && basicMealThree  ){
                try {
                    Double price= Double.parseDouble(mealDailyPlan.getMeal()[i].getPrice());

                    if (price>1.80){
                        layoutMealCardsBasic.addView(mealCard);
                        basicMealThree=false;
                    }else{
                        layoutMealCardsExtra.addView(mealCard);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    layoutMealCardsBasic.addView(mealCard);
                    basicMealThree=false;
                }
            }else{
                layoutMealCardsExtra.addView(mealCard);}
            //----------------------
            LinearLayout cardLayout = new LinearLayout(CantineActivity.this);
            cardLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            cardLayout.setOrientation(LinearLayout.HORIZONTAL);
            cardLayout.setPadding(0,20,0,20);
            mealCard.addView(cardLayout);
            LinearLayout mealLayout = new LinearLayout(CantineActivity.this);
            mealLayout.setLayoutParams(new ViewGroup.LayoutParams(pageTitleExtra.getWidth()-120, ViewGroup.LayoutParams.WRAP_CONTENT));
            mealLayout.setOrientation(LinearLayout.VERTICAL);
            mealLayout.setPadding(0,0,10,0);
            cardLayout.addView(mealLayout);
            TextView mealView = new TextView(CantineActivity.this);
            mealView.setTextSize(15);
            mealView.setTextColor(getResources().getColor(R.color.black));
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
            for (int j=0; j<mealDailyPlan.getMeal()[i].getNotes().size(); j++){
                Chip chip = new Chip(CantineActivity.this);
                chip.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                chip.setText(mealDailyPlan.getMeal()[i].getNotes().get(j));
                chip.setTextSize(12);
                chip.setGravity(Gravity.CENTER);
                chip.setCheckable(false);
                chip.setClickable(false);
                chipLayout.addView(chip);
            }
            LinearLayout preisLayout = new LinearLayout(CantineActivity.this);
            preisLayout.setLayoutParams(new ViewGroup.LayoutParams(120, ViewGroup.LayoutParams.MATCH_PARENT));
            preisLayout.setOrientation(LinearLayout.VERTICAL);
            preisLayout.setBackgroundColor(getResources().getColor(R.color.grey_light));
            preisLayout.setGravity(Gravity.CENTER);
            cardLayout.addView(preisLayout);
            TextView preisView = new TextView(CantineActivity.this);
            preisView.setTextSize(18);
            preisView.setTextColor(getResources().getColor(R.color.grey_dark));
            preisView.setText(mealDailyPlan.getMeal()[i].getPrice());
            preisView.setGravity(Gravity.CENTER);
            preisView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            preisLayout.addView(preisView);
            }

    }

    private void mealPlanFromOpenMensa(Date date) throws IOException {
        indicator.show();
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd");
                URL urlOpenMensa= null;
                try {
                    urlOpenMensa = new URL("https://openmensa.org/api/v2/canteens/33/days/"+format.format(date)+"/meals");
                    HttpsURLConnection connection=(HttpsURLConnection) urlOpenMensa.openConnection();
                    if (connection.getResponseCode() ==200){ // something wrong
                        InputStream responseBody = connection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                        inputFromApi= new BufferedReader( responseBodyReader).lines().collect(Collectors.joining("\n"));
                        layoutMealCardsBasic.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    MealDailyPlan mealDailyPlan = new MealDailyPlan( inputFromApi);
                                    loadLayout(mealDailyPlan, date);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    loadDisplay(getResources().getString(R.string.problemsWithOpenMensa));
                                }
                            }
                        });
                    }else{
                        inputFromApi=null;
                        layoutMealCardsBasic.post(new Runnable() {
                            @Override
                            public void run() {
                                SimpleDateFormat f =new SimpleDateFormat("dd.MM.yyyy");
                                loadDisplay(getString(R.string.thereIsNoData)+" "+f.format(date)+" "+getString(R.string.cantineIsProbablyClosed));
                            }
                        });
                    }
                } catch (IOException  e) {
                    layoutMealCardsBasic.post(new Runnable() {
                        @Override
                        public void run() {
                            loadDisplay(getResources().getString(R.string.dataLoadProblem));
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void loadDisplay(String message)  {
        pageTitleExtra.setVisibility(View.GONE);
        pageTitleBasic.setVisibility(View.GONE);
        todayIs.setVisibility(View.GONE);
        layoutMealCardsBasic.removeAllViews();
        layoutMealCardsExtra.removeAllViews();
        TextView errorView = new TextView(CantineActivity.this);
        errorView.setTextSize(18);
        errorView.setTextColor(getResources().getColor(R.color.black));
        errorView.setText(message);
        errorView.setPadding(0,0,0,15);
        errorView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutMealCardsBasic.addView(errorView);
    }

    public static List<String> loadDataForDashboard() {
        List <String> meals= new ArrayList<>();
                Date now= new Date();
                SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd");
                URL urlOpenMensa= null;
                try { urlOpenMensa = new URL("https://openmensa.org/api/v2/canteens/33/days/" + format.format(now) + "/meals");
                        HttpsURLConnection connection = (HttpsURLConnection) urlOpenMensa.openConnection();
                    if (connection.getResponseCode() == 200) { // something wrong
                        InputStream responseBody = connection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                        inputForDashboard = new BufferedReader(responseBodyReader).lines().collect(Collectors.joining("\n"));

                        MealDailyPlan plan= new MealDailyPlan(inputForDashboard);
                        meals= plan.getMainCourseNames();
                    }
                } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
                return meals;
    }

    public void refreshClick(@NonNull MenuItem item) throws NullPointerException{
        indicator.show();
        if (!NetworkAvailability.check(CantineActivity.this)){
            loadDisplay(getResources().getString(R.string.problemsWithInternetConnection));
        }else{
            generateCurrentWeek();
            showTabs();
            fillTabsWithData();
        }
    }
}