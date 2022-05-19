package com.main.dhbworld.Dualis;

import static org.junit.Assert.*;

import android.content.Context;

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
   public void setAlarmManagerTest() {
      Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

      DualisAPI.createAlarmManager(appContext, 15);

      WorkManager workManager = WorkManager.getInstance(appContext.getApplicationContext());
      try {
         List<WorkInfo> workInfos = workManager.getWorkInfosForUniqueWork("DualisNotifier").get();


      } catch (ExecutionException | InterruptedException e) {
         fail();
      }
   }
}
