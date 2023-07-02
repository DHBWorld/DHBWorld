package com.main.dhbworld.Dualis.parser.htmlparser.semesters.semester;

import com.main.dhbworld.Dualis.parser.htmlparser.DualisComponent;
import com.main.dhbworld.Dualis.parser.htmlparser.semesters.course.DualisSemesterCourse;

import java.util.ArrayList;

public class DualisSemester extends DualisComponent {
    private final String name;
    private final String value;

    private ArrayList<DualisSemesterCourse> dualisSemesterCourses = new ArrayList<>();

    public DualisSemester(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setDualisSemesterCourses(ArrayList<DualisSemesterCourse> dualisSemesterCourses) {
        this.dualisSemesterCourses = dualisSemesterCourses;
    }

    public ArrayList<DualisSemesterCourse> getDualisSemesterCourses() {
        return dualisSemesterCourses;
    }
}
