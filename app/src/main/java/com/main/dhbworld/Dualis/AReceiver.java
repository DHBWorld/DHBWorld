package com.main.dhbworld.Dualis;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.main.dhbworld.DualisActivity;
import com.main.dhbworld.R;

public class AReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DualisAPI.setAlarmManager(context);
        NotificationCompat.Builder builder1 = new NotificationCompat.Builder(context, "4321")
                .setSmallIcon(R.drawable.ic_baseline_school_24)
                .setContentTitle("CREATED")
                .setContentText("Test Notification FIRST RUN")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Test Notification FIRST RUN"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent notificationIntent1 = new Intent(context, DualisActivity.class);
        notificationIntent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent1 = PendingIntent.getActivity(context, 0,
                notificationIntent1, PendingIntent.FLAG_IMMUTABLE);
        builder1.setContentIntent(intent1);

        NotificationManagerCompat notificationManager1 = NotificationManagerCompat.from(context);
        notificationManager1.notify(55, builder1.build());
    }
}