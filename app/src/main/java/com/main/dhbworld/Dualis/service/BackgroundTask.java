package com.main.dhbworld.Dualis.service;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.main.dhbworld.Dualis.SecureStore;
import com.main.dhbworld.Dualis.parser.api.DualisAPI;
import com.main.dhbworld.Dualis.parser.htmlparser.semesters.semester.DualisSemester;
import com.main.dhbworld.DualisActivity;
import com.main.dhbworld.R;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class BackgroundTask {

    private Context context;

    private String username = "";
    private String password = "";

    public BackgroundTask(Context context) {
        this.context = context;
    }

    public void doWork() {
        SharedPreferences sharedPref = context.getSharedPreferences("Dualis", MODE_PRIVATE);

        if (!sharedPref.getBoolean("saveCredentials", false)) {
            return;
        }

        SecureStore secureStore = new SecureStore(context, sharedPref);
        Map<String, String> credentials = null;
        try {
            credentials = secureStore.loadCredentials();
            username = credentials.get("email");
            password = credentials.get("password");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (password == null || username == null) {
            return;
        }

        new Thread(() -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                java.net.CookieManager cookieManager = new java.net.CookieManager();
                CookieHandler.setDefault(cookieManager);
                CookieHandler cookieHandler = CookieHandler.getDefault();
                URL url;
                try {
                    url = new URL("https://dualis.dhbw.de/scripts/mgrqispi.dll");
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

                    writer.write("usrname=" + URLEncoder.encode(username, "UTF-8") + "&pass=" + URLEncoder.encode(password, "UTF-8") + "&APPNAME=CampusNet&PRGNAME=LOGINCHECK&ARGUMENTS=clino%2Cusrname%2Cpass%2Cmenuno%2Cmenu_type%2Cbrowser%2Cplatform&clino=000000000000001&menuno=000324&menu_type=classic&browser=&platform=");
                    writer.flush();
                    writer.close();

                    int status = conn.getResponseCode();

                    if (status == HttpURLConnection.HTTP_OK) {
                        List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
                        if (cookies.size() == 0) {
                            handler.post(() -> {
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "4321")
                                        .setSmallIcon(R.drawable.ic_baseline_school_24)
                                        .setContentTitle(context.getResources().getString(R.string.authentication_expired))
                                        .setContentText(context.getResources().getString(R.string.authentication_expired_expl))
                                        .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getResources().getString(R.string.authentication_expired_expl)))
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                Intent notificationIntent = new Intent(context, DualisActivity.class);
                                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                PendingIntent intent = PendingIntent.getActivity(context, 0,
                                        notificationIntent, PendingIntent.FLAG_IMMUTABLE);
                                builder.setContentIntent(intent);

                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                                    notificationManager.notify(55, builder.build());
                                }
                            });
                        } else {
                            String arguments = conn.getHeaderField("REFRESH");
                            arguments = arguments.split("&")[2];

                            DualisAPI dualisAPI = new DualisAPI(context, arguments, cookieHandler);
                            dualisAPI.requestSemesters(new DualisAPI.SemesterDataListener() {
                                @Override
                                public void onSemesterDataLoaded(ArrayList<DualisSemester> dualisSemesters) {
                                    try {
                                        DualisAPI.compareSaveNotification(context, dualisSemesters);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }).start();
        return;
    }
}
