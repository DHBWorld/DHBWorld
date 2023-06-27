package com.main.dhbworld;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.preference.PreferenceManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.main.dhbworld.Debugging.Debugging;
import com.main.dhbworld.Dualis.CredentialsSaver;
import com.main.dhbworld.Dualis.SecureStore;
import com.main.dhbworld.Dualis.parser.api.DualisAPI;
import com.main.dhbworld.Dualis.parser.api.DualisNotification;
import com.main.dhbworld.Dualis.service.AutostartEnabler;
import com.main.dhbworld.Navigation.NavigationUtilities;

import java.net.HttpCookie;
import java.util.List;
import java.util.Map;

public class DualisActivity extends AppCompatActivity {

    TextInputEditText dualisEmail;
    TextInputEditText dualisPassword;
    TextInputLayout dualisEmailLayout;
    TextInputLayout dualisPasswordLayout;
    MaterialCheckBox saveCredentialsCheckbox;
    MaterialButton loginButton;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    SharedPreferences settingsPrefs;
    SharedPreferences.Editor settingsEditor;

    CircularProgressIndicator progressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Debugging.startDebugging(this);
        NavigationUtilities.setupLangAndDarkmode(this, this.getResources());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dualis_login);
        NavigationUtilities.setUpNavigation(this, R.id.dualis);

        createNotificationChannels();
        setupSharedPrefs();

        if (!sharedPreferences.getBoolean("autostart", false)) {
            AutostartEnabler.askForAutostart(this, editor);
        }

        setupViews();
        SecureStore secureStore = fillCredentialsOrAskBiometrics();
        setupPasswordView(secureStore);
        setupLoginButton(secureStore);
    }

    private void setupLoginButton(SecureStore secureStore) {
        loginButton.setOnClickListener(view -> {
            changeViewsOnLogin();

            InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            String dualisEmailString = dualisEmail.getText() == null ? "" : dualisEmail.getText().toString();
            String dualisPasswordString = dualisPassword.getText() == null ? "" : dualisPassword.getText().toString();
            boolean saveCredentials = saveCredentialsCheckbox.isChecked();

            if (checkIfEmpty(dualisEmailString, dualisPasswordString)) {
                loginButton.setEnabled(true);
                progressIndicator.setVisibility(View.GONE);
                return;
            }

            editor.putBoolean("saveCredentials", saveCredentials);
            editor.apply();

            new Thread(() -> login(dualisEmailString, dualisPasswordString, saveCredentials, secureStore)).start();
        });
    }

    private boolean checkIfEmpty(String dualisEmailString, String dualisPasswordString) {
        boolean empty = false;
        if (dualisEmailString.isEmpty()) {
            dualisEmailLayout.setError("Dieses Feld darf nicht leer sein");
            empty = true;
        }
        if (dualisPasswordString.isEmpty()) {
            dualisPasswordLayout.setError("Dieses Feld darf nicht leer sein");
            empty = true;
        }
        return empty;
    }

    private void changeViewsOnLogin() {
        loginButton.setEnabled(false);
        progressIndicator.setVisibility(View.VISIBLE);
        dualisEmailLayout.setError(null);
        dualisPasswordLayout.setError(null);
        dualisPasswordLayout.clearFocus();
        dualisEmailLayout.clearFocus();
    }

    private void setupPasswordView(SecureStore secureStore) {
        dualisPasswordLayout.setEndIconOnClickListener(view -> fillSavedCredentialsWithBiometrics(secureStore));
    }

    @NonNull
    private SecureStore fillCredentialsOrAskBiometrics() {
        boolean saveCredentials = sharedPreferences.getBoolean("saveCredentials", false);
        saveCredentialsCheckbox.setChecked(saveCredentials);

        SecureStore secureStore = new SecureStore(this, sharedPreferences);

        if (saveCredentials) {
            if (settingsPrefs.getBoolean("useBiometrics", false)) {
                dualisPasswordLayout.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
                fillSavedCredentialsWithBiometrics(secureStore);
            } else {
                fillCredentials(secureStore);
            }
        }
        return secureStore;
    }

    private void fillCredentials(SecureStore secureStore) {
        dualisPasswordLayout.setEndIconMode(TextInputLayout.END_ICON_NONE);
        try {
            Map<String, String> credentials = secureStore.loadCredentials();
            dualisEmail.setText(credentials.get("email"));
            dualisPassword.setText(credentials.get("password"));
        } catch (Exception e) {
            editor.putBoolean("saveCredentials", false);
            editor.apply();
            saveCredentialsCheckbox.setChecked(false);
            e.printStackTrace();
        }
    }

    private void setupSharedPrefs() {
        sharedPreferences = getSharedPreferences("Dualis", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        settingsPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        settingsEditor = settingsPrefs.edit();
    }

    private void createNotificationChannels() {
        DualisNotification.createNotificationChannelGeneral(this);
        DualisNotification.createNotificationChannelNewGrade(this);
    }

    private void setupViews() {
        dualisEmail = findViewById(R.id.dualisEmail);
        dualisPassword = findViewById(R.id.dualisPassword);
        dualisEmailLayout = findViewById(R.id.dualisEmailLayout);
        dualisPasswordLayout = findViewById(R.id.dualisPasswordLayout);
        saveCredentialsCheckbox = findViewById(R.id.dualisSaveCredentials);
        loginButton = findViewById(R.id.dualisLoginButton);
        progressIndicator = findViewById(R.id.dualisLoginProgressIndicator);
    }

    private void login(String email, String password, boolean saveCredentials, SecureStore secureStore) {
        CredentialsSaver credentialsSaver = new CredentialsSaver(this, editor, settingsEditor, secureStore);
        DualisAPI.login(this, email, password, new DualisAPI.LoginListener() {
            @Override
            public void onSuccess(String arguments, List<HttpCookie> cookies) {
                if (saveCredentials) {
                    if (!sharedPreferences.getBoolean("alreadyAskedBiometrics", false)) {
                        credentialsSaver.saveCredentialsWithBiometrics(arguments, cookies, email, password);
                    } else {
                        credentialsSaver.saveCredentialsWithoutBiometrics(arguments, cookies, email, password);
                    }
                } else {
                    credentialsSaver.dontSaveCredentials(arguments, cookies, secureStore);
                }
            }

            @Override
            public void onNoCookies() {
                noCookiesHandler();
            }

            @Override
            public void onError(Exception e) {
                onErrorHandler(e);
            }

            @Override
            public void onNon200() {
                onErrorHandler(getString(R.string.error));
            }
        });
    }

    private void onErrorHandler(String error) {
        Snackbar.make(findViewById(android.R.id.content), error, BaseTransientBottomBar.LENGTH_LONG).show();
        loginButton.setEnabled(true);
        progressIndicator.setVisibility(View.GONE);
    }

    private void onErrorHandler(Exception e) {
        e.printStackTrace();
        if (e.getMessage() != null) {
            onErrorHandler(e.getMessage());
        }
    }

    private void noCookiesHandler() {
        dualisEmailLayout.setError(getString(R.string.wrong_credentials));
        progressIndicator.setVisibility(View.GONE);
        loginButton.setEnabled(true);
    }

    private void fillSavedCredentialsWithBiometrics(SecureStore secureStore) {
        secureStore.askForBiometricsIfAvailable(new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                try {
                    Map<String, String> credentials = secureStore.loadCredentials();
                    dualisEmail.setText(credentials.get("email"));
                    dualisPassword.setText(credentials.get("password"));
                } catch (Exception e) {
                    editor.putBoolean("saveCredentials", false);
                    editor.apply();
                    saveCredentialsCheckbox.setChecked(false);
                    e.printStackTrace();
                }
            }
        });
    }


}