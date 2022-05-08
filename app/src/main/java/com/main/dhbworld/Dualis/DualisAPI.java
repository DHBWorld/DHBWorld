package com.main.dhbworld.Dualis;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.main.dhbworld.DualisActivity;
import com.main.dhbworld.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DualisAPI {

    String mainArguments = "";
    JSONObject mainJson = new JSONObject();

    private CourseDataLoadedListener courseListener;
    private CourseErrorListener courseErrorListener;

    private OverallDataLoadedListener overallListener;
    private OverallErrorListener overallErrorListener;

    public DualisAPI() {
        this.courseListener = null;
        this.courseErrorListener = null;

        this.overallListener = null;
        this.overallErrorListener = null;
    }

    public void makeOverallRequest(Context context, String arguments, CookieHandler cookieHandler) {
        mainArguments = arguments.replace("-N000000000000000", "");
        mainArguments = mainArguments.replace("-N000019,", "-N000307");

        CookieManager.setDefault(cookieHandler);
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://dualis.dhbw.de/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=STUDENT_RESULT&" + mainArguments;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    response = new String(response.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                    Document doc = Jsoup.parse(response);
                    Elements resultTables = doc.select(".nb.list.students_results");

                    Element resultModulesTable = resultTables.get(0);
                    Element resultSum = resultTables.get(1);

                    resultSum.select("tr").forEach(element -> {
                        Elements columns = element.select("th");
                        String name = columns.get(0).text();
                        String value = columns.get(1).text();

                        try {
                            if (name.equalsIgnoreCase("Gesamt-GPA")) {
                                mainJson.put("totalGPA", value);
                            } else if (name.equalsIgnoreCase("Hauptfach-GPA")) {
                                mainJson.put("majorCourseGPA", value);
                            }
                        } catch (JSONException ignored) { }
                    });

                    JSONArray coursesJsonArray = new JSONArray();

                    Elements resultModules = resultModulesTable.select("tr:not(.subhead,.tbsubhead)");

                    for (Element element : resultModules) {
                        Elements data = element.select("td");

                        if (data.size() < 6 && data.get(0).select(".level00").size() == 0) {
                            continue;
                        }

                        if (data.get(0).select(".level00").size() > 0) {
                            try {
                                if (data.size() == 5) {
                                    String totalSum = data.get(2).text().trim();
                                    mainJson.put("totalSum", totalSum);
                                } else if (data.size() == 1) {
                                    String totalSumNeeded = data.get(0).text().replace("Erforderliche Credits für Abschluss: ", "").trim();
                                    mainJson.put("totalSumNeeded", totalSumNeeded);
                                }
                            } catch (JSONException ignored) { }

                            continue;
                        }

                        String moduleID = data.get(0).text();

                        String moduleName;
                        if (data.get(1).childrenSize() > 0) {
                            moduleName = data.get(1).child(0).text();
                        } else {
                            moduleName = data.get(1).text();
                        }

                        String credits = data.get(3).text();
                        String grade = data.get(4).text();
                        String passedString = data.get(5).child(0).attr("title");
                        boolean passed = passedString.equalsIgnoreCase("Bestanden");

                        try {
                            JSONObject courseObject = new JSONObject();
                            courseObject.put("moduleID", moduleID);
                            courseObject.put("moduleName", moduleName);
                            courseObject.put("credits", credits);
                            courseObject.put("grade", grade);
                            courseObject.put("passed", passed);

                            coursesJsonArray.put(courseObject);
                        } catch (JSONException ignored) { }
                    }

                    try {
                        mainJson.put("courses", coursesJsonArray);
                    } catch (JSONException ignored) { }

                    overallListener.onOverallDataLoaded(mainJson);

                }, error -> {
                    if (overallErrorListener != null) {
                        overallErrorListener.onOverallError(error);
                    }
                });

        queue.add(stringRequest);
    }

    public void makeClassRequest(Context context, String arguments, CookieHandler cookieHandler) {
        mainArguments = arguments.replace("-N000000000000000", "");
        mainArguments = mainArguments.replace("-N000019,", "-N000307");

        CookieManager.setDefault(cookieHandler);

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://dualis.dhbw.de/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=COURSERESULTS&" + mainArguments;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        response = new String(response.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                        Document doc = Jsoup.parse(response);
                        Elements selectDiv = doc.select("select#semester");
                        Elements options = selectDiv.select("option");
                        try {
                            JSONArray semesterOptionen = new JSONArray();
                            for (Element option : options) {
                                JSONObject jsonObject = new JSONObject();

                                jsonObject.put("name", option.text());
                                jsonObject.put("value", option.val());

                                semesterOptionen.put(jsonObject);
                            }
                            mainJson.put("semester", semesterOptionen);
                            for (int i=0; i<options.size(); i++) {
                                requestSemester(i, context);
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
        queue.add(stringRequest);
    }

    private void requestSemester(int semesterIndex, Context context) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://dualis.dhbw.de/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=COURSERESULTS&" + mainArguments + ",-N" + mainJson.getJSONArray("semester").getJSONObject(semesterIndex).get("value");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        response = new String(response.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                        Document doc = Jsoup.parse(response);
                        Elements table = doc.select("table.nb.list");
                        Elements rows = table.get(0).select("tbody tr");
                        JSONArray vorlesungen = new JSONArray();
                        for (int i=0; i<rows.size(); i++) {
                            Element row = rows.get(i);
                            Elements tabledatas = row.select("td");
                            if (tabledatas.size() > 1) {
                                try {
                                    JSONObject vorlesung = new JSONObject();
                                    vorlesung.put("nummer", tabledatas.get(0).text());
                                    vorlesung.put("name", tabledatas.get(1).text());
                                    vorlesung.put("note", tabledatas.get(2).text());
                                    vorlesung.put("credits", tabledatas.get(3).text());

                                    Element script = tabledatas.get(5).selectFirst("script");
                                    String scriptText = script.html();

                                    Pattern pattern = Pattern.compile("dl_popUp\\(\"(.+?)\",\"Resultdetails\"", Pattern.DOTALL);
                                    Matcher matcher = pattern.matcher(scriptText);
                                    matcher.find();
                                    String link = matcher.group(1);

                                    vorlesung.put("link", link);
                                    vorlesungen.put(vorlesung);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
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
        queue.add(stringRequest);
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
                                response = new String(response.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                                Document doc = Jsoup.parse(response);
                                Element table = doc.select("table").get(0);

                                JSONArray pruefungen = new JSONArray();

                                for (int k=0; k<table.select("tr").size(); k++) {
                                    try {
                                        String thema = table.select("tr").get(k).select("td").get(1).text();
                                        String note = table.select("tr").get(k).select("td").get(3).text();
                                        if (!note.isEmpty() && table.select("tr").get(k).select("td").get(1).hasClass("tbdata")) {
                                            JSONObject pruefung = new JSONObject();
                                            pruefung.put("thema", thema);
                                            pruefung.put("note", note);
                                            pruefungen.put(pruefung);
                                        }
                                    } catch (IndexOutOfBoundsException ignored) {
                                        ignored.printStackTrace();
                                    }

                                }
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
        File file = new File(context.getFilesDir() + "/data.json");
        String fileContent = "";
        if (file.exists()) {
            try {
                BufferedReader fin = new BufferedReader(new FileReader(context.getFilesDir() + "/data.json"));
                StringBuilder stringBuilder = new StringBuilder();
                while (fin.ready()) {
                    stringBuilder.append(fin.readLine());
                }
                fileContent = stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject savedJson = new JSONObject();
            try {
                savedJson = new JSONObject(fileContent);
                for (int i=0; i<savedJson.getJSONArray("semester").length(); i++) {
                    for (int j=0; j<savedJson.getJSONArray("semester").getJSONObject(i).getJSONArray("Vorlesungen").length(); j++) {
                        savedJson.getJSONArray("semester").getJSONObject(i).getJSONArray("Vorlesungen").getJSONObject(j).remove("link");
                    }
                }

                for (int i=0; i<mainJson.getJSONArray("semester").length(); i++) {
                    for (int j=0; j<mainJson.getJSONArray("semester").getJSONObject(i).getJSONArray("Vorlesungen").length(); j++) {
                        mainJson.getJSONArray("semester").getJSONObject(i).getJSONArray("Vorlesungen").getJSONObject(j).remove("link");
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println(mainJson);
            System.out.println(savedJson.toString());
            if (savedJson.toString().equals(mainJson.toString())) {
                Log.d("DualisAPI", "No new grades");
                System.out.println(123123);
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
                                    System.out.println("noti");
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1234")
                                            .setSmallIcon(R.drawable.ic_baseline_school_24)
                                            .setContentTitle(context.getResources().getString(R.string.new_grade_exam))
                                            .setContentText(context.getResources().getString(R.string.new_grade_exam_text, vorlesung.getString("name"), noteCurrent))
                                            .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getResources().getString(R.string.new_grade_exam_text, vorlesung.getString("name"), noteCurrent)))
                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                    Intent notificationIntent = new Intent(context, DualisActivity.class);
                                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    PendingIntent intent = PendingIntent.getActivity(context, 0,
                                            notificationIntent, PendingIntent.FLAG_IMMUTABLE);
                                    builder.setContentIntent(intent);

                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                                    notificationManager.notify(Integer.parseInt(j + String.valueOf(k)), builder.build());
                                }
                            }
                            if (!endnoteCurrent.equals(endnoteSaved)) {
                                System.out.println("endnoti");
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1234")
                                        .setSmallIcon(R.drawable.ic_baseline_school_24)
                                        .setContentTitle(context.getResources().getString(R.string.new_grade_final))
                                        .setContentText(context.getResources().getString(R.string.new_grade_final_text, vorlesung.getString("name"), endnoteCurrent))
                                        .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getResources().getString(R.string.new_grade_final_text, vorlesung.getString("name"), endnoteCurrent)))
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                Intent notificationIntent = new Intent(context, DualisActivity.class);
                                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                PendingIntent intent = PendingIntent.getActivity(context, 0,
                                        notificationIntent, PendingIntent.FLAG_IMMUTABLE);
                                builder.setContentIntent(intent);

                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                                notificationManager.notify(j, builder.build());
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            FileOutputStream fOut = context.openFileOutput("data.json", Context.MODE_PRIVATE);
            fOut.write(mainJson.toString().getBytes());
            fOut.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void setAlarmManager(Context context) {
        SharedPreferences settingsPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences sharedPref = context.getSharedPreferences("Dualis", Context.MODE_PRIVATE);
        if (!sharedPref.getBoolean("saveCredentials", false) || !settingsPref.getBoolean("sync", true)) {
            return;
        }

        int time = Integer.parseInt(settingsPref.getString("sync_time", "15"));

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
        CharSequence name = context.getString(R.string.channel_name_dualis);
        String description = context.getString(R.string.channel_description_dualis);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("1234", name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public static void createNotificationChannelGeneral(Context context) {
        CharSequence name = context.getString(R.string.channel_name_general_dualis);
        String description = context.getString(R.string.channel_description_general_dualis);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("4321", name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
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
}