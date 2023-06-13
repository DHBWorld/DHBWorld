package com.main.dhbworld.Dualis.parser.htmlparser.gpa;

import com.main.dhbworld.Dualis.parser.htmlparser.DualisComponent;

public class DualisGPA extends DualisComponent {
    private String totalGPA;
    private String majorCourseGPA;

    public DualisGPA() {
        this.totalGPA = "N/A";
        this.majorCourseGPA = "N/A";
    }

    public String getTotalGPA() {
        return totalGPA;
    }

    public String getMajorCourseGPA() {
        return majorCourseGPA;
    }

    public void setTotalGPA(String totalGPA) {
        this.totalGPA = totalGPA;
    }

    public void setMajorCourseGPA(String majorCourseGPA) {
        this.majorCourseGPA = majorCourseGPA;
    }
}
