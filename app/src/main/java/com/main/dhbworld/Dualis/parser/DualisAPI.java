package com.main.dhbworld.Dualis.parser;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.main.dhbworld.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import java.net.CookieHandler;
import java.net.CookieManager;

public class DualisAPI {

    JSONObject mainJson = new JSONObject();

    Context context;
    String mainArguments;
    CookieHandler cookieHandler;

    public DualisAPI(Context context, String arguments, CookieHandler cookieHandler) {
        this.context = context;
        this.mainArguments = DualisURL.refactorMainArguments(arguments);
        this.cookieHandler = cookieHandler;
    }

    private void createRequest(String url, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        CookieManager.setDefault(cookieHandler);
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, responseListener, errorListener);
        queue.add(stringRequest);
    }

    public void requestDocuments(DocumentsListener documentsListener) {
        String url = DualisURL.DocumentsURL.getUrl() + mainArguments;
        createRequest(url, response -> handleDocumentsReponse(response, documentsListener), documentsListener::onError);
    }

    private void handleDocumentsReponse(String response, DocumentsListener documentsListener) {
        Document doc = DualisParser.parseResponse(response);
        JSONArray documentsArray;
        try {
            documentsArray = DualisParser.parseDocuments(doc);
            mainJson.put("documents", documentsArray);

            documentsListener.onDocumentsLoaded(mainJson);
        } catch (Exception e) {
            documentsListener.onError(new Exception(context.getResources().getString(R.string.error)));
        }
    }

    public void requestOverall(OverallDataListener overallListener) {
        String url = DualisURL.OverallURL.getUrl() + mainArguments;
        createRequest(url, response -> handleOverallResponse(overallListener, response), overallListener::onError);
    }

    private void handleOverallResponse(OverallDataListener overallListener, String response) {
        Document doc = DualisParser.parseResponse(response);
        DualisParser.parseGPA(doc, mainJson);
        JSONArray coursesJsonArray = DualisParser.parseCoursesOverall(mainJson, doc);

        try {
            mainJson.put("courses", coursesJsonArray);
        } catch (JSONException ignored) { }

        overallListener.onOverallDataLoaded(mainJson);
    }

    public void requestClass(CourseDataListener courseDataListener) {
        String url = DualisURL.ClassURL.getUrl() + mainArguments;
        createRequest(url, response -> handleClassResponse(courseDataListener, response), courseDataListener::onError);
    }

    private void handleClassResponse(CourseDataListener courseDataListener, String response) {
        Document doc = DualisParser.parseResponse(response);
        JSONArray semesterOptionen = DualisParser.parseClasses(doc);

        try {
            mainJson.put("semester", semesterOptionen);
            for (int i = 0; i < semesterOptionen.length(); i++) {
                requestSemester(i, courseDataListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestSemester(int semesterIndex, CourseDataListener courseDataListener) throws JSONException {
        String url = DualisURL.SemesterURL.getUrl() + mainArguments + ",-N" + mainJson.getJSONArray("semester").getJSONObject(semesterIndex).get("value");

        createRequest(url, response -> handleSemesterResponse(semesterIndex, courseDataListener, response), courseDataListener::onError);
    }

    private void handleSemesterResponse(int semesterIndex, CourseDataListener courseDataListener, String response) {
        try {
            Document doc = DualisParser.parseResponse(response);
            JSONArray vorlesungen = DualisParser.parseVorlesungen(doc);

            mainJson.getJSONArray("semester").getJSONObject(semesterIndex).put("Vorlesungen", vorlesungen);
            boolean vorlesungFehlt = false;
            for (int i = 0; i < mainJson.getJSONArray("semester").length(); i++) {
                JSONObject semester = mainJson.getJSONArray("semester").getJSONObject(i);
                if (!semester.has("Vorlesungen")) {
                    vorlesungFehlt = true;
                }
            }
            if (!vorlesungFehlt) {
                requestPruefungen(courseDataListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestPruefungen(CourseDataListener courseDataListener) throws JSONException {
        final int[] count = {0};
        final int[] anzahl = {0};
        JSONArray semesterArray = mainJson.getJSONArray("semester");
        for (int i = 0; i < semesterArray.length(); i++) {
            JSONObject semester = semesterArray.getJSONObject(i);
            JSONArray vorlesungen = semester.getJSONArray("Vorlesungen");
            for (int j = 0; j < vorlesungen.length(); j++) {
                anzahl[0]++;
                String link = vorlesungen.getJSONObject(j).getString("link");
                String url = "https://dualis.dhbw.de" + link;

                int finalJ = j;

                createRequest(url, response -> {
                    handlePruefungenResponse(vorlesungen, finalJ, response);
                    count[0]++;
                    if (count[0] == anzahl[0]) {
                        courseDataListener.onCourseDataLoaded(mainJson);
                    }
                }, courseDataListener::onError);
            }
        }
    }

    private void handlePruefungenResponse(JSONArray vorlesungen, int finalJ, String response) {
        try {
            Document doc = DualisParser.parseResponse(response);
            JSONArray pruefungen = DualisParser.parsePruefungen(doc, context);

            try {
                vorlesungen.getJSONObject(finalJ).put("pruefungen", pruefungen);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void compareAndSave(Context context, JSONObject mainJson) throws Exception {
        String fileContent = DualisParser.getSavedFileContent(context);
        DualisParser.parseMainJsonForCompareison(mainJson);
        DualisParser.saveFileContent(context, mainJson);
        if (fileContent == null) {
            return;
        }

        JSONObject savedJson = DualisParser.parseSavedFile(fileContent);
        if (savedJson.toString().equals(mainJson.toString())) {
            Log.d("DualisAPI", "No new grades");
            return;
        }
        for (int i = 0; i < mainJson.getJSONArray("semester").length(); i++) {
            for (int j = 0; j < mainJson.getJSONArray("semester").getJSONObject(i).getJSONArray("Vorlesungen").length(); j++) {
                JSONObject vorlesung = mainJson.getJSONArray("semester").getJSONObject(i).getJSONArray("Vorlesungen").getJSONObject(j);
                JSONArray pruefungen = savedJson.getJSONArray("semester").getJSONObject(i).getJSONArray("Vorlesungen").getJSONObject(j).getJSONArray("pruefungen");
                String endnoteCurrent = vorlesung.getString("note");
                String endnoteSaved = savedJson.getJSONArray("semester").getJSONObject(i).getJSONArray("Vorlesungen").getJSONObject(j).getString("note");

                checkForNewExamGrade(context, vorlesung, pruefungen);
                checkForNewFinalGrade(context, vorlesung, endnoteSaved, endnoteCurrent);
            }
        }
    }

    private static void checkForNewFinalGrade(Context context, JSONObject vorlesung, String endnoteSaved, String endnoteCurrent) throws JSONException {
        if (!endnoteCurrent.equals(endnoteSaved)) {
            String endnoteCurrentDot = endnoteCurrent.replace(",", "").trim();

            if (isNumber(endnoteCurrentDot)) {
                DualisNotification.sendNotification(context,
                        context.getResources().getString(R.string.new_grade_final),
                        context.getResources().getString(R.string.new_grade_final_text, vorlesung.getString("name"), endnoteCurrent),
                        DualisNotification.calcID(vorlesung.getString("name") + endnoteCurrent));
            }
        }
    }

    private static void checkForNewExamGrade(Context context, JSONObject vorlesung, JSONArray pruefungen) throws JSONException {
        for (int k = 0; k < pruefungen.length(); k++) {
            String noteCurrent = pruefungen.getJSONObject(k).getString("note");
            String noteSaved = vorlesung.getJSONArray("pruefungen").getJSONObject(k).getString("note");
            if (!noteCurrent.equals(noteSaved)) {
                String noteCurrentDot = noteCurrent.replace(",", "").trim();

                if (isNumber(noteCurrentDot)) {
                    DualisNotification.sendNotification(context,
                            context.getResources().getString(R.string.new_grade_exam),
                            context.getResources().getString(R.string.new_grade_exam_text, vorlesung.getString("name"), noteCurrent),
                            DualisNotification.calcID(vorlesung.getString("name") + noteCurrent));
                }
            }
        }
    }

    private static boolean isNumber(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    public interface CourseDataListener {
        void onCourseDataLoaded(JSONObject data);
        void onError(Exception e);
    }

    public interface OverallDataListener {
        void onOverallDataLoaded(JSONObject data);
        void onError(Exception e);
    }

    public interface DocumentsListener {
        void onDocumentsLoaded(JSONObject jsonObject);
        void onError(Exception e);
    }
}