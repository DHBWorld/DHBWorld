package com.main.dhbworld.Dualis.parser.api;

import android.content.Context;

import com.main.dhbworld.Dualis.parser.DualisParser;
import com.main.dhbworld.Dualis.parser.DualisURL;
import com.main.dhbworld.Dualis.parser.htmlparser.documents.DualisDocument;
import com.main.dhbworld.Dualis.parser.htmlparser.gpa.DualisGPA;
import com.main.dhbworld.Dualis.parser.htmlparser.overall.DualisOverallData;
import com.main.dhbworld.Dualis.parser.htmlparser.semesters.semester.DualisSemester;
import com.main.dhbworld.R;

import org.jsoup.nodes.Document;

import java.net.CookieHandler;
import java.util.ArrayList;

public class DualisAPI {

    Context context;
    String mainArguments;
    CookieHandler cookieHandler;

    public DualisAPI(Context context, String arguments, CookieHandler cookieHandler) {
        this.context = context;
        this.mainArguments = DualisURL.refactorMainArguments(arguments);
        this.cookieHandler = cookieHandler;
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