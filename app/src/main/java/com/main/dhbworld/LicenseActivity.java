package com.main.dhbworld;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class LicenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licence);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String[] licenses = getResources().getStringArray(R.array.licenses);
        String[] licenseTitles = getResources().getStringArray(R.array.license_titles);

        ListView licenseListView = findViewById(R.id.license_listview);
        licenseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new MaterialAlertDialogBuilder(LicenseActivity.this)
                        .setTitle(licenseTitles[position])
                        .setMessage(licenses[position])
                        .setPositiveButton(getResources().getString(R.string.close), null)
                        .show();
            }
        });
    }
}