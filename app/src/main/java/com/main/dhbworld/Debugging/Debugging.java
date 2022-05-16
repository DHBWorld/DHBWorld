package com.main.dhbworld.Debugging;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.DocumentsContract;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Debugging {

    public static void startDebugging(Context context) {

        File file = new File(context.getFilesDir().getAbsolutePath() + "/debug.log");
        File file1 = new File(context.getFilesDir().getAbsolutePath() + "/debug.log.new");

        if (file.length() > 1000000) {

            try {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file1));

                int CHUNK_SIZE = 8096;
                long startPos = file.length() - 500000;
                long endPos = file.length();
                long bytesToRead = endPos - startPos;
                int b;
                byte[] buff = new byte[CHUNK_SIZE];
                bufferedInputStream.skip(startPos - 1);
                int currentChunkSize = Math.min(CHUNK_SIZE, (int) bytesToRead);
                while ((b = bufferedInputStream.read(buff, 0, currentChunkSize)) != -1) {
                    bufferedOutputStream.write(buff, 0, b);
                    bytesToRead -= b;
                    if (bytesToRead == 0) {
                        break;
                    }
                    currentChunkSize = Math.min(CHUNK_SIZE, (int) bytesToRead);
                }

                bufferedInputStream.close();
                bufferedOutputStream.close();

                file.delete();
                file1.renameTo(new File(context.getFilesDir().getAbsolutePath() + "/debug.log"));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Process process = Runtime.getRuntime().exec("logcat -f /data/data/com.main.dhbworld/files/debug.log -r 100000");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File getLog(Context context) {
        return new File(context.getFilesDir().getAbsolutePath() + "/debug.log");
    }

    public static void createFile(Activity activity, Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "DHBWorld.log");

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        activity.startActivityForResult(intent, 1);
    }

}
