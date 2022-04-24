package com.main.dhbworld;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.util.IOUtils;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.progressindicator.BaseProgressIndicatorSpec;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;


import com.main.dhbworld.CantineClasses.Meal;
import com.main.dhbworld.CantineClasses.MealDailyPlan;
import com.main.dhbworld.Navigation.NavigationUtilities;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

public class CantineActivity extends AppCompatActivity {


    private LinearLayout layoutMealCardsBasic;
    private LinearLayout layoutMealCardsExtra;
    private CircularProgressIndicator progressIndicator;
    private ScrollView scroll;
    private String inputFromApi;
    TabLayout tabLayout;
    Date[] currentWeek;

    static String inputForDashboard;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cantine);
        layoutMealCardsBasic= findViewById(R.id.layoutMealCardsBasic);
        layoutMealCardsExtra= findViewById(R.id.layoutMealCardsExtra);
        tabLayout= findViewById(R.id.tabTags);
        NavigationUtilities.setUpNavigation(this,R.id.cantine);



        loadProgressIndikator();

        generateCurrentWeek();
        showTabs();






        scroll= findViewById(R.id.scrollViewCantine);

        final AtomicReference<Float>[] x1 = new AtomicReference[]{new AtomicReference<>((float) 0)};
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
        });




        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                try {

                    switch (tab.getPosition()){
                        case 0:
                            loadProgressIndikator();
                            mealPlanFromOpenMensa(currentWeek[0]);
                            break;
                        case 1:
                            loadProgressIndikator();
                            mealPlanFromOpenMensa(currentWeek[1]);
                            break;
                        case 2:
                            loadProgressIndikator();
                            mealPlanFromOpenMensa(currentWeek[2]);
                            break;
                        case 3:
                            loadProgressIndikator();
                            mealPlanFromOpenMensa(currentWeek[3]);
                            break;
                        case 4:
                            loadProgressIndikator();
                            mealPlanFromOpenMensa(currentWeek[4]);
                            break;
                    }
                } catch ( IOException e) {
                            loadDisplay("Es ist ein Fehler aufgetaucht...");
                            e.printStackTrace();
                        }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });





    }

    private void generateCurrentWeek()  {
        Date date= new Date();
        currentWeek = new Date[5];
        Calendar c= Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        c.setTime(date);


        Integer dayOfWeek = c.get(Calendar.DAY_OF_WEEK); // dayOfWeek= 2 ->Mo
        dayOfWeek=dayOfWeek-2; // dayOfWeek= 0 ->Mo
        if ((dayOfWeek<0) || (dayOfWeek>4)){
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
        tabLayout.getTabAt(0).setText("Mo ("+f.format(currentWeek[0])+")");
        tabLayout.getTabAt(1).setText("Di ("+f.format(currentWeek[1])+")");
        tabLayout.getTabAt(2).setText("Mi ("+f.format(currentWeek[2])+")");
        tabLayout.getTabAt(3).setText("Do ("+f.format(currentWeek[3])+")");
        tabLayout.getTabAt(4).setText("Fr ("+f.format(currentWeek[4])+")");

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

    private void loadLayout (MealDailyPlan mealDailyPlan, Date today){

        layoutMealCardsBasic.removeAllViews();
        layoutMealCardsExtra.removeAllViews();
        LinearLayout titleLayout = new LinearLayout(CantineActivity.this);
        titleLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        layoutMealCardsBasic.addView(titleLayout);

        TextView pageTitleBasic = new TextView(CantineActivity.this);
        pageTitleBasic.setTextSize(25);
        pageTitleBasic.setTextColor(getResources().getColor(R.color.black));
        pageTitleBasic.setText("Hauptgerichte");
        pageTitleBasic.setPadding(0,0,0,15);
        pageTitleBasic.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        titleLayout.addView(pageTitleBasic);

        TextView todayIs = new TextView(CantineActivity.this);
        todayIs.setTextSize(18);
        todayIs.setTextColor(getResources().getColor(R.color.grey_light));
        SimpleDateFormat format =new SimpleDateFormat("dd.MM.yyyy");
        todayIs.setText(format.format(today));
        todayIs.setPadding(250,0,0,15);
        todayIs.setGravity(Gravity.RIGHT);
        todayIs.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        titleLayout.addView(todayIs);

        TextView pageTitleExtra = new TextView(CantineActivity.this);
        pageTitleExtra.setTextSize(25);
        pageTitleExtra.setTextColor(getResources().getColor(R.color.black));
        pageTitleExtra.setText("Sonstiges");
        pageTitleExtra.setPadding(0,0,0,15);

        pageTitleExtra.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutMealCardsExtra.addView(pageTitleExtra);

        for (int i=0;i<mealDailyPlan.getMeal().length; i++){
            MaterialCardView mealCard= new MaterialCardView(CantineActivity.this);
            mealCard.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mealCard.setStrokeColor(getResources().getColor(R.color.grey_dark));

            if(mealDailyPlan.getMeal()[i].getCategory().equals("Wahlessen 3")){
                layoutMealCardsExtra.addView(mealCard);
            }else{
                layoutMealCardsBasic.addView(mealCard);
            }


            LinearLayout cardLayout = new LinearLayout(CantineActivity.this);
            cardLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            cardLayout.setOrientation(LinearLayout.HORIZONTAL);
            cardLayout.setPadding(0,20,0,20);
            mealCard.addView(cardLayout);

            LinearLayout mealLayout = new LinearLayout(CantineActivity.this);
            mealLayout.setLayoutParams(new ViewGroup.LayoutParams(810, ViewGroup.LayoutParams.WRAP_CONTENT));
            mealLayout.setOrientation(LinearLayout.VERTICAL);
            mealLayout.setPadding(0,0,20,0);
            cardLayout.addView(mealLayout);

            TextView mealView = new TextView(CantineActivity.this);
            mealView.setTextSize(15);
            mealView.setTextColor(getResources().getColor(R.color.black));
            mealView.setText(mealDailyPlan.getMeal()[i].getName());
            mealView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
            preisLayout.setLayoutParams(new ViewGroup.LayoutParams(150, ViewGroup.LayoutParams.MATCH_PARENT));
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

    loadProgressIndikator();

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {


                SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd");
                URL urlOpenMensa= null;
                try {
                    urlOpenMensa = new URL("https://openmensa.org/api/v2/canteens/33/days/"+format.format(date)+"/meals");
                    //urlOpenMensa = new URL("https://openmensa.org/api/v2/canteens/33/days/2021-10-25/meals");
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
                                    loadDisplay("Es kann keine Verbindung mit \"OpenMensa\" hergestellt werden.");
                                }

                            }
                        });

                    }else{
                        inputFromApi=null;
                        layoutMealCardsBasic.post(new Runnable() {
                            @Override
                            public void run() {
                                SimpleDateFormat f =new SimpleDateFormat("dd.MM.yyyy");
                                loadDisplay("Es sind keine Daten für "+f.format(date)+" gefunden.\n\nDie Kantine ist an diesem Tag wahrscheinlich geschlossen.  ");
                            }
                        });

                    }
                } catch (IOException  e) {
                    layoutMealCardsBasic.post(new Runnable() {
                        @Override
                        public void run() {
                            loadDisplay("Es ist ein Fehler aufgetaucht...");
                        }
                    });
                    e.printStackTrace();

                }

            }

        }).start();


    }

    private void loadDisplay(String message)  {

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

    private void loadProgressIndikator(){

        if (layoutMealCardsBasic.getChildCount()==0){
        progressIndicator = new CircularProgressIndicator(CantineActivity.this);
        progressIndicator.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        progressIndicator.setIndeterminate(true);
        layoutMealCardsBasic.addView(progressIndicator);
        progressIndicator.setVisibility(View.GONE);
        progressIndicator.setVisibility(View.VISIBLE);
        }
    }

    public static MealDailyPlan getMealPlanForDashboard() throws JSONException {
        MealDailyPlan plan= new MealDailyPlan(inputForDashboard);


        return plan;
    }

    public static void loadDataForDashboard() {


        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {

                Date now= new Date();
                SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd");
                URL urlOpenMensa= null;

                try {


                        urlOpenMensa = new URL("https://openmensa.org/api/v2/canteens/33/days/" + format.format(now) + "/meals");

                    HttpsURLConnection connection = (HttpsURLConnection) urlOpenMensa.openConnection();

                    if (connection.getResponseCode() == 200) { // something wrong
                        InputStream responseBody = connection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");

                        inputForDashboard = new BufferedReader(responseBodyReader).lines().collect(Collectors.joining("\n"));

                    }


                } catch (IOException e) {
                e.printStackTrace();
            }
            }

        }).start();





    }


}