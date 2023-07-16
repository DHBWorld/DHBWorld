package com.main.dhbworld.Backup.FileHandler;

import android.app.Activity;
import android.content.Intent;

public class BackupFileHandler {
    public static boolean restoreFile(Intent data, Activity activity, String filePassword) {
        return BackupResotrer.restoreBackup(data, activity, filePassword);
    }

    public static void saveBackupFile(Intent data, Activity activity, boolean exportDualis, String password) {
        BackupSaver.saveBackup(data, activity, exportDualis, password);
    }
}
