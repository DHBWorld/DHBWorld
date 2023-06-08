package com.main.dhbworld.Services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.MessagingAnalytics;
import com.google.firebase.messaging.RemoteMessage;
import com.main.dhbworld.BroadcastReceiver.UpdateEventsReceiver;
import com.main.dhbworld.DashboardActivity;
import com.main.dhbworld.Enums.InteractionState;
import com.main.dhbworld.FeedbackActivity;
import com.main.dhbworld.Firebase.Utilities;
import com.main.dhbworld.R;
import com.main.dhbworld.SettingsActivity;
import com.main.dhbworld.UserInteraction;

import java.util.Random;

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
                        if (remoteMessage.getData().get("issue") != null) {
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                            Intent intent = new Intent(getApplicationContext(), FeedbackActivity.class);
                            Intent mainActivity = new Intent(getApplicationContext(), DashboardActivity.class);
                            Intent settingsActivity = new Intent(getApplicationContext(), SettingsActivity.class);
                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                            stackBuilder.addNextIntentWithParentStack(mainActivity);
                            stackBuilder.addNextIntentWithParentStack(settingsActivity);
                            stackBuilder.addNextIntentWithParentStack(intent);
                            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                            System.out.println(remoteMessage.getData().get("issue"));

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "feedback")
                                    .setSmallIcon(R.drawable.baseline_language_24_red)
                                    .setContentTitle(remoteMessage.getNotification().getTitle())
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification().getBody()))
                                    .setContentText(remoteMessage.getNotification().getBody())
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .setContentIntent(resultPendingIntent);

                            if (notificationManager.getNotificationChannel("feedback") == null) {
                                createNotificationChannel(UserInteractionMessagingService.this, "feedback", "Information zu deinem Feedback");
                            }
                            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                                notificationManager.notify(new Random().nextInt(Integer.MAX_VALUE), builder.build());
                            }
                        } else {
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "feedback")
                                    .setSmallIcon(R.drawable.baseline_language_24_red)
                                    .setContentTitle(remoteMessage.getNotification().getTitle())
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification().getBody()))
                                    .setContentText(remoteMessage.getNotification().getBody())
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setDefaults(Notification.DEFAULT_ALL);
                            if (notificationManager.getNotificationChannel("general") == null) {
                                createNotificationChannel(UserInteractionMessagingService.this, "general", "Allgemeine Informationen");
                            }
                            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                                notificationManager.notify(new Random().nextInt(Integer.MAX_VALUE), builder.build());
                            }
                        }
                        return;
                    }
                    int problem = Integer.parseInt(remoteMessage.getData().get("problem"));
                    String title = "";

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

                    switch (category) {
                        case Utilities.CATEGORY_CAFETERIA:
                            if (!sharedPreferences.getBoolean("notifications_mensa", true)) {
                                return;
                            }
                            title = getResources().getString(R.string.canteen);
                            break;
                        case Utilities.CATEGORY_COFFEE:
                            if (!sharedPreferences.getBoolean("notifications_coffee", true)) {
                                return;
                            }
                            title = getResources().getString(R.string.coffee_machine);
                            break;
                        case Utilities.CATEGORY_PRINTER:
                            if (!sharedPreferences.getBoolean("notifications_printer", true)) {
                                return;
                            }
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
                        createNotificationChannel(UserInteractionMessagingService.this, "warnings", "Warnungen (User Interaction)");
                    }
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                        notificationManager.notify(icon, builder.build());
                    }
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

    public static void createNotificationChannel(Context context, String id, String name) {
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setDescription(context.getResources().getString(R.string.event_notifications));
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}
