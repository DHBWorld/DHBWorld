package com.main.dhbworld.Dualis;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            context.startService(new Intent(context, EverlastingService.class));
        } catch (IllegalStateException ignored) { }
    }
}