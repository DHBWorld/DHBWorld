package com.main.dhbworld.Dualis;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.main.dhbworld.R;

import java.io.IOException;
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
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class SecureStore {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context activity;

    public SecureStore(Context activity, SharedPreferences sharedPreferences) {
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
        final Cipher cipher = getCipherEncryption();

        String credentialsBase64 = Base64.getEncoder().encodeToString(email.getBytes(StandardCharsets.UTF_8))
                + ":" +
                Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8));

        byte[] encryptionIv = cipher.getIV();
        byte[] credentialsEncrypted = cipher.doFinal(credentialsBase64.getBytes(StandardCharsets.UTF_8));

        saveCredentials(encryptionIv, credentialsEncrypted);
    }

    @NonNull
    private static Cipher getCipherEncryption() throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException {
        final KeyGenerator keyGenerator = KeyGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

        final KeyGenParameterSpec keyGenParameterSpec = getKeyGenParameterSpec();
        keyGenerator.init(keyGenParameterSpec);

        final SecretKey secretKey = keyGenerator.generateKey();
        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher;
    }

    @NonNull
    private static KeyGenParameterSpec getKeyGenParameterSpec() {
        return new KeyGenParameterSpec.Builder("dualisCredentials",
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build();
    }

    private void saveCredentials(byte[] encryptionIv, byte[] credentialsEncrypted) {
        editor.putString("dualisIV", Base64.getEncoder().encodeToString(encryptionIv));
        editor.putString("dualisCredentials", Base64.getEncoder().encodeToString(credentialsEncrypted));
        editor.apply();
    }

    public Map<String, String> loadCredentials() throws Exception {
        final byte[] encryptionIv = Base64.getDecoder().decode(sharedPreferences.getString("dualisIV", ""));
        final byte[] encryptedCredentials = Base64.getDecoder().decode(sharedPreferences.getString("dualisCredentials", ""));

        final Cipher cipher = getCipherDecryption(encryptionIv);

        final byte[] decodedData = cipher.doFinal(encryptedCredentials);
        final String[] credentialsBase64 = new String(decodedData, StandardCharsets.UTF_8).split(":");

        return getCredentialsMap(credentialsBase64);
    }

    @NonNull
    private static Map<String, String> getCredentialsMap(String[] credentialsBase64) {
        Map<String, String> credentials = new HashMap<>();

        credentials.put("email", new String(Base64.getDecoder().decode(credentialsBase64[0]), StandardCharsets.UTF_8));
        credentials.put("password", new String(Base64.getDecoder().decode(credentialsBase64[1]), StandardCharsets.UTF_8));
        return credentials;
    }

    @NonNull
    private static Cipher getCipherDecryption(byte[] encryptionIv) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableEntryException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore
                .getEntry( "dualisCredentials", null);

        final SecretKey secretKey = secretKeyEntry.getSecretKey();

        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        final GCMParameterSpec spec = new GCMParameterSpec(128, encryptionIv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
        return cipher;
    }

    public void askForBiometricsIfAvailable(BiometricPrompt.AuthenticationCallback callback) {
        BiometricManager biometricManager = BiometricManager.from(activity);
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS) {
            biometricPromt(callback);
        }
    }

    public boolean askIfUseBiometricsIfAvailable(BiometricPrompt.AuthenticationCallback callback) {
        BiometricManager biometricManager = BiometricManager.from(activity);
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS) {
            new MaterialAlertDialogBuilder(activity)
                    .setTitle(R.string.use_biometrics)
                    .setMessage(R.string.use_biometrics_for_authentication)
                    .setNegativeButton(R.string.no, (dialogInterface, i) -> callback.onAuthenticationFailed())
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> biometricPromt(callback))
                    .show();
            return true;
        } else {
            return false;
        }
    }

    private void biometricPromt(BiometricPrompt.AuthenticationCallback callback) {
        BiometricPrompt biometricPrompt = new BiometricPrompt((FragmentActivity) activity, callback);

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(activity.getString(R.string.biometric_authentication))
                .setSubtitle(activity.getString(R.string.authenticate_with_biometrics))
                .setNegativeButtonText(activity.getString(android.R.string.cancel))
                .build();

        biometricPrompt.authenticate(promptInfo);
    }
}
