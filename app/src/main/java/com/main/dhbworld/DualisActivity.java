package com.main.dhbworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.preference.PreferenceManager;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.net.Credentials;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.OAuthProvider;
import com.main.dhbworld.Dualis.DualisAPI;
import com.main.dhbworld.Dualis.LoggedInView;
import com.main.dhbworld.Dualis.SecureStore;
import com.main.dhbworld.Navigation.NavigationUtilities;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.net.ssl.HttpsURLConnection;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dualis_login);

        NavigationUtilities.setUpNavigation(this, R.id.dualis);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        DualisAPI.createNotificationChannelGeneral(this);
        DualisAPI.createNotificationChannelNewGrade(this);

        sharedPreferences = getSharedPreferences("Dualis", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        settingsPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        settingsEditor = settingsPrefs.edit();

        dualisEmail = findViewById(R.id.dualisEmail);
        dualisPassword = findViewById(R.id.dualisPassword);
        dualisEmailLayout = findViewById(R.id.dualisEmailLayout);
        dualisPasswordLayout = findViewById(R.id.dualisPasswordLayout);
        saveCredentialsCheckbox = findViewById(R.id.dualisSaveCredentials);
        loginButton = findViewById(R.id.dualisLoginButton);
        progressIndicator = findViewById(R.id.dualisLoginProgressIndicator);

        boolean saveCredentials = sharedPreferences.getBoolean("saveCredentials", false);
        saveCredentialsCheckbox.setChecked(saveCredentials);

        SecureStore secureStore = new SecureStore(this, sharedPreferences);

        if (saveCredentials) {
            if (settingsPrefs.getBoolean("useBiometrics", false)) {
                dualisPasswordLayout.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
                fillSavedCredentialsWithBiometrics(secureStore);
            } else {
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
        }

        dualisPasswordLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fillSavedCredentialsWithBiometrics(secureStore);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.setEnabled(false);
                progressIndicator.setVisibility(View.VISIBLE);
                dualisEmailLayout.setError(null);
                dualisPasswordLayout.setError(null);
                dualisPasswordLayout.clearFocus();
                dualisEmailLayout.clearFocus();

                InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                String dualisEmailString = dualisEmail.getText().toString();
                String dualisPasswordString = dualisPassword.getText().toString();
                boolean saveCredentials = saveCredentialsCheckbox.isChecked();

                if (dualisEmailString.isEmpty()) {
                    dualisEmailLayout.setError("Dieses Feld darf nicht leer sein");
                }
                if (dualisPasswordString.isEmpty()) {
                    dualisPasswordLayout.setError("Dieses Feld darf nicht leer sein");
                }

                if (dualisPasswordString.isEmpty() || dualisEmailString.isEmpty()) {
                    loginButton.setEnabled(true);
                    progressIndicator.setVisibility(View.GONE);
                    return;
                }

                editor.putBoolean("saveCredentials", saveCredentials);
                editor.apply();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        login(dualisEmailString, dualisPasswordString, saveCredentials, secureStore);
                    }
                }).start();
            }
        });
    }

    private void login(String email, String password, boolean saveCredentials, SecureStore secureStore) {
        Handler handler = new Handler(Looper.getMainLooper());
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        URL url;
        try {
            url = new URL("https://dualis.dhbw.de/scripts/mgrqispi.dll");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty( "Content-type", "application/x-www-form-urlencoded");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

            writer.write("usrname=" + URLEncoder.encode(email, "UTF-8") + "&pass=" + URLEncoder.encode(password, "UTF-8") + "&APPNAME=CampusNet&PRGNAME=LOGINCHECK&ARGUMENTS=clino%2Cusrname%2Cpass%2Cmenuno%2Cmenu_type%2Cbrowser%2Cplatform&clino=000000000000001&menuno=000324&menu_type=classic&browser=&platform=");
            writer.flush();
            writer.close();

            int status = conn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
                if (cookies.size() == 0) {
                    handler.post(() -> {
                        dualisEmailLayout.setError(getString(R.string.wrong_credentials));
                        progressIndicator.setVisibility(View.GONE);
                        loginButton.setEnabled(true);
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String arguments = conn.getHeaderField("REFRESH").split("&")[2];
                            if (saveCredentials) {
                                if (!sharedPreferences.getBoolean("alreadyAskedBiometrics", false)) {
                                    secureStore.askIfUseBiometricsIfAvailable(new BiometricPrompt.AuthenticationCallback() {
                                        @Override
                                        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                                            super.onAuthenticationError(errorCode, errString);

                                            editor.putBoolean("alreadyAskedBiometrics", true);
                                            settingsEditor.putBoolean("useBiometrics", false);
                                            editor.apply();
                                            settingsEditor.apply();

                                            try {
                                                secureStore.saveCredentials(email, password);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            new LoggedInView(DualisActivity.this, arguments, cookies).createView();
                                        }

                                        @Override
                                        public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                                            super.onAuthenticationSucceeded(result);
                                            editor.putBoolean("alreadyAskedBiometrics", true);
                                            settingsEditor.putBoolean("useBiometrics", true);
                                            editor.apply();
                                            settingsEditor.apply();
                                            try {
                                                secureStore.saveCredentials(email, password);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            new LoggedInView(DualisActivity.this, arguments, cookies).createView();
                                        }

                                        @Override
                                        public void onAuthenticationFailed() {
                                            super.onAuthenticationFailed();
                                            editor.putBoolean("alreadyAskedBiometrics", true);
                                            settingsEditor.putBoolean("useBiometrics", false);
                                            editor.apply();
                                            settingsEditor.apply();
                                            try {
                                                secureStore.saveCredentials(email, password);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            new LoggedInView(DualisActivity.this, arguments, cookies).createView();
                                        }
                                    });
                                } else {
                                    try {
                                        secureStore.saveCredentials(email, password);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    new LoggedInView(DualisActivity.this, arguments, cookies).createView();
                                }
                            } else {
                                editor.putBoolean("alreadyAskedBiometrics", false);
                                settingsEditor.putBoolean("useBiometrics", false);
                                editor.apply();
                                settingsEditor.apply();
                                try {
                                    secureStore.clearCredentials();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                new LoggedInView(DualisActivity.this, arguments, cookies).createView();
                            }
                        }
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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