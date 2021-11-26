package com.main.dhbworld.BroadcastReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.google.firebase.auth.FirebaseUser;
import com.main.dhbworld.Firebase.SignedInListener;
import com.main.dhbworld.Firebase.Utilities;

public class UpdateEventsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String category = intent.getStringExtra("category");
        if (category == null) {
            return;
        }
        Utilities utilities = new Utilities(context);
        utilities.setSignedInListener(new SignedInListener() {
            @Override
            public void onSignedIn(FirebaseUser user) {
                utilities.removeFromDatabase(category);
            }

            @Override
            public void onSignInError() {

            }
        });
        utilities.signIn();
    }

    public static void setReceiver(Context context, String category) {
        Intent intent = new Intent("com.main.dhbworld");
        intent.putExtra("category", category);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        ((AlarmManager)context.getSystemService(Context.ALARM_SERVICE)).setInexactRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + (AlarmManager.INTERVAL_FIFTEEN_MINUTES), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);

        context.registerReceiver(new UpdateEventsReceiver(), new IntentFilter("com.main.dhbworld"));
    }
}
