package com.main.dhbworld.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.MessagingAnalytics;
import com.google.firebase.messaging.RemoteMessage;
import com.main.dhbworld.BroadcastReceiver.UpdateEventsReceiver;
import com.main.dhbworld.Enums.InteractionState;
import com.main.dhbworld.Firebase.Utilities;
import com.main.dhbworld.R;
import com.main.dhbworld.UserInteraction;

public class UserInteractionMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Context context = this;
        if (remoteMessage.getNotification() != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    String category = remoteMessage.getData().get("category");
                    if (category == null || remoteMessage.getData().get("problem") == null) {
                        return;
                    }
                    int problem = Integer.parseInt(remoteMessage.getData().get("problem"));
                    String title = "";
                    switch (category) {
                        case Utilities.CATEGORY_CAFETERIA:
                            title = getResources().getString(R.string.canteen);
                            break;
                        case Utilities.CATEGORY_COFFEE:
                            title = getResources().getString(R.string.coffee_machine);
                            break;
                        case Utilities.CATEGORY_PRINTER:
                            title = getResources().getString(R.string.Printer);
                            break;
                    }

                    String message = getString(R.string.status_changed_message, InteractionState.parseId(problem).getText(context));

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                    notificationManager.cancel(0);

                    if (problem != 0) {
                        UpdateEventsReceiver.setReceiver(getApplicationContext(), category);
                    }

                    int color = InteractionState.parseId(problem).getColor();

                    int icon = R.drawable.baseline_coffee_maker_24;
                    if (category.equals(Utilities.CATEGORY_CAFETERIA)) {
                        icon = R.drawable.ic_baseline_restaurant_24;
                    } else if (category.equals(Utilities.CATEGORY_PRINTER)) {
                        icon = R.drawable.ic_baseline_print_24;
                    }

                    Intent intent = new Intent(getApplicationContext(), UserInteraction.class);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                    stackBuilder.addNextIntentWithParentStack(intent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "warnings")
                            .setSmallIcon(icon)
                            .setContentTitle(title)
                            .setColor(getResources().getColor(color))
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                            .setContentText(message)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setContentIntent(resultPendingIntent);

                    if (notificationManager.getNotificationChannel("warnings") == null) {
                        createNotificationChannel(UserInteractionMessagingService.this);
                    }
                    notificationManager.notify(icon, builder.build());
                }
            });
        }
    }

    /**
     * some hacky override of a Firebase function to catch the notification in the background to call our own customized #onMessageReceived
     * @param intent is the intent coming from firebase
     */
    @Override
    public void handleIntent(@NonNull Intent intent) {
        System.out.println(intent.getStringExtra("message_type"));
        if (intent.getStringExtra("message_type") == null) {
            Bundle var2 = intent.getExtras();
            if (var2 == null) {
                var2 = new Bundle();
            }

            var2.remove("androidx.content.wakelockid");

            RemoteMessage remoteMessage = new RemoteMessage(var2);
            onMessageReceived(remoteMessage);
        }
        MessagingAnalytics.logNotificationReceived(intent);
    }

    public static void createNotificationChannel(Context context) {
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel("warnings", "Warnungen (User Interaction)", importance);
        channel.setDescription(context.getResources().getString(R.string.event_notifications));
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}
