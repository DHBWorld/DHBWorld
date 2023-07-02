package com.main.dhbworld.Dualis.parser.api;

import android.content.Context;

import com.main.dhbworld.Dualis.parser.DualisParser;
import com.main.dhbworld.Dualis.parser.DualisURL;
import com.main.dhbworld.Dualis.parser.htmlparser.semesters.course.DualisSemesterCourse;
import com.main.dhbworld.Dualis.parser.htmlparser.semesters.exam.DualisExam;
import com.main.dhbworld.Dualis.parser.htmlparser.semesters.semester.DualisSemester;

import org.jsoup.nodes.Document;

import java.net.CookieHandler;
import java.util.ArrayList;

public class DualisSemesterAPI {

    private final Context context;
    private final String mainArguments;
    private final CookieHandler cookieHandler;

    private DualisAPI.SemesterDataListener semesterDataListener;

    private ArrayList<DualisSemester> dualisSemesters;

    private int examRequests = 0;

    public DualisSemesterAPI(Context context, String mainArguments, CookieHandler cookieHandler) {
        this.context = context;
        this.mainArguments = mainArguments;
        this.cookieHandler = cookieHandler;
    }

    public void requestSemesters(DualisAPI.SemesterDataListener semesterDataListener) {
        this.semesterDataListener = semesterDataListener;
        String url = DualisURL.ClassURL.getUrl() + mainArguments;
        new DualisURL.DualisURLRequest(context, cookieHandler)
                .createRequest(url, this::handleSemestersResponse, semesterDataListener::onError);
    }

    private void handleSemestersResponse(String response) {
        Document doc = DualisParser.parseResponse(response);
        dualisSemesters = DualisParser.parseSemesters(doc);
        for (DualisSemester dualisSemester : dualisSemesters) {
            requestSemesterCourses(dualisSemester);
        }
    }

    private void requestSemesterCourses(DualisSemester dualisSemester) {
        String url = DualisURL.SemesterURL.getUrl() + mainArguments + ",-N" + dualisSemester.getValue();
        new DualisURL.DualisURLRequest(context, cookieHandler)
                .createRequest(url, response -> handleSemesterCoursesResponse(dualisSemester, response), semesterDataListener::onError);
    }

    private void handleSemesterCoursesResponse(DualisSemester dualisSemester, String response) {
        try {
            Document doc = DualisParser.parseResponse(response);
            ArrayList<DualisSemesterCourse> dualisSemesterCourses = DualisParser.parseSemesterCourses(doc);

            dualisSemester.setDualisSemesterCourses(dualisSemesterCourses);

            for (int i = 0; i < dualisSemesterCourses.size(); i++) {
                DualisSemesterCourse dualisSemesterCourse = dualisSemesterCourses.get(i);
                if (dualisSemesterCourse.getDualisExams() == null) {
                    examRequests++;
                    requestExams(dualisSemesterCourse);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestExams(DualisSemesterCourse dualisSemesterCourse)  {
        String url = "https://dualis.dhbw.de" + dualisSemesterCourse.getExamLink();
        new DualisURL.DualisURLRequest(context, cookieHandler)
                .createRequest(url, response -> handleExamsResponse(response, dualisSemesterCourse), semesterDataListener::onError);
    }

    private void handleExamsResponse(String response, DualisSemesterCourse dualisSemesterCourse) {
        try {
            Document doc = DualisParser.parseResponse(response);
            ArrayList<DualisExam> exams = DualisParser.parseExams(doc);
            dualisSemesterCourse.setDualisExams(exams);

        } catch (Exception e) {
            e.printStackTrace();
        }

        examRequests--;

        if (examRequests <= 0) {
            semesterDataListener.onSemesterDataLoaded(dualisSemesters);
        }
    }
}
