package com.main.dhbworld.Dualis;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.main.dhbworld.DualisActivity;

public class AReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DualisAPI.setAlarmManager(context);
    }
}