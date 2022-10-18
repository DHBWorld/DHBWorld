package com.main.dhbworld;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.jsibbold.zoomage.ZoomageView;
import com.main.dhbworld.Navigation.NavigationUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MapActivity extends AppCompatActivity {

    AutoCompleteTextView roomDropdown;
    AutoCompleteTextView entryDropdown;
    Button showMapButton;
    ZoomageView mapImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        NavigationUtilities.setUpNavigation(this, R.id.map);

        initializeViews();

        setRooms();

        ArrayList<String> entryItems = setEntries();

        showMapButton.setOnClickListener(view -> {
            hideKeyboard();

            String selectedRoom = roomDropdown.getText().toString();
            String selectedEntry = entryDropdown.getText().toString();

            Drawable map = getImage(selectedRoom, selectedEntry, entryItems);
            if (map != null) {
                mapImageView.setImageDrawable(map);
            } else {
                Snackbar.make(MapActivity.this.findViewById(android.R.id.content),
                                getResources().getString(R.string.no_map_available),
                                BaseTransientBottomBar.LENGTH_LONG)
                        .show();
            }

        });

        if (getIntent() != null && getIntent().hasExtra("room")) {
            String roomname = getIntent().getStringExtra("room");
            roomDropdown.setText(roomname);
            showMapButton.callOnClick();
        }
    }

    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ignored) { }

        roomDropdown.clearFocus();
        entryDropdown.clearFocus();
    }

    private ArrayList<String> setEntries() {
        ArrayList<String> entryItems = new ArrayList<>();
        entryItems.add(getString(R.string.main_entry));
        entryItems.add(getString(R.string.left));
        entryItems.add(getString(R.string.right));
        ArrayAdapter<String> arrayAdapterEntry = new ArrayAdapter<>(this, R.layout.dualis_semester_list_item, entryItems);
        entryDropdown.setAdapter(arrayAdapterEntry);
        entryDropdown.setText(entryItems.get(0), false);
        return entryItems;
    }

    private void setRooms() {
        ArrayList<String> roomItems = getRoomItems();

        ArrayAdapter<String> arrayAdapterRoom = new ArrayAdapter<>(this, R.layout.dualis_semester_list_item, roomItems);
        roomDropdown.setAdapter(arrayAdapterRoom);
    }

    private ArrayList<String> getRoomItems() {
        ArrayList<String> roomItems = new ArrayList<>();

        Map<String, String> fileMap = loadFileMap();
        for (String file : fileMap.keySet()) {
            roomItems.add(file.replace(".png", ""));
        }
        Collections.sort(roomItems);
        return roomItems;
    }

    private void initializeViews() {
        roomDropdown = findViewById(R.id.mapRoomDropdown);
        entryDropdown = findViewById(R.id.mapEntryDropdown);
        showMapButton = findViewById(R.id.showMapButton);
        mapImageView = findViewById(R.id.mapImageView);
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
        } catch (IOException | JSONException ignored) { }

        return fileMap;
    }
}