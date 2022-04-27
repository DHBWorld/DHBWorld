package com.main.dhbworld;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.main.dhbworld.CantineClasses.MealDailyPlan;
import com.main.dhbworld.Navigation.NavigationUtilities;

import org.json.JSONException;

import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private TextView test;
    private LinearLayout layoutCardMealPlan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        NavigationUtilities.setUpNavigation(this,R.id.dashboard);

        layoutCardMealPlan= findViewById(R.id.layoutCardMealPlan);

        loadMealPlan();





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
                                TextView mealView = new TextView(DashboardActivity.this);
                                mealView.setTextSize(13);
                                mealView.setTextColor(getResources().getColor(R.color.black));
                                mealView.setText(m);
                                mealView.setPadding(0,7,5,7);
                                mealView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                layoutCardMealPlan.addView(mealView);

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