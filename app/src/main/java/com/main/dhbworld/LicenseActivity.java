package com.main.dhbworld;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class LicenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licence);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> finish());

        String[] licenses = getResources().getStringArray(R.array.licenses);
        String[] licenseTitles = getResources().getStringArray(R.array.license_titles);

        ListView licenseListView = findViewById(R.id.license_listview);
        licenseListView.setOnItemClickListener((parent, view, position, id) -> new MaterialAlertDialogBuilder(LicenseActivity.this)
                .setTitle(licenseTitles[position])
                .setMessage(licenses[position])
                .setPositiveButton(getResources().getString(R.string.close), null)
                .show());
    }
}