package com.main.dhbworld.Settings;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.main.dhbworld.Backup.BackupHandler;
import com.main.dhbworld.BuildConfig;
import com.main.dhbworld.Calendar.CalenderSaver;
import com.main.dhbworld.DataPrivacyActivity;
import com.main.dhbworld.Debugging.Debugging;
import com.main.dhbworld.FeedbackActivity;
import com.main.dhbworld.LicenseActivity;
import com.main.dhbworld.MenuReorder.ReorderMenuActivity;
import com.main.dhbworld.R;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class OnPreferenceClickListener {

    private final Context context;
    private final Activity activity;

    public OnPreferenceClickListener(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public boolean information() {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(context);
        dialog.setTitle(R.string.information_about_app);
        dialog.setMessage(context.getResources().getString(R.string.informations_message, BuildConfig.VERSION_NAME));
        dialog.setPositiveButton(context.getResources().getString(R.string.close), (dialog1, which) -> dialog1.dismiss());
        dialog.show();
        return true;
    }

    public boolean licenses() {
        context.startActivity(new Intent(context, LicenseActivity.class));
        return true;
    }

    public boolean dataPrivacy() {
        context.startActivity(new Intent(context, DataPrivacyActivity.class));
        return true;
    }

    public boolean feedback() {
        context.startActivity(new Intent(context, FeedbackActivity.class));
        return true;
    }

    public boolean customizeNotification() {
        Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());

        context.startActivity(settingsIntent);
        return true;
    }

    public boolean calenderURL() {
        LayoutInflater inflater = activity.getLayoutInflater();
        View tempView = inflater.inflate(R.layout.urlalertdialog,null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setView(tempView);
        builder.setTitle("Please enter your Rapla-URL");
        builder.setCancelable(false);
        EditText urlEditText = tempView.findViewById(R.id.urlEditText);
        EditText courseEditText = tempView.findViewById(R.id.urlCourseName);
        EditText courseDirEditText = tempView.findViewById(R.id.urlCourseDirector);

        builder.setPositiveButton(R.string.submit, (dialog, which) -> {
            String courseName = courseEditText.getText().toString().toUpperCase().replace(" ","");
            String courseDirector = courseDirEditText.getText().toString().toLowerCase().replace(" ", "");
            String urlString = urlEditText.getText().toString();

            CalenderSaver.saveCalender(context, courseName, courseDirector, urlString);
        });
        builder.setNegativeButton(R.string.close, null);
        builder.create();
        builder.create().show();
        return true;
    }

    public boolean exportDebugLog(ActivityResultLauncher<Intent> activityResultLauncher) {
        if (getDownloadsDir() != null) {
            Debugging.createFile(activityResultLauncher, getDownloadsDir().toURI());
        }
        return true;
    }

    public void handleDebugLogResult(Intent data) {
        try {
            if (data == null || data.getData() == null) {
                throw new IOException();
            }
            OutputStream outputStream = context.getContentResolver().openOutputStream(data.getData());
            Files.copy(Debugging.getLog(context).toPath(), outputStream);

            Snackbar.make(activity.findViewById(android.R.id.content),
                    context.getString(R.string.file_saved),
                    BaseTransientBottomBar.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Snackbar.make(activity.findViewById(android.R.id.content),
                    context.getString(R.string.default_error_msg),
                    BaseTransientBottomBar.LENGTH_SHORT).show();
        }
    }

    public boolean exportBackup(ActivityResultLauncher<Intent> activityResultLauncherExportBackup) {
        if (getDownloadsDir() != null) {
            BackupHandler.exportBackup(activityResultLauncherExportBackup, getDownloadsDir().toURI());
        }
        return true;
    }

    public void handleExportBackup(Intent data) {
        BackupHandler.saveBackupAskPassword(data, activity);
    }

    public boolean restoreBackup(ActivityResultLauncher<Intent> activityResultLauncherRestoreBackup) {
        if (getDownloadsDir() != null) {
            BackupHandler.restoreBackup(activityResultLauncherRestoreBackup, getDownloadsDir().toURI());
        }
        return true;
    }

    private File getDownloadsDir() {
        return context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
    }

    public void handleRestoreBackup(Intent data) {
        BackupHandler.restoreCheckPassword(data, activity);
    }

    public boolean reorderMenu(ActivityResultLauncher<Intent> activityLauncher) {
        activityLauncher.launch(new Intent(context, ReorderMenuActivity.class));
        return true;
    }
}
