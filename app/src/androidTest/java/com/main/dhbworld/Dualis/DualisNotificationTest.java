package com.main.dhbworld.Dualis;

import static org.junit.Assert.*;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.service.notification.StatusBarNotification;

import androidx.core.app.NotificationManagerCompat;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RunWith(AndroidJUnit4.class)
public class DualisNotificationTest {

   @Test
   public void createAlarmManagerTest() {
      Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

      DualisAPI.createAlarmManager(appContext, 15);

      WorkManager workManager = WorkManager.getInstance(appContext.getApplicationContext());
      try {
         List<WorkInfo> workInfos = workManager.getWorkInfosForUniqueWork("DualisNotifier").get();

         assertEquals(1, workInfos.size());

         WorkInfo workInfo = workInfos.get(0);
         assertEquals(WorkInfo.State.ENQUEUED, workInfo.getState());

      } catch (ExecutionException | InterruptedException e) {
         fail();
      }
   }

   @Test
   public void createNotificationChannelTest() {
      Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
      String id =  "testID";
      String name = "name";
      String description = "description";
      DualisAPI.createNotificationChannel(appContext, id, name, description);

      NotificationManager notificationManager = appContext.getSystemService(NotificationManager.class);
      NotificationChannel notificationChannel = notificationManager.getNotificationChannel(id);
      assertNotNull(notificationChannel);
      assertEquals(name, notificationChannel.getName());
      assertEquals(description, notificationChannel.getDescription());
   }

   @Test
   public void sendNotificationTest() {
      Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

      int id = 6187;
      String title = "title";
      String message = "message";

      DualisAPI.createNotificationChannel(appContext, "1234", "test", "test");
      DualisAPI.sendNotification(appContext, title, message, id);

      NotificationManager notificationManager = appContext.getSystemService(NotificationManager.class);
      StatusBarNotification[] notifications = notificationManager.getActiveNotifications();

      assertTrue(notifications.length > 0);

      for (StatusBarNotification notification : notifications) {
         if (notification.getId() == id) {
            return;
         }
      }
      fail();
   }
}
