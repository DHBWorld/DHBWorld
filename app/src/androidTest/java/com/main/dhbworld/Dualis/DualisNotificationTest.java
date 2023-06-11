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

   //@Test
   //public void createNotificationChannelTest() {
   //   Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
   //   String id =  "testID";
   //   String name = "name";
   //   String description = "description";
   //   DualisNotification.createNotificationChannel(appContext, id, name, description);
//
   //   NotificationManager notificationManager = appContext.getSystemService(NotificationManager.class);
   //   NotificationChannel notificationChannel = notificationManager.getNotificationChannel(id);
   //   assertNotNull(notificationChannel);
   //   assertEquals(name, notificationChannel.getName());
   //   assertEquals(description, notificationChannel.getDescription());
   //}
}
