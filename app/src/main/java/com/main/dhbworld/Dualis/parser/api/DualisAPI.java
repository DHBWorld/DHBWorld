package com.main.dhbworld.Dualis.parser.api;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.main.dhbworld.Dualis.parser.DualisParser;
import com.main.dhbworld.Dualis.parser.DualisURL;
import com.main.dhbworld.Dualis.parser.htmlparser.documents.DualisDocument;
import com.main.dhbworld.Dualis.parser.htmlparser.gpa.DualisGPA;
import com.main.dhbworld.Dualis.parser.htmlparser.overall.DualisOverallData;
import com.main.dhbworld.Dualis.parser.htmlparser.semesters.semester.DualisSemester;
import com.main.dhbworld.R;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class DualisAPI {

    Context context;
    String mainArguments;
    CookieHandler cookieHandler;

    public DualisAPI(Context context, String arguments, CookieHandler cookieHandler) {
        this.context = context;
        this.mainArguments = DualisURL.refactorMainArguments(arguments);
        this.cookieHandler = cookieHandler;
    }

    public static void login(Activity activity, String email, String password, LoginListener loginListener) {
        Handler handler = new Handler(Looper.getMainLooper());
        try {
            new DualisURL.DualisLoginRequest(email, password).createRequest((responseCode, arguments, cookies) -> {
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    if (cookies.size() == 0) {
                        handler.post(loginListener::onNoCookies);
                    } else {
                        activity.runOnUiThread(() -> loginListener.onSuccess(arguments, cookies));
                    }
                } else {
                    handler.post(loginListener::onNon200);
                }
            });
        } catch (IOException e) {
            handler.post(() -> loginListener.onError(e));
        }
    }

    public void requestDocuments(DocumentsListener documentsListener) {
        String url = DualisURL.DocumentsURL.getUrl() + mainArguments;
        new DualisURL.DualisURLRequest(context, cookieHandler)
                .createRequest(url, response -> handleDocumentsReponse(response, documentsListener), documentsListener::onError);
    }

    private void handleDocumentsReponse(String response, DocumentsListener documentsListener) {
        Document doc = DualisParser.parseResponse(response);
        try {
            ArrayList<DualisDocument> documents = DualisParser.parseDocuments(doc);
            documentsListener.onDocumentsLoaded(documents);
        } catch (Exception e) {
            documentsListener.onError(new Exception(context.getResources().getString(R.string.error)));
        }
    }

    public void requestOverall(OverallDataListener overallListener) {
        String url = DualisURL.OverallURL.getUrl() + mainArguments;
        new DualisURL.DualisURLRequest(context, cookieHandler)
                .createRequest(url, response -> handleOverallResponse(overallListener, response), overallListener::onError);
    }

    private void handleOverallResponse(OverallDataListener overallListener, String response) {
        Document doc = DualisParser.parseResponse(response);

        DualisGPA dualisGPA = DualisParser.parseGPA(doc);
        DualisOverallData dualisOverallData = DualisParser.parseCoursesOverall(doc);

        overallListener.onOverallDataLoaded(dualisGPA, dualisOverallData);
    }

    public void requestSemesters(SemesterDataListener semesterDataListener) {
        DualisSemesterAPI dualisSemesterAPI = new DualisSemesterAPI(context, mainArguments, cookieHandler);
        dualisSemesterAPI.requestSemesters(semesterDataListener);
    }

    public static void compareSaveNotification(Context context, ArrayList<DualisSemester> dualisSemesters) {
        DualisGradeComparer.compareSaveNotification(context, dualisSemesters);
    }

    public interface LoginListener {
        void onSuccess(String arguments, List<HttpCookie> cookies);
        void onNoCookies();
        void onError(Exception e);
        void onNon200();
    }

    public interface SemesterDataListener {
        void onSemesterDataLoaded(ArrayList<DualisSemester> dualisSemesters);
        void onError(Exception e);
    }

    public interface OverallDataListener {
        void onOverallDataLoaded(DualisGPA dualisGPA, DualisOverallData dualisOverallData);
        void onError(Exception e);
    }

    public interface DocumentsListener {
        void onDocumentsLoaded(ArrayList<DualisDocument> dualisDocuments);
        void onError(Exception e);
    }
}