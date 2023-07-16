package com.main.dhbworld.Backup.BackupHandler;

import android.app.Activity;
import android.content.Intent;
import android.provider.DocumentsContract;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.main.dhbworld.Backup.FileHandler.BackupFileHandler;
import com.main.dhbworld.R;
import com.main.dhbworld.Utilities.ProgressDialog;

import java.io.FileNotFoundException;

public class BackupSaverGUI {

    private final Intent data;
    private final Activity activity;

    private CheckBox dualisExport;
    private TextInputLayout backupPasswordLayout;
    private TextInputLayout backupPasswordRepeatLayout;
    private TextInputEditText backupPassword;
    private TextInputEditText backupPasswordRepeat;
    private Button exportButton;
    private Button cancelButton;

    public BackupSaverGUI(Intent data, Activity activity) {
        this.data = data;
        this.activity = activity;
    }

    public void show() {
        AlertDialog alertDialog = createMainDialog(activity);
        alertDialog.setOnShowListener(dialogInterface -> onDialogShow(data, activity, alertDialog));
        alertDialog.show();
    }

    private AlertDialog createMainDialog(Activity activity) {
        return new MaterialAlertDialogBuilder(activity)
                .setTitle(R.string.export_backup)
                .setView(R.layout.dialog_backup)
                .setPositiveButton(R.string.Export, null)
                .setNegativeButton(R.string.cancel, null)
                .setCancelable(false)
                .create();
    }

    private void onDialogShow(Intent data, Activity activity, AlertDialog alertDialog) {
        initializeViews(alertDialog);
        setDualisCheckedListener();
        initializeErrorRemoval();

        exportButton.setOnClickListener(view -> export(data, activity, alertDialog));

        cancelButton.setOnClickListener(view -> cancelDialog(data, activity, alertDialog));
    }

    private void initializeViews(AlertDialog alertDialog) {
        dualisExport = alertDialog.findViewById(R.id.dualis_export_checkbox);
        backupPasswordLayout = alertDialog.findViewById(R.id.backupPasswordLayout);
        backupPasswordRepeatLayout = alertDialog.findViewById(R.id.backupPasswordRepeatLayout);
        backupPassword = alertDialog.findViewById(R.id.backupPassword);
        backupPasswordRepeat = alertDialog.findViewById(R.id.backupPasswordRepeat);
        exportButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        cancelButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
    }

    private void setDualisCheckedListener() {
        dualisExport.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                backupPasswordLayout.setHint(R.string.password);
                backupPasswordRepeatLayout.setHint(R.string.repeat_password);
            } else {
                backupPasswordLayout.setHint(R.string.optional_password);
                backupPasswordRepeatLayout.setHint(R.string.optional_repeat_password);
            }
        });
    }

    private void initializeErrorRemoval() {
        BackupHandler.removeErrorOnType(backupPassword, backupPasswordLayout, backupPasswordRepeatLayout);
        BackupHandler.removeErrorOnType(backupPasswordRepeat, backupPasswordLayout, backupPasswordRepeatLayout);
    }

    private void export(Intent data, Activity activity, AlertDialog alertDialog) {
        if (backupPassword.getText() == null || backupPasswordRepeat.getText() == null) {
            return;
        }

        String password = backupPassword.getText().toString();
        String passwordRepeat = backupPasswordRepeat.getText().toString();

        checkPassword(data, activity, password, passwordRepeat);
        alertDialog.dismiss();
    }

    private void checkPassword(Intent data, Activity activity, String password, String passwordRepeat) {
        if (password.isEmpty()) {
            if (!dualisExport.isChecked()) {
                startExport(data, activity, false, null);
            } else {
                backupPasswordRepeatLayout.setError(activity.getString(R.string.password_reqired));
            }
        } else {
            if (password.equals(passwordRepeat)) {
                startExport(data, activity, dualisExport.isChecked(), password);
            } else {
                backupPasswordRepeatLayout.setError(activity.getString(R.string.passwords_dont_match));
            }
        }
    }

    private void startExport(Intent data, Activity activity, boolean exportDualis, String password) {
        ProgressDialog progressDialog = new ProgressDialog(activity)
                .setTitle(R.string.Export)
                .setMessage(R.string.please_wait)
                .setCancelable(false)
                .show();
        new Thread(() -> {
            BackupFileHandler.saveBackupFile(data, activity, exportDualis, password);
            progressDialog.dismiss();
        }).start();
    }

    private void cancelDialog(Intent data, Activity activity, AlertDialog alertDialog) {
        try {
            if (data.getData() == null) {
                throw new FileNotFoundException();
            }
            DocumentsContract.deleteDocument(activity.getApplicationContext().getContentResolver(), data.getData());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            alertDialog.dismiss();
        }
    }
}
