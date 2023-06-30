package com.main.dhbworld.Blackboard;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.dhbworld.R;
import com.main.dhbworld.Utilities.SimpleDataFormatUniversal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NewAdvertisementActivity extends AppCompatActivity {

    private AutoCompleteTextView validUntil;
    private AutoCompleteTextView tagOne;
    private AutoCompleteTextView tagTwo;
    private TextInputEditText title;
    private TextInputEditText description;
    private MaterialButton sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_advertisement);
        setupViews();

        setupClickListener();

    }

    private void setupViews() {
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> finish());

        validUntil = findViewById(R.id.publishingDuration);
        tagOne=findViewById(R.id.tagOne);
        tagTwo=findViewById(R.id.tagTwo);
        String[] types = {"1 Week", "2 Week", "4 Week", "12 Week"};
        validUntil.setAdapter(new ArrayAdapter<>(this, R.layout.dropdown_list_item, types));
        String[] tags = {"Event", "Mieten", "Sport", "Sonstiges", "Suchen"};
        tagOne.setAdapter(new ArrayAdapter<>(this, R.layout.dropdown_list_item, tags));
        tagTwo.setAdapter(new ArrayAdapter<>(this, R.layout.dropdown_list_item, tags));
        title = findViewById(R.id.newAdvertTitle);
        description = findViewById(R.id.newAdvertDiscription);
        sendButton = findViewById(R.id.sendAdvert);
    }


    private void setupClickListener() {
        sendButton.setOnClickListener(v -> new MaterialAlertDialogBuilder(com.main.dhbworld.Blackboard.NewAdvertisementActivity.this)
                .setTitle(R.string.important)
                .setMessage("Wenn Sie fortsetzten, kann Ihre Anzeige nicht mehr geändert werden. Bei Problemen mit bereits publizierten Anzeigen wenden Sie sich direkt an das DHBWorld-Team über die Feadback-Funktion oder per E-mail.")
                .setPositiveButton(R.string.send, (dialog, which) -> loadDateInFirebase())
                .setNegativeButton(android.R.string.cancel, null)
                .setCancelable(false)
                .show());

    }

    private void loadDateInFirebase() {
        updatePostingCount();
        String[] inputData = getInputData();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> advertisement = new HashMap<>();
        advertisement.put("approved", false);
        advertisement.put("validUntil", Objects.requireNonNull(inputData)[0]);
        advertisement.put("title", Objects.requireNonNull(inputData)[1]);
        advertisement.put("text", Objects.requireNonNull(inputData)[2]);
        advertisement.put("tagOne", Objects.requireNonNull(inputData)[3]);
        advertisement.put("tagTwo", Objects.requireNonNull(inputData)[4]);

        db.collection("Blackboard").add(advertisement);
        finish();
    }

    public void updatePostingCount() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        long lastPostDate = sharedPrefs.getLong("blackboard_date_last_post", 0);
        int postCount = sharedPrefs.getInt("blackboard_total_posted", 0);

        Instant lastPost = Instant.ofEpochMilli(lastPostDate);
        if (lastPostDate == 0 || lastPost.atZone(ZoneId.systemDefault()).getDayOfYear() != Instant.now().atZone(ZoneId.systemDefault()).getDayOfYear()) {
            lastPostDate = Instant.now().toEpochMilli();
            postCount = 1;
        } else {
            if (postCount < 3) {
                postCount++;
            }
        }

        editor.putLong("blackboard_date_last_post", lastPostDate);
        editor.putInt("blackboard_total_posted", postCount);
        editor.apply();
    }


    private String[] getInputData() {
        String[] inputData = new String[5];
        Editable validUntilText = validUntil.getText();
        Editable titleText = title.getText();
        Editable descriptionText = description.getText();
        Editable tagOneText = tagOne.getText();
        Editable tagTwoText = tagTwo.getText();

        if (validUntilText == null || titleText == null || descriptionText == null) {
            return null;
        }

        inputData[0] = calculateDeleteDay(validUntilText.toString());
        inputData[1] = validatedInput(titleText.toString());
        inputData[2] = validatedInput(descriptionText.toString());
        inputData[3] = tagOneText.toString();
        inputData[4] = tagTwoText.toString();

        return inputData;
    }
    private String validatedInput(String input){
        input=input.replace("<!", " ");
        input=input.replace("<!--", " ");
        input=input.replace("/*", " ");
        if (input.contains(">")){
            input=input.replace("<", " ");
        }
        return input;
    }

    private String  calculateDeleteDay(String publishingPeriod){

        int publishingDuration = 0;
        switch (publishingPeriod) {
            case "1 Week":
                publishingDuration = 7;
                break;
            case "2 Week":
                publishingDuration = 14;
                break;
            case "4 Week":
                publishingDuration = 28;
                break;
            case "12 Week":
                publishingDuration = 84;
                break;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, publishingDuration);
        DateFormat dateFormat = new SimpleDataFormatUniversal();
        return dateFormat.format(calendar.getTime());


    }













}
