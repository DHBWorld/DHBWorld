package com.main.dhbworld.Backup.BackupHandler;

import android.app.Activity;
import android.content.Intent;
import android.provider.DocumentsContract;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.activity.result.ActivityResultLauncher;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.main.dhbworld.DashboardActivity;
import com.main.dhbworld.R;

import java.net.URI;

public class BackupHandler {

    public static final byte[] ENC_MAGIC_NUMBER = new byte[]{40, 80, 20, 50};
    public static final byte[] NOENC_MAGIC_NUMBER = new byte[]{50, 80, 20, 50};

    public static void exportBackup(ActivityResultLauncher<Intent> activity, URI pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/octet-stream");
        intent.putExtra(Intent.EXTRA_TITLE, "Backup.dhbworld");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        activity.launch(intent);
    }

    public static void restoreBackup(ActivityResultLauncher<Intent> activity, URI pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/octet-stream");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        activity.launch(intent);
    }

    public static void saveBackupAskPassword(Intent data, Activity activity) {
        new BackupSaverGUI(data, activity).show();
    }



    public static void restoreCheckPassword(Intent data, Activity activity) {
        new BackupRestorerGUI(data, activity).show();
    }

    public static void showAppNeedsRestart(Activity activity) {
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

     public static void removeErrorOnType(TextInputEditText editText, TextInputLayout... layouts) {
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
