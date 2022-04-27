package com.main.dhbworld;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.main.dhbworld.Navigation.NavigationUtilities;

import org.json.JSONException;

import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private TextView test;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        NavigationUtilities.setUpNavigation(this,R.id.dashboard);



        test= findViewById(R.id.test);
        test.setText("test1");

        loadMealPlan();





    }

    private void loadMealPlan(){
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                List<String> meals= CantineActivity.loadDataForDashboard();

                loadLayoutMealPlan(meals);


            }

        }).start();
    }

    private void loadLayoutMealPlan(List<String> meals){

       for (String m:meals){
           test.setText(m);

       }

    }
}