package com.main.dhbworld;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.internal.StringResourceValueReader;
import com.google.android.material.appbar.MaterialToolbar;

public class DataPrivacyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dataprivacy);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView textView = findViewById(R.id.dataprivacytext);
        String text = getResources().getString(R.string.data_privacy);
        textView.setText(Html.fromHtml(text,1));
    }
}