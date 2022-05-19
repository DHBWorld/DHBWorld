package com.main.dhbworld.Dualis;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.main.dhbworld.DualisActivity;
import com.main.dhbworld.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.concurrent.TimeUnit;

public class DualisAPI {

    String mainArguments = "";
    JSONObject mainJson = new JSONObject();

    private CourseDataLoadedListener courseListener;
    private CourseErrorListener courseErrorListener;

    private OverallDataLoadedListener overallListener;
    private OverallErrorListener overallErrorListener;

    private DocumentsLoadedListener documentsListener;
    private DocumentsErrorListener documentsErrorListener;

    public DualisAPI() {
        this.courseListener = null;
        this.courseErrorListener = null;

        this.overallListener = null;
        this.overallErrorListener = null;

        this.documentsListener = null;
        this.documentsErrorListener = null;
    }

    private void createRequest(Context context, CookieHandler cookieHandler, String url, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        CookieManager.setDefault(cookieHandler);
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, responseListener, errorListener);
        queue.add(stringRequest);
    }

    public void makeDocumentsRequest(Context context, String arguments, CookieHandler cookieHandler) {
        refactorMainArguments(arguments);

        String url = "https://dualis.dhbw.de/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=CREATEDOCUMENT&" + mainArguments;
        createRequest(context, cookieHandler, url, response -> {
            Document doc = DualisParser.parseResponse(response);
            JSONArray documentsArray = DualisParser.parseDocuments(doc);

            try {
                mainJson.put("documents", documentsArray);
            } catch (JSONException ignored) { }

            if (documentsListener != null) {
                documentsListener.onDocumentsLoaded(mainJson);
            }
        }, error -> {
            if (documentsErrorListener != null) {
                documentsErrorListener.onDocumentsError(error);
            }
        });
    }

    public void makeOverallRequest(Context context, String arguments, CookieHandler cookieHandler) {
        refactorMainArguments(arguments);

        String url = "https://dualis.dhbw.de/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=STUDENT_RESULT&" + mainArguments;
        createRequest(context, cookieHandler, url, response -> {
            Document doc = DualisParser.parseResponse(response);
            DualisParser.parseGPA(doc, mainJson);
            JSONArray coursesJsonArray = DualisParser.parseCoursesOverall(mainJson, doc);

            try {
                mainJson.put("courses", coursesJsonArray);
            } catch (JSONException ignored) { }

            if (overallListener != null) {
                overallListener.onOverallDataLoaded(mainJson);
            }
        }, error -> {
            if (overallErrorListener != null) {
                overallErrorListener.onOverallError(error);
            }
        });
    }

    public void makeClassRequest(Context context, String arguments, CookieHandler cookieHandler) {
        refactorMainArguments(arguments);

        String url = "https://dualis.dhbw.de/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=COURSERESULTS&" + mainArguments;
        createRequest(context, cookieHandler, url, response -> {
            Document doc = DualisParser.parseResponse(response);
            JSONArray semesterOptionen = DualisParser.parseClasses(doc);

            try {
                mainJson.put("semester", semesterOptionen);
                for (int i=0; i<semesterOptionen.length(); i++) {
                    requestSemester(i, context, cookieHandler);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> {
            if (courseErrorListener != null) {
                courseErrorListener.onCourseError(error);
            }
        });
    }

    private void requestSemester(int semesterIndex, Context context, CookieHandler cookieHandler) throws JSONException {
        String url = "https://dualis.dhbw.de/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=COURSERESULTS&" + mainArguments + ",-N" + mainJson.getJSONArray("semester").getJSONObject(semesterIndex).get("value");

        createRequest(context, cookieHandler, url, response -> {
            try {
                Document doc = DualisParser.parseResponse(response);
                JSONArray vorlesungen = DualisParser.parseVorlesungen(doc);

                try {
                    mainJson.getJSONArray("semester").getJSONObject(semesterIndex).put("Vorlesungen", vorlesungen);
                    boolean vorlesungFehlt = false;
                    for (int i=0; i<mainJson.getJSONArray("semester").length(); i++) {
                        JSONObject semester = mainJson.getJSONArray("semester").getJSONObject(i);
                        if (!semester.has("Vorlesungen")) {
                            vorlesungFehlt = true;
                        }
                    }
                    if (!vorlesungFehlt) {
                        requestPruefungen(context);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> {
            if (courseErrorListener != null) {
                courseErrorListener.onCourseError(error);
            }
        });
    }

    private void requestPruefungen(Context context) throws JSONException {
        final int[] count = {0};
        final int[] anzahl = {0};
        JSONArray semesterArray = mainJson.getJSONArray("semester");
        for (int i=0; i<semesterArray.length(); i++) {
            JSONObject semester = semesterArray.getJSONObject(i);
            JSONArray vorlesungen = semester.getJSONArray("Vorlesungen");
            for (int j=0; j<vorlesungen.length(); j++) {
                anzahl[0]++;
                String link = vorlesungen.getJSONObject(j).getString("link");

                RequestQueue queue = Volley.newRequestQueue(context);
                String url = "https://dualis.dhbw.de" + link;
                int finalJ = j;
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        response -> {
                            try {
                                Document doc = DualisParser.parseResponse(response);
                                JSONArray pruefungen = DualisParser.parsePruefungen(doc);

                                try {
                                    vorlesungen.getJSONObject(finalJ).put("pruefungen", pruefungen);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                count[0]++;
                                if (count[0] == anzahl[0]) {
                                    if (courseListener != null) {
                                        courseListener.onCourseDataLoaded(mainJson);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }, error -> {
                    if (courseErrorListener != null) {
                        courseErrorListener.onCourseError(error);
                    }
                });
                queue.add(stringRequest);

            }
        }
    }

    static void copareAndSave(Context context, JSONObject mainJson) {
        String fileContent = DualisParser.getSavedFileContent(context);

        if (fileContent != null) {
            JSONObject savedJson = DualisParser.parseSavedFile(fileContent);
            DualisParser.parseMainJsonForCompareison(mainJson);

            if (savedJson.toString().equals(mainJson.toString())) {
                Log.d("DualisAPI", "No new grades");
            } else {
                try {
                    for (int i=0; i<mainJson.getJSONArray("semester").length(); i++) {
                        for (int j=0; j<mainJson.getJSONArray("semester").getJSONObject(i).getJSONArray("Vorlesungen").length(); j++) {
                            JSONObject vorlesung = mainJson.getJSONArray("semester").getJSONObject(i).getJSONArray("Vorlesungen").getJSONObject(j);
                            String endnoteCurrent = vorlesung.getString("note");
                            String endnoteSaved = savedJson.getJSONArray("semester").getJSONObject(i).getJSONArray("Vorlesungen").getJSONObject(j).getString("note");

                            JSONArray pruefungen = vorlesung.getJSONArray("pruefungen");

                            for (int k=0; k<pruefungen.length(); k++) {
                                String noteCurrent = pruefungen.getJSONObject(k).getString("note");
                                String noteSaved = savedJson.getJSONArray("semester").getJSONObject(i).getJSONArray("Vorlesungen").getJSONObject(j).getJSONArray("pruefungen").getJSONObject(k).getString("note");
                                if (!noteCurrent.equals(noteSaved)) {
                                    sendNotification(context,
                                            context.getResources().getString(R.string.new_grade_exam),
                                            context.getResources().getString(R.string.new_grade_exam_text, vorlesung.getString("name"), noteCurrent),
                                            Integer.parseInt("" + j + k));
                                }
                            }
                            if (!endnoteCurrent.equals(endnoteSaved)) {
                                sendNotification(context,
                                        context.getResources().getString(R.string.new_grade_final),
                                        context.getResources().getString(R.string.new_grade_final_text, vorlesung.getString("name"), endnoteCurrent),
                                        j);
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            DualisParser.saveFileContent(context, mainJson);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void sendNotification(Context context, String title, String message, int id) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1234")
                .setSmallIcon(R.drawable.ic_baseline_school_24)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent notificationIntent = new Intent(context, DualisActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(intent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(id, builder.build());
    }

    public static void setAlarmManager(Context context) {
        SharedPreferences settingsPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences sharedPref = context.getSharedPreferences("Dualis", Context.MODE_PRIVATE);
        if (!sharedPref.getBoolean("saveCredentials", false) || !settingsPref.getBoolean("sync", true)) {
            return;
        }

        int time = Integer.parseInt(settingsPref.getString("sync_time", "15"));

        createAlarmManager(context, time);
    }

    public static void createAlarmManager(Context context, int time) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(BackgroundWorker.class, time, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();

        WorkManager workManager = WorkManager.getInstance(context.getApplicationContext());
        workManager.enqueueUniquePeriodicWork("DualisNotifier", ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);
    }

    public static void createNotificationChannelNewGrade(Context context) {
        String name = context.getString(R.string.channel_name_dualis);
        String description = context.getString(R.string.channel_description_dualis);
        String id = "1234";
        createNotificationChannel(context, id, name, description);
    }

    public static void createNotificationChannelGeneral(Context context) {
        String name = context.getString(R.string.channel_name_general_dualis);
        String description = context.getString(R.string.channel_description_general_dualis);
        String id = "4321";
        createNotificationChannel(context, id, name, description);
    }

    static void createNotificationChannel(Context context, String id, String name, String description) {
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void refactorMainArguments(String arguments) {
        mainArguments = arguments.replace("-N000000000000000", "");
        mainArguments = mainArguments.replace("-N000019,", "-N000307");
    }

    public void setOnCourseDataLoadedListener(CourseDataLoadedListener listener) {
        this.courseListener = listener;
    }

    public void setOnCourseErrorListener(CourseErrorListener listener) {
        this.courseErrorListener = listener;
    }

    public void setOnOverallDataLoadedListener(OverallDataLoadedListener listener) {
        this.overallListener = listener;
    }

    public void setOnOverallErrorListener(OverallErrorListener listener) {
        this.overallErrorListener = listener;
    }

    public void setDocumentsLoadedListener(DocumentsLoadedListener listener) {
        this.documentsListener = listener;
    }

    public void setDocumentsErrorListener(DocumentsErrorListener listener) {
        this.documentsErrorListener = listener;
    }

    public interface CourseDataLoadedListener {
        void onCourseDataLoaded(JSONObject data);
    }

    public interface CourseErrorListener {
        void onCourseError(VolleyError error);
    }

    public interface OverallDataLoadedListener {
        void onOverallDataLoaded(JSONObject data);
    }

    public interface OverallErrorListener {
        void onOverallError(VolleyError error);
    }

    public interface DocumentsLoadedListener {
        void onDocumentsLoaded(JSONObject jsonObject);
    }

    public interface DocumentsErrorListener {
        void onDocumentsError(VolleyError error);
    }
}