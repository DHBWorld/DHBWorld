package com.main.dhbworld.Backup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.DocumentsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.main.dhbworld.DashboardActivity;
import com.main.dhbworld.Dualis.SecureStore;
import com.main.dhbworld.R;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class BackupHandler {

    private static final byte[] ENC_MAGIC_NUMBER = new byte[]{40, 80, 20, 50};
    private static final byte[] NOENC_MAGIC_NUMBER = new byte[]{50, 80, 20, 50};

    public static void exportBackup(Activity activity, URI pickerInitialUri) {

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/octet-stream");
        intent.putExtra(Intent.EXTRA_TITLE, "Backup.dhbworld");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
        intent.putExtra("test", "HallodasisteinTest");

        activity.startActivityForResult(intent, 2);
    }

    public static void restoreBackup(Activity activity, URI pickerInitialUri) {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/octet-stream");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        activity.startActivityForResult(intent, 3);
    }

    public static void saveBackupAskPassword(Intent data, Activity activity) {
        AlertDialog alertDialog = new MaterialAlertDialogBuilder(activity)
                .setTitle(R.string.export_backup)
                .setView(R.layout.dialog_backup)
                .setPositiveButton("Exportieren", null)
                .setNegativeButton(R.string.cancel, null)
                .setCancelable(false)
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                CheckBox dualisExport = alertDialog.findViewById(R.id.dualis_export_checkbox);
                TextInputLayout backupPasswordLayout = alertDialog.findViewById(R.id.backupPasswordLayout);
                TextInputLayout backupPasswordRepeatLayout = alertDialog.findViewById(R.id.backupPasswordRepeatLayout);
                TextInputEditText backupPassword = alertDialog.findViewById(R.id.backupPassword);
                TextInputEditText backupPasswordRepeat = alertDialog.findViewById(R.id.backupPasswordRepeat);
                Button exportButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button cancelButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                dualisExport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                        if (checked) {
                            backupPasswordLayout.setHint("Passwort");
                            backupPasswordRepeatLayout.setHint("Passwort wiederholen");
                        } else {
                            backupPasswordLayout.setHint("(Optional) Passwort");
                            backupPasswordRepeatLayout.setHint("(Optional) Passwort wiederholen");
                        }
                    }
                });

                backupPassword.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        backupPasswordLayout.setError(null);
                        backupPasswordRepeatLayout.setError(null);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                backupPasswordRepeat.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        backupPasswordLayout.setError(null);
                        backupPasswordRepeatLayout.setError(null);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                exportButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String password = backupPassword.getText().toString();
                        String passwordRepeat = backupPasswordRepeat.getText().toString();

                        if (password.isEmpty()) {
                            if (!dualisExport.isChecked()) {
                                ProgressDialog progressDialog = new ProgressDialog(activity);
                                progressDialog.setTitle("Export");
                                progressDialog.setMessage("Bitte warten");
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        saveBackup(data, activity, false, null);
                                        progressDialog.dismiss();
                                    }
                                }).start();
                            } else {
                                backupPasswordRepeatLayout.setError("Passwort muss gesetzt sein");
                            }
                        } else {
                            if (password.equals(passwordRepeat)) {
                                ProgressDialog progressDialog = new ProgressDialog(activity);
                                progressDialog.setTitle("Export");
                                progressDialog.setMessage("Bitte warten");
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        saveBackup(data, activity, dualisExport.isChecked(), password);
                                        progressDialog.dismiss();
                                    }
                                }).start();
                            } else {
                                backupPasswordRepeatLayout.setError("Passwörter stimmen nicht überein");
                            }
                        }
                        alertDialog.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            DocumentsContract.deleteDocument(activity.getContentResolver(), data.getData());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });

        alertDialog.show();
    }

    private static void saveBackup(Intent data, Activity activity, boolean exportDualis, String password) {

        try {
            File fileDualis = new File(activity.getFilesDir().getPath() + "/../shared_prefs/Dualis.xml");
            File fileAll = new File(activity.getFilesDir().getPath() + "/../shared_prefs/com.main.dhbworld_preferences.xml");
            File filePersonal = new File(activity.getFilesDir().getPath() + "/../shared_prefs/myPreferencesKey.xml");

            OutputStream outputStream = activity.getContentResolver().openOutputStream(data.getData());
            if (password != null) {
                outputStream.write(ENC_MAGIC_NUMBER); //magic number encrypted
            } else {
                outputStream.write(NOENC_MAGIC_NUMBER); //magic number unencrypted
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
                    String credentialData = credentials.get("email") +
                            "::_::" +
                            Base64.getEncoder().encodeToString(credentials.get("password").getBytes(StandardCharsets.UTF_8)) +
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
                encrypt(password, inputStream, outputStream);
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

    public static void restoreCheckPassword(Intent data, Activity activity) {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("Import");
        progressDialog.setMessage("Bitte warten");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream inputStreamZip = activity.getContentResolver().openInputStream(data.getData());
                    byte[] magicNumber = new byte[4];
                    inputStreamZip.read(magicNumber);

                    if (Arrays.equals(magicNumber, ENC_MAGIC_NUMBER)) {
                        progressDialog.dismiss();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog alertDialog = new MaterialAlertDialogBuilder(activity)
                                        .setTitle(R.string.restore_backup)
                                        .setView(R.layout.dialog_restore)
                                        .setPositiveButton("Importieren", null)
                                        .setNegativeButton(R.string.cancel, null)
                                        .setCancelable(false)
                                        .create();

                                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialogInterface) {
                                        Button importButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                                        TextInputLayout restorePasswordLayout = alertDialog.findViewById(R.id.restorePasswordLayout);
                                        TextInputEditText restorePassword = alertDialog.findViewById(R.id.restorePassword);

                                        restorePassword.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                                restorePasswordLayout.setError(null);
                                            }

                                            @Override
                                            public void afterTextChanged(Editable editable) {

                                            }
                                        });

                                        importButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                String password = restorePassword.getText().toString();
                                                ProgressDialog progressDialog = new ProgressDialog(activity);
                                                progressDialog.setTitle("Export");
                                                progressDialog.setMessage("Bitte warten");
                                                progressDialog.setCancelable(false);
                                                progressDialog.show();
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        boolean success = restoreFile(data, activity, password);
                                                        progressDialog.dismiss();
                                                        if (success) {
                                                            alertDialog.dismiss();
                                                        } else {
                                                            activity.runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    restorePasswordLayout.setError("Passwort oder Datei falsch");
                                                                }
                                                            });
                                                        }
                                                    }
                                                }).start();
                                            }
                                        });
                                    }
                                });

                                alertDialog.show();
                            }
                        });
                    } else if (Arrays.equals(magicNumber, NOENC_MAGIC_NUMBER)) {
                        restoreFile(data, activity, null);
                        progressDialog.dismiss();
                    } else {
                        progressDialog.dismiss();
                        Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.no_backup_file), BaseTransientBottomBar.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static boolean restoreFile(Intent data, Activity activity, String filePassword) {
        try {
            InputStream inputStreamZip = activity.getContentResolver().openInputStream(data.getData());

            InputStream inputStream;
            if (filePassword != null) {
                inputStream = decrypt(filePassword, inputStreamZip);
            } else {
                byte[] magicNmber = new byte[4];
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
            showAppNeedsRestart(activity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void showAppNeedsRestart(Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MaterialAlertDialogBuilder(activity)
                        .setTitle("Neustart erforderlich")
                        .setMessage("Das Backup wurde erfolgreich wiederhergestellt. Die App wird neu gestartet, um die Änderungen anzuwenden.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(activity, DashboardActivity.class);
                                System.exit(0);
                                activity.startActivity(intent);
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });
    }

    private static InputStream decrypt(String password, InputStream in) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        reader.lines().forEach(stringBuilder::append);
        reader.close();
        String file = stringBuilder.toString();
        byte[] fileBytes = file.getBytes(StandardCharsets.UTF_8);
        byte[] magicnumber = Arrays.copyOfRange(fileBytes, 0, 4);

        if (Arrays.equals(magicnumber, ENC_MAGIC_NUMBER)) {
            file = new String(Arrays.copyOfRange(fileBytes, 4, fileBytes.length));
        }

        String[] data = file.split("__:__");
        byte[] encryptedData = Base64.getDecoder().decode(data[0]);
        byte[] iv = Base64.getDecoder().decode(data[1]);
        byte[] salt = Base64.getDecoder().decode(data[2]);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        final GCMParameterSpec paramSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);

        ByteArrayInputStream bis = new ByteArrayInputStream(encryptedData);

        return new CipherInputStream(bis, cipher);
    }

    private static void encrypt(String password, InputStream in, OutputStream out) throws Exception {

        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[8];
        secureRandom.nextBytes(salt);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] iv = cipher.getIV();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        CipherOutputStream cos = new CipherOutputStream(outputStream, cipher);

        IOUtils.copy(in, cos);

        cos.flush();
        cos.close();
        outputStream.flush();

        byte[] data = outputStream.toByteArray();
        String data64 = Base64.getEncoder().encodeToString(data);

        ByteArrayOutputStream concat = new ByteArrayOutputStream();
        concat.write(data64.getBytes(StandardCharsets.UTF_8));
        concat.write("__:__".getBytes(StandardCharsets.UTF_8));
        concat.write(Base64.getEncoder().encode(iv));
        concat.write("__:__".getBytes(StandardCharsets.UTF_8));
        concat.write(Base64.getEncoder().encode(salt));
        concat.flush();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(concat.toByteArray());

        IOUtils.copy(inputStream, out);

        concat.close();
        inputStream.close();
        out.close();

    }

    private static Map<String, byte[]> encrypt(String password, String[] datas) throws Exception {
        Map<String, byte[]> returnData = new HashMap<>();

        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[8];
        secureRandom.nextBytes(salt);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptionIv = cipher.getIV();

        for (int i = 0; i < datas.length; i++) {
            String data = datas[i];
            byte[] dataBase64 = Base64.getEncoder().encode(data.getBytes(StandardCharsets.UTF_8));
            byte[] dataEncrypted = cipher.doFinal(dataBase64);
            returnData.put("data" + i, dataEncrypted);
        }


        returnData.put("iv", encryptionIv);
        returnData.put("salt", salt);

        return returnData;
    }
}
