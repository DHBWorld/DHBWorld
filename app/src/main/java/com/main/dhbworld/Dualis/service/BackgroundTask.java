package com.main.dhbworld.Dualis.service;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.main.dhbworld.Dualis.SecureStore;
import com.main.dhbworld.Dualis.parser.api.DualisAPI;
import com.main.dhbworld.Dualis.parser.htmlparser.semesters.semester.DualisSemester;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

public class BackgroundTask {

    private final Context context;
    private final SharedPreferences sharedPref;

    public BackgroundTask(Context context) {
        this.context = context;
        sharedPref = context.getSharedPreferences("Dualis", MODE_PRIVATE);
    }

    public void doWork(boolean secondTry) {
        if (!sharedPref.getBoolean("saveCredentials", false)) {
            return;
        }

        Map<String, String> credentials = getCredentials();
        if (credentials == null) {
            return;
        }

        new Thread(() -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> getDualisUpdates(credentials.get("email"), credentials.get("password"), secondTry));
        }).start();
    }

    private Map<String, String> getCredentials() {
        SecureStore secureStore = new SecureStore(context, sharedPref);
        Map<String, String> credentials;
        try {
            credentials = secureStore.loadCredentials();
            if (credentials.get("email") != null && credentials.get("password") != null) {
                return credentials;
            }
        } catch (Exception ignored) { }
        return null;
    }

    private void getDualisUpdates(String username, String password, boolean secondTry) {
        java.net.CookieManager cookieManager = new java.net.CookieManager();
        CookieHandler cookieHandler = getCookieHandler(cookieManager);
        try {
            HttpsURLConnection conn = login(username, password);
            int status = conn.getResponseCode();
            String response = getResponse(conn);

            if (status == HttpURLConnection.HTTP_OK) {
                List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
                if (cookies.size() == 0) {
                    Log.d("DUALIS", response);
                } else {
                    handleSuccessfullogin(cookieHandler, conn, secondTry);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private String getResponse(HttpsURLConnection conn) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String response = in.lines().collect(Collectors.joining());
        in.close();
        return response;
    }

    private CookieHandler getCookieHandler(CookieManager cookieManager) {
        CookieHandler.setDefault(cookieManager);
        return CookieHandler.getDefault();
    }

    @NonNull
    private HttpsURLConnection getHttpsURLConnection() throws IOException {
        URL url = new URL("https://dualis.dhbw.de/scripts/mgrqispi.dll");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
        return conn;
    }

    private void sendLoginData(String username, String password, HttpsURLConnection conn) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

        writer.write("usrname=" + URLEncoder.encode(username, "UTF-8") + "&pass=" + URLEncoder.encode(password, "UTF-8") + "&APPNAME=CampusNet&PRGNAME=LOGINCHECK&ARGUMENTS=clino%2Cusrname%2Cpass%2Cmenuno%2Cmenu_type%2Cbrowser%2Cplatform&clino=000000000000001&menuno=000324&menu_type=classic&browser=&platform=");
        writer.flush();
        writer.close();
    }

    @NonNull
    private HttpsURLConnection login(String username, String password) throws IOException {
        HttpsURLConnection conn = getHttpsURLConnection();
        sendLoginData(username, password, conn);
        return conn;
    }

    private void handleSuccessfullogin(CookieHandler cookieHandler, HttpsURLConnection conn, boolean secondTry) {
        String arguments = getArguments(conn);
        DualisAPI dualisAPI = new DualisAPI(context, arguments, cookieHandler);
        dualisAPI.requestSemesters(new DualisAPI.SemesterDataListener() {
            @Override
            public void onSemesterDataLoaded(ArrayList<DualisSemester> dualisSemesters) {
                try {
                    boolean success = DualisAPI.compareSaveNotification(context, dualisSemesters, secondTry);
                    if (!success) {
                        doWork(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private String getArguments(HttpsURLConnection conn) {
        String arguments = conn.getHeaderField("REFRESH");
        arguments = arguments.split("&")[2];
        return arguments;
    }
}
