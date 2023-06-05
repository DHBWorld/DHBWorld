package com.main.dhbworld.Dualis;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;

import androidx.annotation.WorkerThread;
import androidx.preference.PreferenceManager;

import java.util.concurrent.atomic.AtomicBoolean;

public class EverlastingService extends Service {

   private static final int RESPAWN_DELAY = 1000;

   @SuppressWarnings("FieldCanBeLocal")
   private volatile HandlerThread mHandlerThread;
   private volatile Handler mHandler;
   private int mName;
   private final AtomicBoolean isDestroyed = new AtomicBoolean();

   private Thread mainBackgroundThread;

   public static boolean isRunning = false;

   public EverlastingService() {
      super();
      mName = Long.hashCode(System.currentTimeMillis());
   }

   /**
    * Where you should implement your service's logic.
    * Note that the {@link #mustStop()} flag should be checked regularly,
    * and if found <code>true</code>, this method should quickly return.
    */
   @WorkerThread
   protected void run() {

      isRunning = true;

      SharedPreferences settingsPref = PreferenceManager.getDefaultSharedPreferences(this);
      long sleepTimeMinutes = Long.parseLong(settingsPref.getString("sync_time", "15"));

      if (!settingsPref.getBoolean("sync", true)) {
         return;
      }

      try {
         for (int i=0; i<sleepTimeMinutes*6; i++) {
            Thread.sleep(10000);
         }
      } catch (InterruptedException ignored) {
         return;
      }

      new BackgroundTask(this).doWork();

      runMainTask();
   }

   /**
    * Where you should free all your resources.
    * This is called when the service is getting destroyed.
    */
   @WorkerThread
   protected void finish() {
      isRunning = false;
   }

   /**
    * Indicates if the service will be stopped by the system.
    * This should be checked regularly in the {@link #run()} method
    * and if found <code>true</code>, the method should quickly return.
    */
   @SuppressWarnings("unused")
   protected final boolean mustStop() {
      return isDestroyed.get();
   }

   /**
    * Run something on the main thread.
    *
    * @param runnable The runnable that will be called on the main thread
    */
   @SuppressWarnings("unused")
   protected final void runOnUiThread(final Runnable runnable) {
      Handler handler = new Handler(Looper.getMainLooper());
      handler.post(runnable);
   }

   protected final void runMainTask() {
      // Spawn a thread and post the main runnable
      mHandlerThread = new HandlerThread("EverlastingService[" + mName + "]");
      mHandlerThread.start();
      mHandler = new Handler(mHandlerThread.getLooper());
      mHandler.post(new Runnable() {
         @Override
         public void run() {
            EverlastingService.this.run();
         }
      });
   }

   @Override
   public void onCreate() {
      super.onCreate();
      isDestroyed.set(false);
      runMainTask();
   }

   @Override
   public void onDestroy() {
      System.out.println("destroyed");

      // Ask the main runnable to return, and launch the finish runnable
      isDestroyed.set(true);
      if (mainBackgroundThread != null && mainBackgroundThread.isAlive()) {
         mainBackgroundThread.interrupt();
      }
      mHandler.post(new Runnable() {
         @Override
         public void run() {
            finish();
         }
      });
      super.onDestroy();
   }

   @Override
   public IBinder onBind(Intent intent) {
      return null;
   }

   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
      // Restart if killed
      return START_STICKY;
   }

   @Override
   public void onTaskRemoved(Intent rootIntent) {

      System.out.println("removed");

      // Necessary for Kitkat
      Intent restartService = new Intent(getApplicationContext(), this.getClass());
      restartService.setPackage(getPackageName());
      PendingIntent restartServicePI = PendingIntent.getService(
              getApplicationContext(), 1, restartService,
              PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

      // Restart the service once it has been killed by the system
      AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
      alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + RESPAWN_DELAY, restartServicePI);

      super.onTaskRemoved(rootIntent);
   }
}