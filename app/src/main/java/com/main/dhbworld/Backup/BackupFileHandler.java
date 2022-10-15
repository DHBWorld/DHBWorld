package com.main.dhbworld.Backup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.main.dhbworld.Dualis.SecureStore;
import com.main.dhbworld.R;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

class BackupFileHandler {
    protected static boolean restoreFile(Intent data, Activity activity, String filePassword) {
        try {
            InputStream inputStreamZip = activity.getContentResolver().openInputStream(data.getData());

            InputStream inputStream;
            if (filePassword != null) {
                inputStream = BackupCipher.decrypt(filePassword, inputStreamZip);
            } else {
                byte[] magicNmber = new byte[4];
                //noinspection ResultOfMethodCallIgnored
                inputStreamZip.read(magicNmber);
                inputStream = inputStreamZip;
            }

            ZipInputStream zipInputStream = new ZipInputStream(inputStream);

            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String name = zipEntry.getName();

                if (name.equals("Dualis.xml")) {
                    StringBuilder builder = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream));
                    reader.lines().forEach(builder::append);

                    String[] credentialData = builder.toString().split("::_::");
                    String email = credentialData[0];
                    String password = new String(Base64.getDecoder().decode(credentialData[1]), StandardCharsets.UTF_8);
                    boolean saveCredentials = Boolean.parseBoolean(credentialData[2]);
                    boolean alreadyAskedBiometrics = Boolean.parseBoolean(credentialData[3]);
                    boolean useBiometrics = Boolean.parseBoolean(credentialData[4]);

                    SharedPreferences sharedPreferences = activity.getSharedPreferences("Dualis", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    SecureStore secureStore = new SecureStore(activity, sharedPreferences);
                    secureStore.saveCredentials(email, password);

                    editor.putBoolean("saveCredentials", saveCredentials);
                    editor.putBoolean("alreadyAskedBiometrics", alreadyAskedBiometrics);
                    editor.putBoolean("useBiometrics", useBiometrics);
                    editor.apply();
                } else {
                    FileOutputStream outputStream = new FileOutputStream(activity.getFilesDir().getPath() + "/../shared_prefs/" + name);
                    IOUtils.copy(zipInputStream, outputStream);
                    zipInputStream.closeEntry();
                    outputStream.close();
                }
            }

            zipInputStream.close();
            inputStreamZip.close();

            Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.backup_restored), BaseTransientBottomBar.LENGTH_SHORT).show();
            BackupHandler.showAppNeedsRestart(activity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    protected static void saveBackupFile(Intent data, Activity activity, boolean exportDualis, String password) {

        try {
            File fileDualis = new File(activity.getFilesDir().getPath() + "/../shared_prefs/Dualis.xml");
            File fileAll = new File(activity.getFilesDir().getPath() + "/../shared_prefs/com.main.dhbworld_preferences.xml");
            File filePersonal = new File(activity.getFilesDir().getPath() + "/../shared_prefs/myPreferencesKey.xml");

            OutputStream outputStream = activity.getContentResolver().openOutputStream(data.getData());
            if (password != null) {
                outputStream.write(BackupHandler.ENC_MAGIC_NUMBER); //magic number encrypted
            } else {
                outputStream.write(BackupHandler.NOENC_MAGIC_NUMBER); //magic number unencrypted
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

            if (fileDualis.exists() && exportDualis) {
                SharedPreferences sharedPreferences = activity.getSharedPreferences("Dualis", Context.MODE_PRIVATE);
                Map<String, String> credentials = null;
                try {
                    SecureStore secureStore = new SecureStore(activity, sharedPreferences);
                    credentials = secureStore.loadCredentials();
                } catch (Exception ignored) { }

                if (credentials != null) {
                    String email = credentials.get("email");
                    String passwordDualis = credentials.get("password");
                    if (email != null && passwordDualis != null) {
                        String credentialData = email +
                                "::_::" +
                                Base64.getEncoder().encodeToString(passwordDualis.getBytes(StandardCharsets.UTF_8)) +
                                "::_::" +
                                sharedPreferences.getBoolean("saveCredentials", false) +
                                "::_::" +
                                sharedPreferences.getBoolean("alreadyAskedBiometrics", false) +
                                "::_::" +
                                sharedPreferences.getBoolean("useBiometrics", false);

                        ByteArrayInputStream inputStream = new ByteArrayInputStream(credentialData.getBytes(StandardCharsets.UTF_8));

                        ZipEntry zipEntry = new ZipEntry("Dualis.xml");
                        zipOutputStream.putNextEntry(zipEntry);
                        IOUtils.copy(inputStream, zipOutputStream);
                        inputStream.close();
                        zipOutputStream.closeEntry();
                    }
                }
            }

            if (fileAll.exists()) {
                FileInputStream fileInputStream = new FileInputStream(fileAll);
                ZipEntry zipEntry = new ZipEntry("com.main.dhbworld_preferences.xml");
                zipOutputStream.putNextEntry(zipEntry);
                IOUtils.copy(fileInputStream, zipOutputStream);
                fileInputStream.close();
                zipOutputStream.closeEntry();
            }

            if (filePersonal.exists()) {
                FileInputStream fileInputStreamDualis = new FileInputStream(filePersonal);
                ZipEntry zipEntry = new ZipEntry("myPreferencesKey.xml");
                zipOutputStream.putNextEntry(zipEntry);
                IOUtils.copy(fileInputStreamDualis, zipOutputStream);
                fileInputStreamDualis.close();
                zipOutputStream.closeEntry();
            }

            zipOutputStream.flush();
            zipOutputStream.close();

            ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

            if (password != null) {
                BackupCipher.encrypt(password, inputStream, outputStream);
            } else {
                IOUtils.copy(inputStream, outputStream);
                inputStream.close();
                outputStream.close();
            }

            Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.backup_saved), BaseTransientBottomBar.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.default_error_msg), BaseTransientBottomBar.LENGTH_SHORT).show();
        }

    }
}
