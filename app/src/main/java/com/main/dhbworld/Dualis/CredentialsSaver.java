package com.main.dhbworld.Dualis;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;

import com.main.dhbworld.Dualis.view.LoggedInView;

import java.net.HttpCookie;
import java.util.List;

public class CredentialsSaver {

    private final AppCompatActivity activity;
    private final SharedPreferences.Editor editor;
    private final SharedPreferences.Editor settingsEditor;

    private final SecureStore secureStore;

    public CredentialsSaver(AppCompatActivity activity, SharedPreferences.Editor editor, SharedPreferences.Editor settingsEditor, SecureStore secureStore) {
        this.activity = activity;
        this.editor = editor;
        this.settingsEditor = settingsEditor;
        this.secureStore = secureStore;
    }

    public void dontSaveCredentials(String arguments, List<HttpCookie> cookies, SecureStore secureStore) {
        setBiometricsSettings(false, false);
        try {
            secureStore.clearCredentials();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new LoggedInView(activity, arguments, cookies).createView();
    }

    public void saveCredentialsWithoutBiometrics(String arguments, List<HttpCookie> cookies, String email, String password) {
        BiometricManager biometricManager = BiometricManager.from(activity);
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK) != BiometricManager.BIOMETRIC_SUCCESS) {
            settingsEditor.putBoolean("useBiometrics", false);
            settingsEditor.apply();
        }
        try {
            secureStore.saveCredentials(email, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new LoggedInView(activity, arguments, cookies).createView();
    }

    public void saveCredentialsWithBiometrics(String arguments, List<HttpCookie> cookies, String email, String password) {
        boolean biometricsAvailable = secureStore.askIfUseBiometricsIfAvailable(new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                biometricsResult(false, email, password, arguments, cookies);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                biometricsResult(true, email, password, arguments, cookies);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                biometricsResult(false, email, password, arguments, cookies);
            }
        });
        if (!biometricsAvailable) {
            noBiometricsAvailable(arguments, cookies, email, password);
        }
    }

    private void noBiometricsAvailable(String arguments, List<HttpCookie> cookies, String email, String password) {
        settingsEditor.putBoolean("useBiometrics", false);
        settingsEditor.apply();
        try {
            secureStore.saveCredentials(email, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new LoggedInView(activity, arguments, cookies).createView();
    }

    private void biometricsResult(boolean value1, String email, String password, String arguments, List<HttpCookie> cookies) {
        setBiometricsSettings(true, value1);

        try {
            secureStore.saveCredentials(email, password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new LoggedInView(activity, arguments, cookies).createView();
    }

    private void setBiometricsSettings(boolean value, boolean value1) {
        editor.putBoolean("alreadyAskedBiometrics", value);
        settingsEditor.putBoolean("useBiometrics", value1);
        editor.apply();
        settingsEditor.apply();
    }
}
