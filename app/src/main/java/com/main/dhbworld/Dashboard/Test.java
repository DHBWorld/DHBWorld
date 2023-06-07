package com.main.dhbworld.Dashboard;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.main.dhbworld.R;

public class Test extends AppCompatActivity {
    Button btns[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maino);
      // DashboardCard wl = (DashboardCard) findViewById(R.id.dashboardCard);

        MaterialCardView card_dash_mealPlan = findViewById(R.id.card_dash_mealPlan);
        LinearLayout  card_dash_mealPlan_layout = findViewById(R.id.card_dash_mealPlan_layout);

      //  DashboardCard dashboardCard = new DashboardCard(card_dash_mealPlan_layout,card_dash_mealPlan );







        }


}