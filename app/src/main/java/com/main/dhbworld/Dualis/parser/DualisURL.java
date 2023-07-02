package com.main.dhbworld.Dualis.parser;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public enum DualisURL {

    DocumentsURL("CREATEDOCUMENT&"),
    OverallURL("STUDENT_RESULT&"),
    ClassURL("COURSERESULTS&"),
    SemesterURL("COURSERESULTS&");

    private static final String baseURL  = "https://dualis.dhbw.de/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=";
    private final String url;

    DualisURL(String url) {
        this.url = baseURL + url;
    }

    public String getUrl() {
        return url;
    }

    public static String refactorMainArguments(String arguments) {
        String mainArguments = arguments.replace("-N000000000000000", "");
        return mainArguments.replace("-N000019,", "-N000307");
    }

    public static class DualisURLRequest {

        private final Context context;
        private final CookieHandler cookieHandler;

        public DualisURLRequest(Context context, CookieHandler cookieHandler) {
            this.context = context;
            this.cookieHandler = cookieHandler;
        }

        public void createRequest(String url, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
            CookieManager.setDefault(cookieHandler);
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, responseListener, errorListener);
            queue.add(stringRequest);
        }
    }

    public static class DualisLoginRequest {

        private final String email;
        private final String password;

        public DualisLoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public void createRequest(LoginRequestResult loginRequestResult) throws IOException {
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
            HttpsURLConnection conn = getHttpsURLConnection();

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

            writer.write("usrname=" + URLEncoder.encode(email, "UTF-8") + "&pass=" + URLEncoder.encode(password, "UTF-8") + "&APPNAME=CampusNet&PRGNAME=LOGINCHECK&ARGUMENTS=clino%2Cusrname%2Cpass%2Cmenuno%2Cmenu_type%2Cbrowser%2Cplatform&clino=000000000000001&menuno=000324&menu_type=classic&browser=&platform=");
            writer.flush();
            writer.close();

            int status = conn.getResponseCode();
            String refreshHeader = conn.getHeaderField("REFRESH");
            String arguments = "";
            if (refreshHeader != null) {
                arguments = refreshHeader.split("&")[2];
            }
            loginRequestResult.onResult(status, arguments, cookieManager.getCookieStore().getCookies());
        }

        @NonNull
        private static HttpsURLConnection getHttpsURLConnection() throws IOException {
            URL url = new URL("https://dualis.dhbw.de/scripts/mgrqispi.dll");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            return conn;
        }

        public interface LoginRequestResult {
            void onResult(int responseCode, String arguments, List<HttpCookie> cookies);
        }
    }
}
