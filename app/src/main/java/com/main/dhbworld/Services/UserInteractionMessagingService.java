package com.main.dhbworld.Services;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class UserInteractionMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification() != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(UserInteractionMessagingService.this.getApplicationContext(), "Title: " +  remoteMessage.getNotification().getTitle() + "\nMessage: " + remoteMessage.getNotification().getBody(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
