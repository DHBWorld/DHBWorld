package com.main.dhbworld.Backup;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.DocumentsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.main.dhbworld.DashboardActivity;
import com.main.dhbworld.R;
import com.main.dhbworld.Utilities.ProgressDialog;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;

public class BackupHandler {

    protected static final byte[] ENC_MAGIC_NUMBER = new byte[]{40, 80, 20, 50};
    protected static final byte[] NOENC_MAGIC_NUMBER = new byte[]{50, 80, 20, 50};

    public static void exportBackup(Activity activity, URI pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/octet-stream");
        intent.putExtra(Intent.EXTRA_TITLE, "Backup.dhbworld");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

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
                .setPositiveButton(R.string.Export, null)
                .setNegativeButton(R.string.cancel, null)
                .setCancelable(false)
                .create();

        alertDialog.setOnShowListener(dialogInterface -> {
            CheckBox dualisExport = alertDialog.findViewById(R.id.dualis_export_checkbox);
            TextInputLayout backupPasswordLayout = alertDialog.findViewById(R.id.backupPasswordLayout);
            TextInputLayout backupPasswordRepeatLayout = alertDialog.findViewById(R.id.backupPasswordRepeatLayout);
            TextInputEditText backupPassword = alertDialog.findViewById(R.id.backupPassword);
            TextInputEditText backupPasswordRepeat = alertDialog.findViewById(R.id.backupPasswordRepeat);
            Button exportButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button cancelButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

            if (dualisExport == null || backupPasswordLayout == null ||
                    backupPasswordRepeatLayout == null || backupPassword == null ||
                    backupPasswordRepeat == null || exportButton == null || cancelButton == null) {
                return;
            }

            dualisExport.setOnCheckedChangeListener((compoundButton, checked) -> {
                if (checked) {
                    backupPasswordLayout.setHint(R.string.password);
                    backupPasswordRepeatLayout.setHint(R.string.repeat_password);
                } else {
                    backupPasswordLayout.setHint(R.string.optional_password);
                    backupPasswordRepeatLayout.setHint(R.string.optional_repeat_password);
                }
            });

            removeErrorOnType(backupPassword, backupPasswordLayout, backupPasswordRepeatLayout);
            removeErrorOnType(backupPasswordRepeat, backupPasswordLayout, backupPasswordRepeatLayout);

            exportButton.setOnClickListener(view -> {
                if (backupPassword.getText() == null || backupPasswordRepeat.getText() == null) {
                    return;
                }

                String password = backupPassword.getText().toString();
                String passwordRepeat = backupPasswordRepeat.getText().toString();

                if (password.isEmpty()) {
                    if (!dualisExport.isChecked()) {
                        ProgressDialog progressDialog = new ProgressDialog(activity)
                                .setTitle(R.string.Export)
                                .setMessage(R.string.please_wait)
                                .setCancelable(false)
                                .show();
                        new Thread(() -> {
                            BackupFileHandler.saveBackupFile(data, activity, false, null);
                            progressDialog.dismiss();
                        }).start();
                    } else {
                        backupPasswordRepeatLayout.setError(activity.getString(R.string.password_reqired));
                    }
                } else {
                    if (password.equals(passwordRepeat)) {
                        ProgressDialog progressDialog = new ProgressDialog(activity)
                                .setTitle(R.string.Export)
                                .setMessage(R.string.please_wait)
                                .setCancelable(false)
                                .show();
                        new Thread(() -> {
                            BackupFileHandler.saveBackupFile(data, activity, dualisExport.isChecked(), password);
                            progressDialog.dismiss();
                        }).start();
                    } else {
                        backupPasswordRepeatLayout.setError(activity.getString(R.string.passwords_dont_match));
                    }
                }
                alertDialog.dismiss();
            });

            cancelButton.setOnClickListener(view -> {
                try {
                    DocumentsContract.deleteDocument(activity.getApplicationContext().getContentResolver(), data.getData());
                    alertDialog.dismiss();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });

        });

        alertDialog.show();
    }



    public static void restoreCheckPassword(Intent data, Activity activity) {
        ProgressDialog progressDialog = new ProgressDialog(activity)
                .setTitle(R.string.import_)
                .setMessage(R.string.please_wait)
                .setCancelable(false)
                .show();
        new Thread(() -> {
            try {
                InputStream inputStreamZip = activity.getContentResolver().openInputStream(data.getData());
                byte[] magicNumber = new byte[4];
                //noinspection ResultOfMethodCallIgnored
                inputStreamZip.read(magicNumber);
                inputStreamZip.close();

                if (Arrays.equals(magicNumber, ENC_MAGIC_NUMBER)) {
                    progressDialog.dismiss();
                    activity.runOnUiThread(() -> {
                        AlertDialog alertDialog = new MaterialAlertDialogBuilder(activity)
                                .setTitle(R.string.restore_backup)
                                .setView(R.layout.dialog_restore)
                                .setPositiveButton(R.string.import_, null)
                                .setNegativeButton(R.string.cancel, null)
                                .setCancelable(false)
                                .create();

                        alertDialog.setOnShowListener(dialogInterface -> {
                            Button importButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            TextInputLayout restorePasswordLayout = alertDialog.findViewById(R.id.restorePasswordLayout);
                            TextInputEditText restorePassword = alertDialog.findViewById(R.id.restorePassword);

                            if (importButton == null || restorePasswordLayout == null || restorePassword == null) {
                                return;
                            }

                            removeErrorOnType(restorePassword, restorePasswordLayout);

                            importButton.setOnClickListener(view -> {
                                if (restorePassword.getText() == null) {
                                    return;
                                }
                                String password = restorePassword.getText().toString();
                                ProgressDialog progressDialog1 = new ProgressDialog(activity)
                                        .setTitle(R.string.import_)
                                        .setMessage(R.string.please_wait)
                                        .setCancelable(false)
                                        .show();
                                new Thread(() -> {
                                    boolean success = BackupFileHandler.restoreFile(data, activity, password);
                                    progressDialog1.dismiss();
                                    if (success) {
                                        alertDialog.dismiss();
                                    } else {
                                        activity.runOnUiThread(() -> restorePasswordLayout.setError(activity.getString(R.string.password_or_file_wrong)));
                                    }
                                }).start();
                            });
                        });

                        alertDialog.show();
                    });
                } else if (Arrays.equals(magicNumber, NOENC_MAGIC_NUMBER)) {
                    BackupFileHandler.restoreFile(data, activity, null);
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.no_backup_file), BaseTransientBottomBar.LENGTH_SHORT).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    protected static void showAppNeedsRestart(Activity activity) {
        activity.runOnUiThread(() -> new MaterialAlertDialogBuilder(activity)
                .setTitle(R.string.needs_restart)
                .setMessage(R.string.needs_restart_helptext)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    Intent intent = new Intent(activity, DashboardActivity.class);
                    System.exit(0);
                    activity.startActivity(intent);
                })
                .setCancelable(false)
                .show());
    }

     private static void removeErrorOnType(TextInputEditText editText, TextInputLayout... layouts) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                for (TextInputLayout layout: layouts) {
                    layout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
