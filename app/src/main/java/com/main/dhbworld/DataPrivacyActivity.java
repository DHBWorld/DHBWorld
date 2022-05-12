package com.main.dhbworld;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class DataPrivacyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dataprivacy);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView textView = findViewById(R.id.dataprivacytext);
        String text = getResources().getString(R.string.data_privacy);
        textView.setText(Html.fromHtml(text,1));
    }
}