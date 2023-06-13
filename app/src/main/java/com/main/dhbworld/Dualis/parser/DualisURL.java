package com.main.dhbworld.Dualis.parser;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.net.CookieHandler;
import java.net.CookieManager;

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
}
