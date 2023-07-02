package com.main.dhbworld.Dualis.parser.htmlparser.semesters.course;

import com.main.dhbworld.Dualis.parser.htmlparser.DualisComponent;

import java.util.ArrayList;

public class DualisSemesterCourses extends DualisComponent {
    private final ArrayList<DualisSemesterCourse> dualisSemesterCourses;

    public DualisSemesterCourses() {
        this.dualisSemesterCourses = new ArrayList<>();
    }

    public void addDualisSemesterCourse(DualisSemesterCourse dualisSemesterCourse) {
        this.dualisSemesterCourses.add(dualisSemesterCourse);
    }

    public ArrayList<DualisSemesterCourse> getDualisSemesterCourses() {
        return dualisSemesterCourses;
    }
}
