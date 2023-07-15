package com.main.dhbworld.Dualis.parser.api;

import android.content.Context;
import android.util.Log;

import com.main.dhbworld.Dualis.parser.DualisParser;
import com.main.dhbworld.Dualis.parser.htmlparser.semesters.course.DualisSemesterCourse;
import com.main.dhbworld.Dualis.parser.htmlparser.semesters.exam.DualisExam;
import com.main.dhbworld.Dualis.parser.htmlparser.semesters.semester.DualisSemester;
import com.main.dhbworld.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DualisGradeComparer {

    static ArrayList<Grade> newGrades = new ArrayList<>();
    static boolean secondTry = false;

    public static boolean compareSaveNotification(Context context, ArrayList<DualisSemester> dualisSemesters, boolean secondTry) {
        DualisGradeComparer.newGrades = new ArrayList<>();
        DualisGradeComparer.secondTry = secondTry;
        ArrayList<DualisSemester> savedDualisSemesters = DualisParser.getSavedFileContent(context);
        if (savedDualisSemesters == null) {
            return true;
        }

        if (savedDualisSemesters.size() == 0) {
            return true;
        }

        if (DualisParser.semestersEqual(savedDualisSemesters, dualisSemesters)) {
            Log.d("DualisAPI", "No new grades");
            return true;
        }

        Map<String, DualisSemesterCourse> savedDualisSemesterCourseMap = createCourseMap(savedDualisSemesters);
        return checkForNewGrades(context, dualisSemesters, savedDualisSemesterCourseMap);
    }

    private static Map<String, DualisSemesterCourse> createCourseMap(ArrayList<DualisSemester> savedDualisSemesters) {
        Map<String, DualisSemesterCourse> savedDualisSemesterCourseMap = new HashMap<>();
        for (DualisSemester dualisSemester : savedDualisSemesters) {
            for (DualisSemesterCourse dualisSemesterCourse : dualisSemester.getDualisSemesterCourses()) {
                savedDualisSemesterCourseMap.put(dualisSemester.getName() + dualisSemesterCourse.getNumber(), dualisSemesterCourse);
            }
        }
        return savedDualisSemesterCourseMap;
    }

    private static boolean checkForNewGrades(Context context, ArrayList<DualisSemester> dualisSemesters, Map<String, DualisSemesterCourse> savedDualisSemesterCourseMap) {
        for (DualisSemester dualisSemester : dualisSemesters) {
            for (DualisSemesterCourse dualisSemesterCourse : dualisSemester.getDualisSemesterCourses()) {
                DualisSemesterCourse savedDualisSemesterCourse = savedDualisSemesterCourseMap.get(dualisSemester.getName() + dualisSemesterCourse.getNumber());
                if (savedDualisSemesterCourse == null) {
                    checkForNewExamGrade(context, dualisSemesterCourse);
                    checkForNewFinalGrade(context, dualisSemesterCourse);
                } else {
                    checkForNewExamGrade(context, dualisSemesterCourse, savedDualisSemesterCourse);
                    checkForNewFinalGrade(context, dualisSemesterCourse, savedDualisSemesterCourse);
                }
            }
        }
        return sendNotifications(context, dualisSemesters);
    }

    private static boolean sendNotifications(Context context, ArrayList<DualisSemester> dualisSemesters) {
        if (newGrades.size() < 4 || secondTry) {
            for (Grade grade : newGrades) {
                DualisNotification.sendNotification(context, grade.title, grade.message, grade.id);
            }
            DualisParser.saveFileContent(context, dualisSemesters);
            return true;
        } else {
            return false;
        }
    }

    private static void checkForNewFinalGrade(Context context, DualisSemesterCourse dualisSemesterCourse, DualisSemesterCourse savedDualisSemesterCourse) {
        if (!dualisSemesterCourse.getGrade().equals(savedDualisSemesterCourse.getGrade())) {
            sendNewFinalGrade(context, dualisSemesterCourse);
        }
    }

    private static void checkForNewExamGrade(Context context, DualisSemesterCourse dualisSemesterCourse, DualisSemesterCourse savedDualisSemesterCourse) {
        for (int k = 0; k < dualisSemesterCourse.getDualisExams().size(); k++) {
            DualisExam dualisExam = dualisSemesterCourse.getDualisExams().get(k);
            if (savedDualisSemesterCourse.getDualisExams().size() > k) {
                DualisExam savedDualisExam = savedDualisSemesterCourse.getDualisExams().get(k);
                String grade = dualisExam.getGrade();
                String savedGrade = savedDualisExam.getGrade();

                if (!grade.equals(savedGrade)) {
                    sendNewExamGrade(context, dualisSemesterCourse, grade);
                }
            } else {
                String grade = dualisExam.getGrade();
                sendNewExamGrade(context, dualisSemesterCourse, grade);
            }
        }
    }

    private static void checkForNewFinalGrade(Context context, DualisSemesterCourse dualisSemesterCourse) {
        sendNewFinalGrade(context, dualisSemesterCourse);
    }

    private static void checkForNewExamGrade(Context context, DualisSemesterCourse dualisSemesterCourse) {
        for (int k = 0; k < dualisSemesterCourse.getDualisExams().size(); k++) {
            DualisExam dualisExam = dualisSemesterCourse.getDualisExams().get(k);
            String grade = dualisExam.getGrade();
            sendNewExamGrade(context, dualisSemesterCourse, grade);
        }
    }

    private static void sendNewExamGrade(Context context, DualisSemesterCourse dualisSemesterCourse, String grade) {
        String gradeDot = grade.replace(",", "").trim();

        if (isNumber(gradeDot)) {
            newGrades.add(new Grade(
                    context.getResources().getString(R.string.new_grade_exam),
                    context.getResources().getString(R.string.new_grade_exam_text, dualisSemesterCourse.getName(), grade),
                    DualisNotification.calcID(dualisSemesterCourse.getName() + grade)
            ));
            //DualisNotification.sendNotification(context,
            //        context.getResources().getString(R.string.new_grade_exam),
            //        context.getResources().getString(R.string.new_grade_exam_text, dualisSemesterCourse.getName(), grade),
            //        DualisNotification.calcID(dualisSemesterCourse.getName() + grade));
        }
    }

    private static void sendNewFinalGrade(Context context, DualisSemesterCourse dualisSemesterCourse) {
        String gradeDot = dualisSemesterCourse.getGrade().replace(",", "").trim();

        if (isNumber(gradeDot)) {
            newGrades.add(new Grade(
                    context.getResources().getString(R.string.new_grade_final),
                    context.getResources().getString(R.string.new_grade_final_text, dualisSemesterCourse.getName(), dualisSemesterCourse.getGrade()),
                    DualisNotification.calcID(dualisSemesterCourse.getName() + dualisSemesterCourse.getGrade())
            ));
            //DualisNotification.sendNotification(context,
            //        context.getResources().getString(R.string.new_grade_final),
            //        context.getResources().getString(R.string.new_grade_final_text, dualisSemesterCourse.getName(), dualisSemesterCourse.getGrade()),
            //        DualisNotification.calcID(dualisSemesterCourse.getName() + dualisSemesterCourse.getGrade()));
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

    private static class Grade {
        private final String title;
        private final String message;
        private final int id;

        private Grade(String title, String message, int id) {
            this.title = title;
            this.message = message;
            this.id = id;
        }
    }
}
