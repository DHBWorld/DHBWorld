package com.main.dhbworld.Dualis.parser;

import android.content.Context;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.main.dhbworld.Dualis.parser.htmlparser.documents.DualisDocument;
import com.main.dhbworld.Dualis.parser.htmlparser.documents.DualisDocumentParser;
import com.main.dhbworld.Dualis.parser.htmlparser.gpa.DualisGPA;
import com.main.dhbworld.Dualis.parser.htmlparser.gpa.DualisGPAParser;
import com.main.dhbworld.Dualis.parser.htmlparser.overall.DualisOverallData;
import com.main.dhbworld.Dualis.parser.htmlparser.overall.DualisOverallParser;
import com.main.dhbworld.Dualis.parser.htmlparser.semesters.course.DualisSemesterCourse;
import com.main.dhbworld.Dualis.parser.htmlparser.semesters.course.DualisSemesterCourseParser;
import com.main.dhbworld.Dualis.parser.htmlparser.semesters.exam.DualisExam;
import com.main.dhbworld.Dualis.parser.htmlparser.semesters.exam.DualisExamParser;
import com.main.dhbworld.Dualis.parser.htmlparser.semesters.semester.DualisSemester;
import com.main.dhbworld.Dualis.parser.htmlparser.semesters.semester.DualisSemesterParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class DualisParser {

    public static Document parseResponse(String response) {
        response = new String(response.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        return Jsoup.parse(response);
    }

    public static ArrayList<DualisDocument> parseDocuments(Document doc) throws Exception {
        return new DualisDocumentParser(doc).parse().getDualisDocuments();
    }

    public static DualisGPA parseGPA(Document doc) {
        return new DualisGPAParser(doc).parse();
    }

    public static DualisOverallData parseCoursesOverall(Document doc) {
        return new DualisOverallParser(doc).parse();
    }

    public static ArrayList<DualisSemester> parseSemesters(Document doc) {
        return new DualisSemesterParser(doc).parse().getDualisSemesters();
    }

    public static ArrayList<DualisSemesterCourse> parseSemesterCourses(Document doc) {
        return new DualisSemesterCourseParser(doc).parse().getDualisSemesterCourses();
    }

    public static ArrayList<DualisExam> parseExams(Document doc) {
        return new DualisExamParser(doc).parse().getDualisExams();
    }

    public static ArrayList<DualisSemester> getSavedFileContent(Context context) {
        File file = new File(context.getFilesDir() + "/data_v2.json");
        String fileContent = null;
        if (file.exists()) {
            try {
                BufferedReader fin = new BufferedReader(new FileReader(context.getFilesDir() + "/data_v2.json"));
                StringBuilder stringBuilder = new StringBuilder();
                while (fin.ready()) {
                    stringBuilder.append(fin.readLine());
                }
                fileContent = stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return parseSavedFile(fileContent);
    }

    public static ArrayList<DualisSemester> parseSavedFile(String fileContent) {
        Gson gson = new Gson();
        ArrayList<DualisSemester> dualisSemesters = gson.fromJson(fileContent, new TypeToken<ArrayList<DualisSemester>>(){}.getType());
        if (dualisSemesters == null) {
            return new ArrayList<>();
        }

        return dualisSemesters;
    }

    public static void saveFileContent(Context context, ArrayList<DualisSemester> dualisSemesters) {
        Gson gson = new Gson();
        try {
            FileOutputStream fOut = context.openFileOutput("data_v2.json", Context.MODE_PRIVATE);
            fOut.write(gson.toJson(dualisSemesters).getBytes());
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean semestersEqual(ArrayList<DualisSemester> savedDualisSemesters, ArrayList<DualisSemester> dualisSemesters) {
        removeExamLinks(dualisSemesters);
        removeExamLinks(savedDualisSemesters);

        Gson gson = new Gson();
        String jsonSaved = gson.toJson(savedDualisSemesters);
        String json = gson.toJson(dualisSemesters);
        return json.equals(jsonSaved);
    }

    private static void removeExamLinks(ArrayList<DualisSemester> dualisSemesters) {
        for (DualisSemester dualisSemester : dualisSemesters) {
            ArrayList<DualisSemesterCourse> dualisSemesterCourses = dualisSemester.getDualisSemesterCourses();
            for (DualisSemesterCourse dualisSemesterCourse : dualisSemesterCourses) {
                dualisSemesterCourse.setExamLink(null);
            }
        }
    }
}
