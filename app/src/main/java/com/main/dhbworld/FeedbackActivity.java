package com.main.dhbworld;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> finish());

        TextInputLayout feedbackNameLayout = findViewById(R.id.feedbackNameLayout);
        TextInputLayout feedbackEmailLayout = findViewById(R.id.feedbackEmailLayout);
        TextInputLayout feedbackTitleLayout = findViewById(R.id.feedbackTitleLayout);
        TextInputLayout feedbackDescriptionLayout = findViewById(R.id.feedbackDescriptionLayout);

        TextInputEditText feedbackName = findViewById(R.id.feedbackName);
        TextInputEditText feedbackEmail = findViewById(R.id.feedbackEmail);
        TextInputEditText feedbackTitle = findViewById(R.id.feedbackTitle);
        TextInputEditText feedbackDescription = findViewById(R.id.feedbackDescription);

        MaterialButton sendButton = findViewById(R.id.sendFeedbackButton);

        removeErrorOnType(feedbackName, feedbackNameLayout);
        removeErrorOnType(feedbackEmail, feedbackEmailLayout);
        removeErrorOnType(feedbackTitle, feedbackTitleLayout);
        removeErrorOnType(feedbackDescription, feedbackDescriptionLayout);

        sendButton.setOnClickListener(view -> {
            String name = feedbackName.getText().toString();
            String email = feedbackEmail.getText().toString();
            String title = feedbackTitle.getText().toString();
            String description = feedbackDescription.getText().toString();

            if (name.isEmpty()) {
                feedbackNameLayout.setError("Darf nicht leer sein");
            }
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) { //TODO stimmt noch nicht
                feedbackEmailLayout.setError("Ungültige E-Mail Adresse");
            }
            if (email.isEmpty()) {
                feedbackEmailLayout.setError("Darf nicht leer sein");
            }
            if (title.isEmpty()) {
                feedbackTitleLayout.setError("Darf nicht leer sein");
            }

            //TODO: INFOS SENDEN

        });
    }

    private void removeErrorOnType(TextInputEditText editText, TextInputLayout layout) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                layout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}