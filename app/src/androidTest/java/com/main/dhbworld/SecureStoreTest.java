package com.main.dhbworld;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.main.dhbworld.Dualis.SecureStore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class SecureStoreTest {

   SharedPreferences sharedPreferences;

   String email = "test@test.de";
   String password = "thisisapassword";

   Context context;

   @Before
   public void initialize() {
      context = InstrumentationRegistry.getInstrumentation().getTargetContext();
      sharedPreferences = context.getSharedPreferences("TEST_PREFERENCES", Context.MODE_PRIVATE);
      sharedPreferences.edit().clear().apply();
   }

   @Test
   public void saveReadCredentials() throws Exception {
      SecureStore secureStore = new SecureStore(context, sharedPreferences);
      secureStore.saveCredentials(email, password);

      Map<String, String> loadedCredentials = secureStore.loadCredentials();

      Map<String, String> expectedCredentials = new HashMap<>();
      expectedCredentials.put("email", email);
      expectedCredentials.put("password", password);

      assertEquals(loadedCredentials, expectedCredentials);
   }

   @Test(expected = NullPointerException.class)
   public void saveClearCredentials() throws Exception {
      SecureStore secureStore = new SecureStore(context, sharedPreferences);
      secureStore.saveCredentials(email, password);

      secureStore.clearCredentials();

      secureStore.loadCredentials();
   }
}
