package com.main.dhbworld;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.google.android.gms.common.internal.StringResourceValueReader;

public class DataPrivacyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dataprivacy);
        TextView textView = findViewById(R.id.dataprivacytext);
        String text = getResources().getString(R.string.data_privacy);
        textView.setText(Html.fromHtml(text,1));
    }
}