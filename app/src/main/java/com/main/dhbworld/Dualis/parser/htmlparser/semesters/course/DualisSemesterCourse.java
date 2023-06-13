package com.main.dhbworld.Dualis.parser.htmlparser.semesters.course;

import com.main.dhbworld.Dualis.parser.htmlparser.DualisComponent;
import com.main.dhbworld.Dualis.parser.htmlparser.semesters.exam.DualisExam;

import java.util.ArrayList;

public class DualisSemesterCourse extends DualisComponent {

    private final String name;
    private final String number;
    private final String grade;
    private final String credits;
    private String examLink;

    private ArrayList<DualisExam> dualisExams;


    public DualisSemesterCourse(String number, String name, String grade, String credits) {
        this.name = name;
        this.number = number;
        this.grade = grade;
        this.credits = credits;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getGrade() {
        return grade;
    }

    public String getCredits() {
        return credits;
    }

    public String getExamLink() {
        return examLink;
    }

    public void setExamLink(String examLink) {
        this.examLink = examLink;
    }

    public ArrayList<DualisExam> getDualisExams() {
        return dualisExams;
    }

    public void setDualisExams(ArrayList<DualisExam> dualisExams) {
        this.dualisExams = dualisExams;
    }
}
