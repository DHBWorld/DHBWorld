package com.main.dhbworld.Dualis;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.main.dhbworld.DualisActivity;

import java.lang.reflect.Executable;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class SecureStore {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Activity activity;

    public SecureStore(Activity activity, SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        this.editor = sharedPreferences.edit();
        this.activity = activity;
    }

    public void clearCredentials() throws Exception {
        final String alias = "dualisCredentials";
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        keyStore.deleteEntry(alias);

        editor.remove("dualisIV");
        editor.remove("dualisCredentials");
        editor.apply();
    }

    public void saveCredentials(String email, String password) throws Exception{
        final String alias = "dualisCredentials";
        final KeyGenerator keyGenerator = KeyGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

        final KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(alias,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build();

        keyGenerator.init(keyGenParameterSpec);
        final SecretKey secretKey = keyGenerator.generateKey();

        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        String credentialsBase64 = Base64.getEncoder().encodeToString(email.getBytes(StandardCharsets.UTF_8))
                + ":" +
                Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8));

        byte[] encryptionIv = cipher.getIV();
        byte[] credentialsEncrypted = cipher.doFinal(credentialsBase64.getBytes(StandardCharsets.UTF_8));

        editor.putString("dualisIV", Base64.getEncoder().encodeToString(encryptionIv));
        editor.putString("dualisCredentials", Base64.getEncoder().encodeToString(credentialsEncrypted));
        editor.apply();

    }

    public Map<String, String> loadCredentials() throws Exception {
        final String alias = "dualisCredentials";
        final byte[] encryptionIv = Base64.getDecoder().decode(sharedPreferences.getString("dualisIV", ""));
        final byte[] encryptedCredentials = Base64.getDecoder().decode(sharedPreferences.getString("dualisCredentials", ""));

        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore
                .getEntry(alias, null);

        final SecretKey secretKey = secretKeyEntry.getSecretKey();

        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        final GCMParameterSpec spec = new GCMParameterSpec(128, encryptionIv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

        final byte[] decodedData = cipher.doFinal(encryptedCredentials);
        final String[] credentialsBase64 = new String(decodedData, StandardCharsets.UTF_8).split(":");


        Map<String, String> credentials = new HashMap<>();

        credentials.put("email", new String(Base64.getDecoder().decode(credentialsBase64[0]), StandardCharsets.UTF_8));
        credentials.put("password", new String(Base64.getDecoder().decode(credentialsBase64[1]), StandardCharsets.UTF_8));

        return credentials;
    }

    public void askForBiometricsIfAvailable(BiometricPrompt.AuthenticationCallback callback) {
        BiometricManager biometricManager = BiometricManager.from(activity);
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS) {
            BiometricPrompt biometricPrompt = new BiometricPrompt((FragmentActivity) activity, callback);

            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Biometric Authentication")
                    .setSubtitle("Please authenticate with your biometrics")
                    .setNegativeButtonText("Cancel")
                    .build();

            biometricPrompt.authenticate(promptInfo);
        }
    }

    public void askIfUseBiometricsIfAvailable(BiometricPrompt.AuthenticationCallback callback) {
        BiometricManager biometricManager = BiometricManager.from(activity);
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS) {
            new MaterialAlertDialogBuilder(activity)
                    .setTitle("Use biometrics")
                    .setMessage("Use biometrics for authentication?")
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            callback.onAuthenticationFailed();
                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            BiometricPrompt biometricPrompt = new BiometricPrompt((FragmentActivity) activity, callback);

                            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                                    .setTitle("Biometric Authentication")
                                    .setSubtitle("Please authenticate with your biometrics")
                                    .setNegativeButtonText("Cancel")
                                    .build();

                            biometricPrompt.authenticate(promptInfo);
                        }
                    })
                    .show();
        }
    }
}
