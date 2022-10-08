package com.main.dhbworld.Backup;

import android.app.Activity;
import android.content.Intent;
import android.provider.DocumentsContract;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.main.dhbworld.R;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class BackupHandler {
   public static void exportBackup(Activity activity, URI pickerInitialUri) {

       //TODO: Information über Backup mit Dialog
       //TODO: Dualis mit Passwort verschlüsseln

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

    public static void saveBackup(Intent data, Activity activity) {
        try {
            File fileDualis = new File(activity.getFilesDir().getPath() + "/../shared_prefs/Dualis.xml");
            File fileAll = new File(activity.getFilesDir().getPath() + "/../shared_prefs/com.main.dhbworld_preferences.xml");
            File filePersonal = new File(activity.getFilesDir().getPath() + "/../shared_prefs/myPreferencesKey.xml");

            OutputStream outputStream = activity.getContentResolver().openOutputStream(data.getData());

            ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

            if (fileDualis.exists()) {
                FileInputStream fileInputStream = new FileInputStream(fileDualis);
                ZipEntry zipEntry = new ZipEntry("Dualis.xml");
                zipOutputStream.putNextEntry(zipEntry);
                IOUtils.copy(fileInputStream, zipOutputStream);
                fileInputStream.close();
                zipOutputStream.closeEntry();
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

            zipOutputStream.close();
            outputStream.close();

            Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.backup_saved), BaseTransientBottomBar.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.default_error_msg), BaseTransientBottomBar.LENGTH_SHORT).show();
        }
    }

    public static void restoreFile(Intent data, Activity activity) {
        try {
            InputStream inputStreamZip = activity.getContentResolver().openInputStream(data.getData());

            ZipInputStream zipInputStream = new ZipInputStream(inputStreamZip);

            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String name = zipEntry.getName();

                FileOutputStream outputStream = new FileOutputStream(activity.getFilesDir().getPath() + "/../shared_prefs/" + name);
                IOUtils.copy(zipInputStream, outputStream);
                zipInputStream.closeEntry();
                outputStream.close();
            }

            zipInputStream.close();
            inputStreamZip.close();

            Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.backup_restored), BaseTransientBottomBar.LENGTH_SHORT).show();
            //TODO: App neustarten
        } catch (IOException e) {
            e.printStackTrace();
            Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.default_error_msg), BaseTransientBottomBar.LENGTH_SHORT).show();
        }
    }
}
