package com.main.dhbworld.Backup.FileHandler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.main.dhbworld.Backup.BackupCipher;
import com.main.dhbworld.Backup.BackupHandler.BackupHandler;
import com.main.dhbworld.Dualis.SecureStore;
import com.main.dhbworld.R;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BackupSaver {

    private static File fileDualis;
    private static File fileAll;
    private static File filePersonal;
    private static File fileFirebaseUser;

    static void saveBackup(Intent data, Activity activity, boolean exportDualis, String password) {
        try {
            initializeFiles(activity);
            OutputStream outputStream = initializeOutputStream(data, activity, password);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

            putFiles(activity, exportDualis, zipOutputStream);
            encrypt(password, outputStream, byteArrayOutputStream);

            Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.backup_saved), BaseTransientBottomBar.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.default_error_msg), BaseTransientBottomBar.LENGTH_SHORT).show();
        }
    }

    private static void initializeFiles(Activity activity) {
        fileDualis = new File(activity.getFilesDir().getPath() + "/../shared_prefs/Dualis.xml");
        fileAll = new File(activity.getFilesDir().getPath() + "/../shared_prefs/com.main.dhbworld_preferences.xml");
        filePersonal = new File(activity.getFilesDir().getPath() + "/../shared_prefs/myPreferencesKey.xml");
        File sharedPrefsDir = new File(activity.getFilesDir().getPath() + "/../shared_prefs/");
        fileFirebaseUser = null;
        File[] sharedPrefsFiles = sharedPrefsDir.listFiles();
        if (sharedPrefsFiles != null) {
            for (File file : sharedPrefsFiles) {
                if (file.getName().matches("com[.]google[.]firebase[.]auth[.]api[.]Store[.].*[.]xml")) {
                    fileFirebaseUser = file;
                }
            }
        }
    }

    private static OutputStream initializeOutputStream(Intent data, Activity activity, String password) throws IOException {
        if (data.getData() == null) {
            throw new IOException();
        }
        OutputStream outputStream = activity.getContentResolver().openOutputStream(data.getData());
        if (outputStream == null) {
            throw new IOException();
        }
        if (password != null) {
            outputStream.write(BackupHandler.ENC_MAGIC_NUMBER); //magic number encrypted
        } else {
            outputStream.write(BackupHandler.NOENC_MAGIC_NUMBER); //magic number unencrypted
        }
        return outputStream;
    }

    private static void putFiles(Activity activity, boolean exportDualis, ZipOutputStream zipOutputStream) throws IOException {
        putFileDualis(activity, exportDualis, zipOutputStream);
        putFile(fileAll, "com.main.dhbworld_preferences.xml", zipOutputStream);
        putFile(filePersonal, "myPreferencesKey.xml", zipOutputStream);
        putFile(fileFirebaseUser, fileFirebaseUser.getName(), zipOutputStream);

        zipOutputStream.flush();
        zipOutputStream.close();
    }

    private static void putFileDualis(Activity activity, boolean exportDualis, ZipOutputStream zipOutputStream) throws IOException {
        if (fileDualis.exists() && exportDualis) {
            SharedPreferences sharedPreferences = activity.getSharedPreferences("Dualis", Context.MODE_PRIVATE);
            Map<String, String> credentials = loadCredentials(activity, sharedPreferences);

            if (credentials != null) {
                String email = credentials.get("email");
                String passwordDualis = credentials.get("password");
                if (email != null && passwordDualis != null) {
                    String credentialData = formatCredentials(sharedPreferences, email, passwordDualis);
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(credentialData.getBytes(StandardCharsets.UTF_8));

                    putDualisFileZip(zipOutputStream, inputStream);
                }
            }
        }
    }

    private static Map<String, String> loadCredentials(Activity activity, SharedPreferences sharedPreferences) {
        Map<String, String> credentials = null;
        try {
            SecureStore secureStore = new SecureStore(activity, sharedPreferences);
            credentials = secureStore.loadCredentials();
        } catch (Exception ignored) { }
        return credentials;
    }


    private static String formatCredentials(SharedPreferences sharedPreferences, String email, String passwordDualis) {
        return email +
                "::_::" +
                Base64.getEncoder().encodeToString(passwordDualis.getBytes(StandardCharsets.UTF_8)) +
                "::_::" +
                sharedPreferences.getBoolean("saveCredentials", false) +
                "::_::" +
                sharedPreferences.getBoolean("alreadyAskedBiometrics", false) +
                "::_::" +
                sharedPreferences.getBoolean("useBiometrics", false);
    }

    private static void putDualisFileZip(ZipOutputStream zipOutputStream, ByteArrayInputStream inputStream) throws IOException {
        ZipEntry zipEntry = new ZipEntry("Dualis.xml");
        zipOutputStream.putNextEntry(zipEntry);
        IOUtils.copy(inputStream, zipOutputStream);
        inputStream.close();
        zipOutputStream.closeEntry();
    }

    private static void putFile(File file, String fileName, ZipOutputStream zipOutputStream) throws IOException {
        if (file != null && file.exists()) {
            FileInputStream fileInputStreamDualis = new FileInputStream(file);
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOutputStream.putNextEntry(zipEntry);
            IOUtils.copy(fileInputStreamDualis, zipOutputStream);
            fileInputStreamDualis.close();
            zipOutputStream.closeEntry();
        }
    }

    private static void encrypt(String password, OutputStream outputStream, ByteArrayOutputStream byteArrayOutputStream) throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        if (password != null) {
            BackupCipher.encrypt(password, inputStream, outputStream);
        } else {
            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        }
    }
}
