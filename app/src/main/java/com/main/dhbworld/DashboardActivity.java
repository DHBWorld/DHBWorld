package com.main.dhbworld;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import java.time.*;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.main.dhbworld.CantineClasses.MealDailyPlan;
import com.main.dhbworld.Enums.InteractionState;
import com.main.dhbworld.Firebase.Utilities;
import com.main.dhbworld.Navigation.NavigationUtilities;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dhbw.timetable.rapla.data.event.Appointment;
import dhbw.timetable.rapla.exceptions.NoConnectionException;
import android.os.CountDownTimer;

public class DashboardActivity extends AppCompatActivity {


    private LinearLayout layoutCardMealPlan;
    Boolean configurationModus;

    SharedPreferences sp;

    public static final String MyPREFERENCES = "myPreferencesKey" ;
    public static final String dashboardSettings="dashboardSettings";

    Boolean cardCalendar_isVisible = true;
    Boolean cardPI_isVisible = true;
    Boolean cardMealPlan_isVisible = true;
    Boolean cardKvv_isVisible = true;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        NavigationUtilities.setUpNavigation(this,R.id.dashboard);



        layoutCardMealPlan= findViewById(R.id.layoutCardMealPlan);









        userConfigurationOfDashboard();



        loadUserInteraction();
        loadMealPlan();
        loadKvv();
        loadCalendar();
        loadPersonalInformation();









    }

    private void userConfigurationOfDashboard(){

        sp = getSharedPreferences(dashboardSettings, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();




        ImageButton settings = findViewById(R.id.dashboard_settings);
        configurationModus=false;
        Button buttonCardCalendar= findViewById(R.id.buttonCardCalendar);
        Button buttonCardPI= findViewById(R.id.buttonCardPI);
        Button buttonCardMealPlan= findViewById(R.id.buttonCardMealPlan);
        Button buttonCardKvv= findViewById(R.id.buttonCardKvv);

        buttonCardCalendar.setBackgroundColor((ColorUtils.setAlphaComponent(getResources().getColor(R.color.black),0)));
        buttonCardPI.setBackgroundColor((ColorUtils.setAlphaComponent(getResources().getColor(R.color.black),0)));
        buttonCardMealPlan.setBackgroundColor((ColorUtils.setAlphaComponent(getResources().getColor(R.color.black),0)));
        buttonCardKvv.setBackgroundColor((ColorUtils.setAlphaComponent(getResources().getColor(R.color.black),0)));

        buttonCardCalendar.setVisibility(View.INVISIBLE);
        buttonCardPI.setVisibility(View.INVISIBLE);
        buttonCardMealPlan.setVisibility(View.INVISIBLE);
        buttonCardKvv.setVisibility(View.INVISIBLE);

        MaterialCardView card_dash_calendar = findViewById(R.id.card_dash_calendar);
        MaterialCardView card_dash_pi = findViewById(R.id.card_dash_pi);
        MaterialCardView card_dash_kvv = findViewById(R.id.card_dash_kvv);
        MaterialCardView card_dash_mealPlan = findViewById(R.id.card_dash_mealPlan);
        LinearLayout card_dash_calendar_layout = findViewById(R.id.card_dash_calendar_layout);
        LinearLayout card_dash_pi_layout = findViewById(R.id.card_dash_pi_layout);
        LinearLayout card_dash_kvv_layout = findViewById(R.id.card_dash_kvv_layout);
        LinearLayout card_dash_mealPlan_layout = findViewById(R.id.card_dash_mealPlan_layout);




     //   card_dash_calendar.setLayoutParams(new MaterialCardView.LayoutParams(0,0));
      //  card_dash_calendar_layout.setLayoutParams(new ViewGroup.LayoutParams(CardView.LayoutParams.MATCH_PARENT,0 ));
     //   card_dash_calendar.setMinimumHeight(0);

        // card_dash_calendar.setLayoutParams(new MaterialCardView.LayoutParams(0,0));

      //  card_dash_pi.setLayoutParams(new  CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, 0));


      cardCalendar_isVisible = sp.getBoolean("cardCalendar", true);
        cardPI_isVisible = sp.getBoolean("cardPI", true);
       cardMealPlan_isVisible = sp.getBoolean("cardMealPlan", true);
        cardKvv_isVisible = sp.getBoolean("cardKvv", true);

       if (!cardCalendar_isVisible){
           card_dash_calendar.setVisibility(View.GONE);
       }
        if (!cardPI_isVisible){
            card_dash_pi.setVisibility(View.GONE);
        }
        if (!cardMealPlan_isVisible){
            card_dash_mealPlan.setVisibility(View.GONE);
        }
        if (!cardKvv_isVisible){
            card_dash_kvv.setVisibility(View.GONE);
        }








        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (configurationModus==false){ //User can configure his dashboard

                    settings.setBackground(getResources().getDrawable(R.drawable.ic_done));
                    Toast.makeText(DashboardActivity.this, " Wählen sie Cards, die ausblenden oder wieder einblenden möchten", Toast.LENGTH_LONG).show();
                    card_dash_calendar.setVisibility(View.VISIBLE);
                    card_dash_pi.setVisibility(View.VISIBLE);
                    card_dash_kvv.setVisibility(View.VISIBLE);
                    card_dash_mealPlan.setVisibility(View.VISIBLE);

                    buttonCardCalendar.setVisibility(View.VISIBLE);
                    buttonCardPI.setVisibility(View.VISIBLE);
                    buttonCardMealPlan.setVisibility(View.VISIBLE);
                    buttonCardKvv.setVisibility(View.VISIBLE);

                    if (!cardCalendar_isVisible){
                        card_dash_calendar.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_calendar.getStrokeColor(),50));
                        card_dash_calendar_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_calendar.getStrokeColor(),50));

                    }
                    if (!cardPI_isVisible){
                        card_dash_pi.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_pi.getStrokeColor(),50));
                        card_dash_pi_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_pi.getStrokeColor(),50));
                    }
                    if (!cardMealPlan_isVisible){
                        card_dash_mealPlan.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_mealPlan.getStrokeColor(),50));
                        card_dash_mealPlan_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_mealPlan.getStrokeColor(),50));
                    }
                    if (!cardKvv_isVisible){
                        card_dash_kvv.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_kvv.getStrokeColor(),50));
                        card_dash_kvv_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_kvv.getStrokeColor(),50));
                    }

                   configurationModus=true;


                } else{
                    settings.setBackground(getResources().getDrawable(R.drawable.ic_construction));
                    buttonCardCalendar.setVisibility(View.INVISIBLE);
                    buttonCardPI.setVisibility(View.INVISIBLE);
                    buttonCardMealPlan.setVisibility(View.INVISIBLE);
                    buttonCardKvv.setVisibility(View.INVISIBLE);


                    card_dash_calendar.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_calendar.getStrokeColor(),255));
                    card_dash_calendar_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_calendar.getStrokeColor(),255));
                    card_dash_pi.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_pi.getStrokeColor(),255));
                    card_dash_pi_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_pi.getStrokeColor(),255));
                    card_dash_kvv.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_kvv.getStrokeColor(),255));
                    card_dash_kvv_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_kvv.getStrokeColor(),255));
                    card_dash_mealPlan.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_mealPlan.getStrokeColor(),255));
                    card_dash_mealPlan_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_mealPlan.getStrokeColor(),255));

                    if (!cardCalendar_isVisible){
                        card_dash_calendar.setVisibility(View.GONE);
                    }
                    if (!cardPI_isVisible){
                        card_dash_pi.setVisibility(View.GONE);
                    }
                    if (!cardMealPlan_isVisible){
                        card_dash_mealPlan.setVisibility(View.GONE);
                    }
                    if (!cardKvv_isVisible){
                        card_dash_kvv.setVisibility(View.GONE);
                    }


                    editor.putBoolean("cardCalendar", cardCalendar_isVisible);
                    editor.putBoolean("cardPI", cardPI_isVisible);
                    editor.putBoolean("cardMealPlan", cardMealPlan_isVisible);
                    editor.putBoolean("cardKvv", cardKvv_isVisible);
                    configurationModus=false;

                }
            }
        });


        buttonCardCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardCalendar_isVisible){
                    cardCalendar_isVisible=false;
                    card_dash_calendar.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_calendar.getStrokeColor(),50));
                    card_dash_calendar_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_calendar.getStrokeColor(),50));
                }else{
                    cardCalendar_isVisible=true;
                    card_dash_calendar.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_calendar.getStrokeColor(),255));
                    card_dash_calendar_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_calendar.getStrokeColor(),255));
                }

            }
        });

        buttonCardPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (cardPI_isVisible){
                    cardPI_isVisible=false; //True = Card is visible
                    card_dash_pi.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_pi.getStrokeColor(),50));
                    card_dash_pi_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_pi.getStrokeColor(),50));
                }else{
                    cardPI_isVisible=true;//True = Card is visible
                    card_dash_pi.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_pi.getStrokeColor(),255));
                    card_dash_pi_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_pi.getStrokeColor(),255));
                }

            }
        });

        buttonCardMealPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardMealPlan_isVisible){
                    cardMealPlan_isVisible=false; //True = Card is visible
                    card_dash_mealPlan.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_mealPlan.getStrokeColor(),50));
                    card_dash_mealPlan_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_mealPlan.getStrokeColor(),50));
                }else{
                    cardMealPlan_isVisible=true;//True = Card is visible
                    card_dash_mealPlan.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_mealPlan.getStrokeColor(),255));
                    card_dash_mealPlan_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_mealPlan.getStrokeColor(),255));
                }

            }
        });

        buttonCardKvv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardKvv_isVisible){
                    cardKvv_isVisible=false; //True = Card is visible
                    card_dash_kvv.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_kvv.getStrokeColor(),50));
                    card_dash_kvv_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_kvv.getStrokeColor(),50));
                }else{
                    cardKvv_isVisible=true;//True = Card is visible
                    card_dash_kvv.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_kvv.getStrokeColor(),255));
                    card_dash_kvv_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_kvv.getStrokeColor(),255));
                }
            }
        });








    }

    private void loadCalendar(){
        new Thread(new Runnable() {
            @Override
            public void run() {


                try {


                    nextEventsProvider nextEventsProvider= new nextEventsProvider();
                    Appointment nextClass = nextEventsProvider.getNextEvent();

                    layoutCardMealPlan.post(new Runnable() {
                        @Override
                        public void run() {





                            LocalDateTime now=LocalDateTime.now();
                            LocalDateTime startClass=nextClass.getStartDate();
                            LocalDateTime endClass=nextClass.getEndDate();
                            Duration durationUntilStartOfClass= Duration.between(now, startClass);
                            Duration durationUntilEndOfClass= Duration.between(now, endClass);



                            LinearLayout layoutCardCalendar = findViewById(R.id.layoutCardCalendar);
                            LinearLayout layoutNextClass = new LinearLayout(DashboardActivity.this);
                            layoutNextClass.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            layoutNextClass.setOrientation(LinearLayout.HORIZONTAL);
                            layoutNextClass.setVerticalGravity(View.TEXT_ALIGNMENT_CENTER);
                            layoutNextClass.setGravity(Gravity.CENTER_VERTICAL);
                            layoutCardCalendar.addView(layoutNextClass);

                            ImageView UniImage = new ImageView(DashboardActivity.this);
                            UniImage.setLayoutParams(new ViewGroup.LayoutParams(65, 65));

                            UniImage.setPadding(0, 7, 10, 0);
                            layoutNextClass.addView(UniImage);

                            TextView nextClassView = new TextView(DashboardActivity.this);
                            nextClassView.setTextSize(15);
                            nextClassView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                            nextClassView.setGravity(Gravity.CENTER_VERTICAL);
                            nextClassView.setTextColor(getResources().getColor(R.color.black));
                            nextClassView.setPadding(0, 7, 5, 0);
                            layoutNextClass.addView(nextClassView);
                            if ((durationUntilStartOfClass.toHours()<=8) && (durationUntilEndOfClass.toMinutes()>=0)){

                                //nextClassView.setLayoutParams(new ViewGroup.LayoutParams(550, ViewGroup.LayoutParams.WRAP_CONTENT));
                                UniImage.setImageResource(R.drawable.ic_uni);
                                nextClassView.setText(nextClass.getTitle());


                                LinearLayout layoutTime = new LinearLayout(DashboardActivity.this);
                                layoutTime.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                layoutTime.setOrientation(LinearLayout.VERTICAL);
                                layoutTime.setVerticalGravity(View.TEXT_ALIGNMENT_CENTER);

                                layoutNextClass.addView(layoutTime);



                                LinearLayout layoutTimeDigit = new LinearLayout(DashboardActivity.this);
                                layoutTimeDigit.setLayoutParams(new ViewGroup.LayoutParams(200,120));
                                layoutTimeDigit.setOrientation(LinearLayout.HORIZONTAL);
                                layoutTime.setPadding(5,0,5,0);
                                layoutTime.setVerticalGravity(Gravity.CENTER);
                                layoutTime.setHorizontalGravity(Gravity.RIGHT);


                                layoutTimeDigit.setBackgroundColor(getColor(R.color.grey_defect));
                                layoutTime.addView(layoutTimeDigit);

                                TextView timeView = new TextView(DashboardActivity.this);
                                timeView.setTextSize(23);
                                timeView.setTextColor(getResources().getColor(R.color.black));
                                timeView.setText(nextClass.getStartTime());
                                timeView.setGravity(Gravity.CENTER);
                                timeView.setPadding(12, 12, 0, 0);
                                timeView.setLayoutParams(new ViewGroup.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT));
                                layoutTimeDigit.addView(timeView);



                                TextView timeViewMin = new TextView(DashboardActivity.this);
                                timeViewMin.setTextSize(12);
                                timeViewMin.setTextColor(getResources().getColor(R.color.black));
                                timeViewMin.setText("Min");
                                timeViewMin.setGravity(Gravity.CENTER);
                                timeViewMin.setPadding(0, 0, 0, 0);
                                timeViewMin.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                layoutTimeDigit.addView(timeViewMin);

                                TextView letterTimeView = new TextView(DashboardActivity.this);
                                letterTimeView.setTextSize(12);
                                letterTimeView.setTextColor(getResources().getColor(R.color.black));
                                letterTimeView.setGravity(Gravity.CENTER);

                                letterTimeView.setPadding(0, 2, 0, 5);
                                letterTimeView.setLayoutParams(new ViewGroup.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT));
                                letterTimeView.setBackgroundColor(getColor(R.color.even_lighter_gray));
                                layoutTime.addView(letterTimeView);

                                if (durationUntilStartOfClass.toMinutes()>=0){



                                    System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
                                    System.out.printf("%dD %dH %dMin%n", durationUntilStartOfClass.toDays(), durationUntilStartOfClass.toHours() % 24, durationUntilStartOfClass.toMinutes() % 60);


                                    new CountDownTimer(durationUntilStartOfClass.toMinutes() * 60000, 60000) {
                                        public void onTick(long millisUtilFinished) {
                                            timeView.setText(Long.toString(millisUtilFinished / 60000 + 1));
                                            letterTimeView.setText("start in ");
                                            timeViewMin.setText("Min");

                                        }

                                        @Override
                                        public void onFinish() {
                                            timeView.setText("now");
                                            letterTimeView.setText("");
                                            timeViewMin.setText("");
                                        }
                                    }.start();
                                }else{
                                    System.out.println("ßßßßßßßßßßßßßßßßßßßßßßßßßßßßßß");
                                    System.out.printf("%dD %dH %dMin%n", durationUntilEndOfClass.toDays(), durationUntilEndOfClass.toHours() % 24, durationUntilEndOfClass.toMinutes() % 60);


                                    new CountDownTimer(durationUntilEndOfClass.toMinutes() * 60000, 60000) {
                                        public void onTick(long millisUtilFinished) {
                                            timeView.setText(Long.toString(millisUtilFinished / 60000 + 1));
                                            letterTimeView.setText("end in ");
                                            timeViewMin.setText("Min");

                                        }

                                        @Override
                                        public void onFinish() {
                                            timeView.setText("");
                                            nextClassView.setText("Pause!");
                                            letterTimeView.setText("");
                                            timeViewMin.setText("");
                                            UniImage.setImageResource(R.drawable.ic_pause);





                                        }
                                    }.start();

                                }

                            }



                            else if (durationUntilStartOfClass.toHours()>9){
                                UniImage.setImageResource(R.drawable.ic_celebration);
                                nextClassView.setText("Sie haben keine Vorlesungen mehr heute");


                            }




                        }
                    });



                } catch (NoConnectionException | IllegalAccessException | MalformedURLException e) {
                    e.printStackTrace();

                }
            }}).start();
    }



    private void loadKvv(){
        LinearLayout layoutCardKvv = findViewById(R.id.layoutCardKvv);

        LinearLayout layoutTram = new LinearLayout(DashboardActivity.this);
        layoutTram.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutTram.setOrientation(LinearLayout.HORIZONTAL);
        layoutTram.setVerticalGravity(View.TEXT_ALIGNMENT_CENTER);



        layoutCardKvv.addView(layoutTram);

        ImageView tramImage= new ImageView(DashboardActivity.this);

        tramImage.setLayoutParams(new ViewGroup.LayoutParams(60, 60));
        tramImage.setImageResource(R.drawable.ic_tram);
        tramImage.setPadding(0,0,10,0);

        layoutTram.addView(tramImage);


        TextView tramView = new TextView(DashboardActivity.this);
        tramView.setTextSize(13);
        tramView.setTextColor(getResources().getColor(R.color.black));
        tramView.setText("1 (Durlach)");
        tramView.setPadding(0,7,5,7);
        tramView.setLayoutParams(new ViewGroup.LayoutParams(250, ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutTram.addView(tramView);

        TextView platformView = new TextView(DashboardActivity.this);
        platformView.setTextSize(13);
        platformView.setTextColor(getResources().getColor(R.color.black));
        platformView.setText("Gleis 2");
        platformView.setPadding(20,7,0,7);
        platformView.setLayoutParams(new ViewGroup.LayoutParams(250, ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutTram.addView(platformView);


        TextView timeView = new TextView(DashboardActivity.this);
        timeView.setTextSize(13);
        timeView.setTextColor(getResources().getColor(R.color.black));
        timeView.setText("12:20");
        timeView.setPadding(20,7,0,7);
        timeView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutTram.addView(timeView);



    }
    private void loadUserInteraction(){


        ImageView image_canteen = findViewById(R.id.imageBox_dashboard_canteen);
        ImageView image_coffee= findViewById(R.id.imageBox_dashboard_coffee);
        ImageView image_printer = findViewById(R.id.imageBox_dashboard_printer);

        InteractionState statePrinter= UserInteraction.getStatePrinter();


        image_canteen.setColorFilter(ContextCompat.getColor(this, R.color.orange_queue));
        image_coffee.setColorFilter(ContextCompat.getColor(this, R.color.blue_cleaning));
        image_printer.setColorFilter(ContextCompat.getColor(this, R.color.grey_light));


      if (statePrinter!=null){
        image_printer.setColorFilter(statePrinter.getColor());
      }

    }


    private void loadPersonalInformation(){


         String MyPREFERENCES = "myPreferencesKey" ;
         List <String> personalData = new ArrayList<>();
        personalData.add("matriculationNumberKey");
        personalData.add("libraryNumberKey" );
        personalData.add("studentMailKey" );

        SharedPreferences sp = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        LinearLayout layoutCardPI = findViewById(R.id.layoutCardPI);


        for (String data:personalData){
            String info= sp.getString(data, "");
            if (!info.equals("")){



                LinearLayout layoutInfo = new LinearLayout(DashboardActivity.this);
                layoutInfo.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                layoutInfo.setOrientation(LinearLayout.HORIZONTAL);
                layoutInfo.setVerticalGravity(Gravity.CENTER_VERTICAL);
                layoutInfo.setPadding(0,15,0,15);



                layoutCardPI.addView(layoutInfo);

                ImageButton copyImage= new ImageButton(DashboardActivity.this);
                copyImage.setLayoutParams(new ViewGroup.LayoutParams(60, 60));
                copyImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_copy));
                copyImage.getDrawable().setColorFilter(ContextCompat.getColor(this, R.color.red), PorterDuff.Mode.SRC_ATOP);
                layoutInfo.addView(copyImage);


                TextView emailView = new TextView(DashboardActivity.this);
                emailView.setTextSize(15);
                emailView.setGravity(Gravity.CENTER_VERTICAL);
                emailView.setTextColor(getResources().getColor(R.color.black));
                emailView.setText(info);
                emailView.setPadding(10,0,5,0);
                emailView.setLayoutParams(new ViewGroup.LayoutParams(250, ViewGroup.LayoutParams.WRAP_CONTENT));
                layoutInfo.addView(emailView);


                copyImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("", info);
                            clipboard.setPrimaryClip(clip);


                            Toast.makeText(DashboardActivity.this, "Kopiert", Toast.LENGTH_SHORT).show();
                        }
                    });








            }else{

                if (layoutCardPI.getChildCount()<1){
                TextView messageView = new TextView(DashboardActivity.this);
                messageView.setTextSize(15);
                messageView.setGravity(Gravity.CENTER_VERTICAL);
                messageView.setTextColor(getResources().getColor(R.color.black));
                messageView.setText("Sie haben noch keine personale Daten gespeichert");
                messageView.setPadding(10,0,5,0);
                messageView.setLayoutParams(new ViewGroup.LayoutParams(250, ViewGroup.LayoutParams.WRAP_CONTENT));
                layoutCardPI.addView(messageView);}
            }
        }




    }

    private void loadMealPlan(){
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                List<String> meals = CantineActivity.loadDataForDashboard();

                layoutCardMealPlan.post(new Runnable() {
                    @Override
                    public void run() {
                        if (meals.size()>0){
                            for (String m:meals){

                                LinearLayout layoutM = new LinearLayout(DashboardActivity.this);
                                layoutM.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                layoutM.setOrientation(LinearLayout.HORIZONTAL);
                                layoutM.setVerticalGravity(View.TEXT_ALIGNMENT_CENTER);



                                layoutCardMealPlan.addView(layoutM);

                                ImageView mealImage= new ImageView(DashboardActivity.this);

                                mealImage.setLayoutParams(new ViewGroup.LayoutParams(60, 60));
                                mealImage.setImageResource(R.drawable.ic_baseline_restaurant_24);
                                mealImage.setPadding(0,0,10,0);

                                layoutM.addView(mealImage);


                                TextView mealView = new TextView(DashboardActivity.this);
                                mealView.setTextSize(13);
                                mealView.setTextColor(getResources().getColor(R.color.black));
                                mealView.setText(m);
                                mealView.setPadding(0,7,5,7);
                                mealView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                layoutM.addView(mealView);

                        }
                        }else{
                            TextView mealView = new TextView(DashboardActivity.this);
                            mealView.setTextSize(13);
                            mealView.setTextColor(getResources().getColor(R.color.black));
                            mealView.setText("Cantine ist heute wahrscheinlich geschlossen.");
                            mealView.setPadding(0,7,5,7);
                            mealView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            layoutCardMealPlan.addView(mealView);

                        }
                    }


                });
            }
        }).start();
        }


}