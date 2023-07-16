package com.main.dhbworld.Backup.BackupHandler;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.main.dhbworld.Backup.FileHandler.BackupFileHandler;
import com.main.dhbworld.R;
import com.main.dhbworld.Utilities.ProgressDialog;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class BackupRestorerGUI {

    private final Intent data;
    private final Activity activity;

    public BackupRestorerGUI(Intent data, Activity activity) {
        this.data = data;
        this.activity = activity;
    }

    public void show() {
        ProgressDialog progressDialog = showProgressDialog();
        new Thread(() -> {
            try {
                byte[] magicNumber = readMagicNumber();

                if (Arrays.equals(magicNumber, BackupHandler.ENC_MAGIC_NUMBER)) {
                    startRestoreGUI(progressDialog);
                } else if (Arrays.equals(magicNumber, BackupHandler.NOENC_MAGIC_NUMBER)) {
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

    private ProgressDialog showProgressDialog() {
        return new ProgressDialog(activity)
                .setTitle(R.string.import_)
                .setMessage(R.string.please_wait)
                .setCancelable(false)
                .show();
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    private byte[] readMagicNumber() throws IOException {
        if (data.getData() == null) {
            throw new IOException();
        }
        InputStream inputStreamZip = activity.getContentResolver().openInputStream(data.getData());
        if (inputStreamZip == null) {
            throw new IOException();
        }

        byte[] magicNumber = new byte[4];
        inputStreamZip.read(magicNumber);
        inputStreamZip.close();
        return magicNumber;
    }

    private void startRestoreGUI(ProgressDialog progressDialog) {
        progressDialog.dismiss();
        activity.runOnUiThread(() -> {
            AlertDialog alertDialog = createMainDialog();

            alertDialog.setOnShowListener(dialogInterface -> onDialogShow(alertDialog));

            alertDialog.show();
        });
    }

    private void onDialogShow(AlertDialog alertDialog) {
        Button importButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        TextInputLayout restorePasswordLayout = alertDialog.findViewById(R.id.restorePasswordLayout);
        TextInputEditText restorePassword = alertDialog.findViewById(R.id.restorePassword);

        if (importButton == null || restorePasswordLayout == null || restorePassword == null) {
            return;
        }

        BackupHandler.removeErrorOnType(restorePassword, restorePasswordLayout);

        importButton.setOnClickListener(view -> startRestore(alertDialog, restorePasswordLayout, restorePassword));
    }

    private void startRestore(AlertDialog alertDialog, TextInputLayout restorePasswordLayout, TextInputEditText restorePassword) {
        if (restorePassword.getText() == null) {
            return;
        }
        String password = restorePassword.getText().toString();
        ProgressDialog progressDialog = showProgressDialog();
        new Thread(() -> {
            boolean success = BackupFileHandler.restoreFile(data, activity, password);
            progressDialog.dismiss();
            if (success) {
                alertDialog.dismiss();
            } else {
                activity.runOnUiThread(() -> restorePasswordLayout.setError(activity.getString(R.string.password_or_file_wrong)));
            }
        }).start();
    }

    private AlertDialog createMainDialog() {
        return new MaterialAlertDialogBuilder(activity)
                .setTitle(R.string.restore_backup)
                .setView(R.layout.dialog_restore)
                .setPositiveButton(R.string.import_, null)
                .setNegativeButton(R.string.cancel, null)
                .setCancelable(false)
                .create();
    }
}
