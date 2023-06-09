package com.main.dhbworld.Blackboard;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.dhbworld.BlackboardActivity;
import com.main.dhbworld.R;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        String[] inputData = getInputData();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> advertisement = new HashMap<>();
        advertisement.put("approved", false);
        advertisement.put("validUntil", inputData[0]);
        advertisement.put("title", inputData[1]);
        advertisement.put("text", inputData[2]);
        advertisement.put("tagOne", inputData[3]);
        advertisement.put("tagTwo", inputData[4]);

        db.collection("Blackboard").add(advertisement);

        Intent intent = new Intent(NewAdvertisementActivity.this, BlackboardActivity.class);
        startActivity(intent);
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
        inputData[1] = titleText.toString();
        inputData[2] = descriptionText.toString();
        inputData[3] = tagOneText.toString();
        inputData[4] = tagTwoText.toString();

        return inputData;
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
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormat.format(calendar.getTime());


    }












}
