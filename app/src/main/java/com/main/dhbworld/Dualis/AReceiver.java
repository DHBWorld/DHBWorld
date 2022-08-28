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
    }
}