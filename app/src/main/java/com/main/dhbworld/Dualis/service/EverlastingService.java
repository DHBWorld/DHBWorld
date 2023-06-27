package com.main.dhbworld.Dualis.service;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.main.dhbworld.DualisActivity;
import com.main.dhbworld.R;

import java.util.concurrent.atomic.AtomicBoolean;

public class EverlastingService extends Service {

   private static final int RESPAWN_DELAY = 1000;

   private final SharedPreferences settingsPref;
   private final int name;

   private volatile HandlerThread mHandlerThread;
   private volatile Handler mHandler;

   private final AtomicBoolean isDestroyed = new AtomicBoolean();
   public static boolean isRunning = false;

   public EverlastingService() {
      super();
      name = Long.hashCode(System.currentTimeMillis());
      settingsPref = PreferenceManager.getDefaultSharedPreferences(this);
   }

   @WorkerThread
   protected void run() {
      isRunning = true;

      long sleepTimeMinutes = Long.parseLong(settingsPref.getString("sync_time", "15"));
      if (!settingsPref.getBoolean("sync", true)) {
         return;
      }

      new BackgroundTask(this).doWork();

      try {
         for (int i=0; i<sleepTimeMinutes*6; i++) {
            Thread.sleep(10000);
         }
      } catch (InterruptedException ignored) {
         return;
      }

      runMainTask();
   }

   @WorkerThread
   protected void finish() {
      isRunning = false;
   }

   protected final void runMainTask() {
      mHandlerThread = new HandlerThread("EverlastingService[" + name + "]");
      mHandlerThread.start();
      mHandler = new Handler(mHandlerThread.getLooper());
      mHandler.post(EverlastingService.this::run);
   }

   @Override
   public void onCreate() {
      super.onCreate();
      isDestroyed.set(false);
      runMainTask();
   }

   @Override
   public void onDestroy() {
      isDestroyed.set(true);
      mHandler.post(this::finish);
      super.onDestroy();
   }

   @Override
   public IBinder onBind(Intent intent) {
      return null;
   }

   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
      createNotificationChannel();

      NotificationCompat.Builder builder = getNotificationBuilder();

      PendingIntent pendingIntent = getNotificationIntent();
      builder.setContentIntent(pendingIntent);

      startForeground(65, builder.build());

      return START_STICKY;
   }

   private void createNotificationChannel() {
      NotificationChannel channel = new NotificationChannel("service", "Dualis Service", NotificationManager.IMPORTANCE_HIGH);
      channel.setDescription(getString(R.string.disable_me));
      NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
   }

   @NonNull
   private NotificationCompat.Builder getNotificationBuilder() {
      return new NotificationCompat.Builder(this, "service")
              .setSmallIcon(R.drawable.ic_baseline_school_24)
              .setContentTitle("Dualis Service")
              .setContentText(getString(R.string.info_dualis_notification))
              .setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.info_dualis_notification)))
              .setPriority(NotificationCompat.PRIORITY_DEFAULT)
              .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE);
   }

   private PendingIntent getNotificationIntent() {
      Intent settingsIntent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
              .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
              .putExtra(Settings.EXTRA_APP_PACKAGE, this.getPackageName())
              .putExtra(Settings.EXTRA_CHANNEL_ID, "service");

      Intent notificationIntent = new Intent(this, DualisActivity.class);
      notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
              | Intent.FLAG_ACTIVITY_SINGLE_TOP);
      return PendingIntent.getActivity(this, 0,
              settingsIntent, PendingIntent.FLAG_IMMUTABLE);
   }

   @Override
   public void onTaskRemoved(Intent rootIntent) {
      Intent restartService = new Intent(getApplicationContext(), this.getClass());
      restartService.setPackage(getPackageName());
      PendingIntent restartServicePI = PendingIntent.getForegroundService(
              getApplicationContext(), 1, restartService,
              PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

      AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
      alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + RESPAWN_DELAY, restartServicePI);

      super.onTaskRemoved(rootIntent);
   }
}