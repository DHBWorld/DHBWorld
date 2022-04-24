package com.main.dhbworld;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;

public class DashboardActivity extends AppCompatActivity {

    private TextView test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        test= findViewById(R.id.test);
        test.setText("test1");
       /* CantineActivity.loadDataForDashboard();
        try {
            if (CantineActivity.getMealPlanForDashboard().getMeal()!=null){
          //  test.setText(CantineActivity.getMealPlanForDashboard().getMeal()[0].getName());
            }else{
                test.setText("problems");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        */
    }
}