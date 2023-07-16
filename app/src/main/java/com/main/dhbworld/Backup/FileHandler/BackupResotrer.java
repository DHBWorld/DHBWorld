package com.main.dhbworld.Backup.FileHandler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.main.dhbworld.Backup.BackupCipher;
import com.main.dhbworld.Backup.BackupHandler.BackupHandler;
import com.main.dhbworld.Dualis.SecureStore;
import com.main.dhbworld.R;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class BackupResotrer {
    public static boolean restoreBackup(Intent data, Activity activity, String filePassword) {
        try {
            InputStream inputStreamZip = getInputStreamZip(data, activity);

            ZipInputStream zipInputStream = initializeZipInputStream(filePassword, inputStreamZip);
            readZipEntries(activity, zipInputStream, inputStreamZip);

            Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.backup_restored), BaseTransientBottomBar.LENGTH_SHORT).show();
            BackupHandler.showAppNeedsRestart(activity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static InputStream getInputStreamZip(Intent data, Activity activity) throws IOException {
        if (data.getData() == null) {
            throw new IOException();
        }
        InputStream inputStreamZip = activity.getContentResolver().openInputStream(data.getData());
        if (inputStreamZip == null) {
            throw new IOException();
        }
        return inputStreamZip;
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @NonNull
    private static ZipInputStream initializeZipInputStream(String filePassword, InputStream inputStreamZip) throws Exception {
        InputStream inputStream;
        if (filePassword != null) {
            inputStream = BackupCipher.decrypt(filePassword, inputStreamZip);
        } else {
            byte[] magicNmber = new byte[4];
            inputStreamZip.read(magicNmber);
            inputStream = inputStreamZip;
        }

        return new ZipInputStream(inputStream);
    }

    private static void readZipEntries(Activity activity, ZipInputStream zipInputStream, InputStream inputStreamZip) throws Exception {
        ZipEntry zipEntry;
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            String name = zipEntry.getName();

            if (name.equals("Dualis.xml")) {
                restoreDualis(activity, zipInputStream);
            } else {
                restoreFile(activity, zipInputStream, name);
            }
        }

        zipInputStream.close();
        inputStreamZip.close();
    }

    private static void restoreDualis(Activity activity, ZipInputStream zipInputStream) throws Exception {
        String[] credentialData = readCredentialData(zipInputStream);

        String email = credentialData[0];
        String password = new String(Base64.getDecoder().decode(credentialData[1]), StandardCharsets.UTF_8);
        boolean saveCredentials = Boolean.parseBoolean(credentialData[2]);
        boolean alreadyAskedBiometrics = Boolean.parseBoolean(credentialData[3]);
        boolean useBiometrics = Boolean.parseBoolean(credentialData[4]);

        saveDualisData(activity, email, password, saveCredentials, alreadyAskedBiometrics, useBiometrics);
    }

    @NonNull
    private static String[] readCredentialData(ZipInputStream zipInputStream) {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream));
        reader.lines().forEach(builder::append);

        return builder.toString().split("::_::");
    }

    private static void restoreFile(Activity activity, ZipInputStream zipInputStream, String name) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(activity.getFilesDir().getPath() + "/../shared_prefs/" + name);
        IOUtils.copy(zipInputStream, outputStream);
        zipInputStream.closeEntry();
        outputStream.close();
    }

    private static void saveDualisData(Activity activity, String email, String password, boolean saveCredentials, boolean alreadyAskedBiometrics, boolean useBiometrics) throws Exception {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("Dualis", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        SecureStore secureStore = new SecureStore(activity, sharedPreferences);
        secureStore.saveCredentials(email, password);

        editor.putBoolean("saveCredentials", saveCredentials);
        editor.putBoolean("alreadyAskedBiometrics", alreadyAskedBiometrics);
        editor.putBoolean("useBiometrics", useBiometrics);
        editor.apply();
    }
}
