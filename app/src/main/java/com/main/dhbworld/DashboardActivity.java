package com.main.dhbworld;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.main.dhbworld.CantineClasses.MealDailyPlan;
import com.main.dhbworld.Enums.InteractionState;
import com.main.dhbworld.Firebase.Utilities;
import com.main.dhbworld.Navigation.NavigationUtilities;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {


    private LinearLayout layoutCardMealPlan;
    public static final String MyPREFERENCES = "" ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        NavigationUtilities.setUpNavigation(this,R.id.dashboard);


        layoutCardMealPlan= findViewById(R.id.layoutCardMealPlan);
        loadUserInteraction();
        loadMealPlan();
        loadKvv();
        loadCalendar();
        loadPersonalInformation();





    }

    private void loadCalendar(){
        LinearLayout layoutCardCalendar = findViewById(R.id.layoutCardCalendar);
        LinearLayout layoutNextClass = new LinearLayout(DashboardActivity.this);
        layoutNextClass.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutNextClass.setOrientation(LinearLayout.HORIZONTAL);
        layoutNextClass.setVerticalGravity(View.TEXT_ALIGNMENT_CENTER);
        layoutCardCalendar.addView(layoutNextClass);

        ImageView UniImage= new ImageView(DashboardActivity.this);

        UniImage.setLayoutParams(new ViewGroup.LayoutParams(60, 60));
        UniImage.setImageResource(R.drawable.ic_uni);
        UniImage.setPadding(0,0,10,0);

        layoutNextClass.addView(UniImage);

        TextView nextClassView = new TextView(DashboardActivity.this);
        nextClassView.setTextSize(15);
        nextClassView.setTextColor(getResources().getColor(R.color.black));
        nextClassView.setText("Softwareengineering");
        nextClassView.setPadding(0,7,5,7);
        nextClassView.setLayoutParams(new ViewGroup.LayoutParams(550, ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutNextClass.addView(nextClassView);

        TextView timeView = new TextView(DashboardActivity.this);
        timeView.setTextSize(15);
        timeView.setTextColor(getResources().getColor(R.color.black));
        timeView.setText("59 Min");
        timeView.setPadding(0,7,5,7);
        timeView.setLayoutParams(new ViewGroup.LayoutParams(250, ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutNextClass.addView(timeView);


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


      // image_canteen.setBackgroundColor(res.color.);
     //  image_canteen.setImageTintList(ColorStateList.valueOf(getColor(R.color.blue_cleaning)));
      // image_canteen.setColorFilter(getContext().getRessources.getColor(R.color.blue_cleaning));
       // image_canteen.setColorFilter(getContext().getRessources().getColor(R.color.grey_dark));

       // ColorStateList l=ColorStateList.valueOf(R.color.blue_cleaning);
        //image_canteen.setImageTintList(this, l);

       // Resources res = getResources();
     //   image_printer.setBackgroundColor(res.getColor(statePrinter.getColor()));

        //image_canteen.setBackgroundColor(getColor(stateCanteen.getColor()));

        //image_coffee.setBackgroundColor(res.getColor(stateCoffee.getColor()));
       // image_printer.setBackgroundColor(res.getColor(statePrinter.getColor()));
    }

    @SuppressLint("ResourceAsColor")
    private void loadPersonalInformation(){


         String MyPREFERENCES = "myPreferencesKey" ;
         List <String> personalData = new ArrayList<>();
        personalData.add("matriculationNumberKey");
        personalData.add("libraryNumberKey" );
        personalData.add("studentMailKey" );

        SharedPreferences sp = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        LinearLayout layoutCardPI = findViewById(R.id.layoutCardPI);


        for (String data:personalData){





        LinearLayout layoutInfo = new LinearLayout(DashboardActivity.this);
        layoutInfo.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutInfo.setOrientation(LinearLayout.HORIZONTAL);
        layoutInfo.setVerticalGravity(View.TEXT_ALIGNMENT_CENTER);



        layoutCardPI.addView(layoutInfo);

        Button copyImage= new Button(DashboardActivity.this);
        copyImage.setLayoutParams(new ViewGroup.LayoutParams(60, 60));
        copyImage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_copy,0,0,0);
        copyImage.setBackgroundColor(getResources().getColor(R.color.white));
        layoutInfo.addView(copyImage);


        TextView emailView = new TextView(DashboardActivity.this);
        emailView.setTextSize(13);
        emailView.setTextColor(getResources().getColor(R.color.black));

        String s= sp.getString(data, "Ihre Studenten-E-Mail ist noch nicht gespreichert");


        emailView.setText(s);
        emailView.setPadding(10,20,5,10);
        emailView.setLayoutParams(new ViewGroup.LayoutParams(250, ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutInfo.addView(emailView);


            copyImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", s);
                    clipboard.setPrimaryClip(clip);


                    Toast.makeText(DashboardActivity.this, "Kopiert", Toast.LENGTH_LONG).show();
                }
            });

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