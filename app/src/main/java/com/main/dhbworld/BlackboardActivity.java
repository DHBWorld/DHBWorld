package com.main.dhbworld;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.main.dhbworld.Navigation.NavigationUtilities;

public class BlackboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blackboard);
        NavigationUtilities.setUpNavigation(this,R.id.blackboard);
    }
}