package com.main.dhbworld.Dualis.parser.api;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.main.dhbworld.DualisActivity;
import com.main.dhbworld.R;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoField;

public class DualisNotification {

    public static void sendNotification(Context context, String title, String message, int id) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1234")
                .setSmallIcon(R.drawable.ic_baseline_school_24)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        PendingIntent intent = createDualisIntent(context);
        builder.setContentIntent(intent);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(id, builder.build());
        }
    }

    public static int calcID(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));

            long value = 0L;
            for (byte b : hash) {
                value = (value << 8) + (b & 255);
            }

            return (int) value;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return Instant.now().get(ChronoField.SECOND_OF_DAY);
    }

    private static PendingIntent createDualisIntent(Context context) {
        Intent notificationIntent = new Intent(context, DualisActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_IMMUTABLE);
    }

    public static void createNotificationChannelNewGrade(Context context) {
        String name = context.getString(R.string.channel_name_dualis);
        String description = context.getString(R.string.channel_description_dualis);
        String id = "1234";
        createNotificationChannel(context, id, name, description);
    }

    public static void createNotificationChannelGeneral(Context context) {
        String name = context.getString(R.string.channel_name_general_dualis);
        String description = context.getString(R.string.channel_description_general_dualis);
        String id = "4321";
        createNotificationChannel(context, id, name, description);
    }

    private static void createNotificationChannel(Context context, String id, String name, String description) {
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}
