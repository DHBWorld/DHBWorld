package com.main.dhbworld.Blackboard;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.dhbworld.R;
import com.main.dhbworld.Utilities.SimpleDataFormatUniversalDay;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NewAdvertisementActivity extends AppCompatActivity {

    private AutoCompleteTextView validUntil;

    private TextInputEditText title;
    private TextInputEditText description;
    private MaterialButton sendButton;
    private List<CheckBox> tags;
    private int tagsCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_advertisement);
        tagsCounter=0;
        setupViews();

        setupClickListeners();

    }

    private void setupViews() {
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> finish());

        validUntil = findViewById(R.id.publishingDuration);
        TextInputLayout tagsLayout = findViewById(R.id.advertisementTagsLayout);
        String[] types = {"1 Week", "2 Week", "4 Week", "12 Week"};
        validUntil.setAdapter(new ArrayAdapter<>(this, R.layout.dropdown_list_item, types));
        CardTags[] tagLabels = {CardTags.EVENT, CardTags.RENT, CardTags.SPORT, CardTags.LOST, CardTags.SEARCH_FOR,CardTags.FOUND};
        title = findViewById(R.id.newAdvertTitle);
        description = findViewById(R.id.newAdvertDiscription);
        sendButton = findViewById(R.id.sendAdvert);
        tags= new ArrayList<>();


        for (CardTags cardTag:tagLabels){
            CheckBox box= new CheckBox(this);
            box.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            box.setText(cardTag.getName());
            tags.add(box);

            tagsLayout.addView(box);
        }


    }


    private void setupClickListeners() {
        sendButton.setOnClickListener(v -> new MaterialAlertDialogBuilder(NewAdvertisementActivity.this)
                .setTitle(R.string.important)
                .setMessage(R.string.warning_send_advert_message)
                .setPositiveButton(R.string.send, (dialog, which) -> loadDateInFirebase())
                .setNeutralButton(getString(R.string.terms), (dialog, which) -> showTerms())
                .setNegativeButton(android.R.string.cancel, null)
                .setCancelable(false)
                .show());

        for (CheckBox tag:tags){
            tag.setOnClickListener(new CheckBoxClickListener(tag));
        }

    }

    private void showTerms() {
        new MaterialAlertDialogBuilder(NewAdvertisementActivity.this)
                .setTitle(R.string.terms)
                .setMessage(R.string.terms_message)
                .setPositiveButton(R.string.close, null)
                .setCancelable(false)
                .show();
    }

    private void loadDateInFirebase() {
        updatePostingCount();
        Object[] inputData = getInputData();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> advertisement = new HashMap<>();
        advertisement.put("approved", false);
        advertisement.put("validUntil", Objects.requireNonNull(inputData)[0]);
        advertisement.put("title", Objects.requireNonNull(inputData)[1]);
        advertisement.put("text", Objects.requireNonNull(inputData)[2]);
        advertisement.put("tagOne", Objects.requireNonNull(inputData)[3]);
        advertisement.put("tagTwo", Objects.requireNonNull(inputData)[4]);
        advertisement.put("tagThree", Objects.requireNonNull(inputData)[5]);
        advertisement.put("addedOn", Objects.requireNonNull(inputData)[6]);

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

    private Object[] getInputData() {
        Object[] inputData = new Object[7];
        Editable validUntilText = validUntil.getText();
        Editable titleText = title.getText();
        Editable descriptionText = description.getText();

        if (validUntilText == null || titleText == null || descriptionText == null) {
            return null;
        }

        inputData[0] = calculateDeleteDay(validUntilText.toString());
        inputData[1] = validatedInput(titleText.toString());
        inputData[2] = validatedInput(descriptionText.toString());
        inputData[3] ="";
        inputData[4] ="";
        inputData[5] ="";
        List <String> tagLabels=new ArrayList<>();
        for (CheckBox box:tags){
            if (box.isChecked()){
                tagLabels.add(box.getText().toString());
            }
        }
        if (tagLabels.size()>0){
          inputData[3] = tagLabels.get(0);
        }
        if (tagLabels.size()>1){
           inputData[4] = tagLabels.get(1);
        }
        if (tagLabels.size()>2){
            inputData[5] = tagLabels.get(2);
        }
        inputData[6] = new Date();
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
        return new SimpleDataFormatUniversalDay().format(calendar.getTime());


    }

    class CheckBoxClickListener implements View.OnClickListener{
        private final CheckBox tag;

        public CheckBoxClickListener(CheckBox tag) {
            this.tag=tag;
        }

        @Override
        public void onClick(View view) {

            if (tag.isChecked()) {
                tagsCounter++;
                if (tagsCounter < 3) {
                    return;
                }
                for (CheckBox t : tags) {
                    t.setClickable(t.isChecked());
                    t.setEnabled(t.isChecked());
                }
            }else{
                if (tagsCounter<1){
                    return;
                }
                tagsCounter--;
                if (tagsCounter!=2){
                    return;
                }
                for (CheckBox t:tags){
                    t.setClickable(true);
                    t.setEnabled(true);
                }

            }
        }
    }













}
