package com.main.dhbworld;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.main.dhbworld.Blackboard.BlackboardCard;
import com.main.dhbworld.Navigation.NavigationUtilities;

public class BlackboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blackboard);
        NavigationUtilities.setUpNavigation(this,R.id.blackboard);

        LinearLayout board = findViewById(R.id.card_dash_mealPlan_layout);


        BlackboardCard card=new BlackboardCard(this, board);
        card.setText("test");
        card.setTitle("test");

        BlackboardCard card2=new BlackboardCard(this, board);
        card2.setText("test");
        card2.setTitle("test");








    }
}