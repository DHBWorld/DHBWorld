package com.main.dhbworld;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import com.jsibbold.zoomage.ZoomageView;
import com.main.dhbworld.Navigation.NavigationUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        NavigationUtilities.setUpNavigation(this, R.id.map);

        AutoCompleteTextView roomDropdown = findViewById(R.id.mapRoomDropdown);
        AutoCompleteTextView entryDropdown = findViewById(R.id.mapEntryDropdown);

        Button button = findViewById(R.id.showMapButton);

        ArrayList<String> roomItems = new ArrayList<>();

        Map<String, String> fileMap = loadFileMap();
        for (String file : fileMap.keySet()) {
            roomItems.add(file.replace(".png", ""));
        }
        Collections.sort(roomItems);
        ArrayAdapter<String> arrayAdapterRoom = new ArrayAdapter<>(this, R.layout.dualis_semester_list_item, roomItems);
        roomDropdown.setAdapter(arrayAdapterRoom);

        ArrayList<String> entryItems = new ArrayList<>();
        entryItems.add("Haupteingang");
        entryItems.add("Links");
        entryItems.add("Rechts");
        ArrayAdapter<String> arrayAdapterEntry = new ArrayAdapter<>(this, R.layout.dualis_semester_list_item, entryItems);
        entryDropdown.setAdapter(arrayAdapterEntry);
        entryDropdown.setText(entryItems.get(0), false);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception ignored) { }

                String selectedRoom = roomDropdown.getText().toString();
                String selectedEntry = entryDropdown.getText().toString();

                roomDropdown.clearFocus();
                entryDropdown.clearFocus();

                //ImageView mapImageView = findViewById(R.id.mapImageView);
                ZoomageView mapImageView = findViewById(R.id.mapImageView);

                Drawable map = getImage(selectedRoom, selectedEntry, entryItems);
                mapImageView.setImageDrawable(map);
            }
        });
    }

    private Drawable getImage(String selectedRoom, String selectedEntry, ArrayList<String> entryItems) {
        Drawable drawable = null;

        String prefix = "";
        if (selectedEntry.equals(entryItems.get(1))) {
            prefix = "L";
        } else if (selectedEntry.equals(entryItems.get(2))) {
            prefix = "R";
        }

        try {
            Map<String, String> fileMap = loadFileMap();
            String imageFileName = fileMap.get(selectedRoom + ".png");

            if (!Arrays.asList(getAssets().list("map/images/")).contains(prefix + imageFileName)) {
                return null;
            }

            InputStream ims = getAssets().open("map/images/" + prefix + imageFileName);
            drawable = Drawable.createFromStream(ims, null);
            ims .close();
        } catch(IOException ignored) {

        }
        return drawable;
    }

    private Map<String, String> loadFileMap() {
        Map<String, String> fileMap = new HashMap<>();
        try {
            InputStreamReader inputStreamFilemap = new InputStreamReader(getAssets().open("map/filemap.json"));
            BufferedReader reader = new BufferedReader(inputStreamFilemap);
            String fileMapString = reader.lines().collect(Collectors.joining());
            JSONObject jsonObject = new JSONObject(fileMapString);

            reader.close();
            inputStreamFilemap.close();

            jsonObject.keys().forEachRemaining(key -> {
                try {
                    String value = jsonObject.getString(key);
                    fileMap.put(key, value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            });
        } catch (IOException | JSONException ignored) {

        }

        return fileMap;
    }
}