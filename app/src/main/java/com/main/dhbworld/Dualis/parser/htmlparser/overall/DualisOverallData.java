package com.main.dhbworld.Dualis.parser.htmlparser.overall;

import com.main.dhbworld.Dualis.parser.htmlparser.DualisComponent;

import java.util.ArrayList;

public class DualisOverallData extends DualisComponent {

    private String earnedCredits;
    private String neededCredits;
    private final ArrayList<DualisOverallCourse> courses;

    public DualisOverallData() {
        this.courses = new ArrayList<>();
        this.earnedCredits = "N/A";
        this.neededCredits = "N/A";
    }

    public void addCourse(DualisOverallCourse dualisOverallCourse) {
        this.courses.add(dualisOverallCourse);
    }

    public ArrayList<DualisOverallCourse> getCourses() {
        return this.courses;
    }

    public String getEarnedCredits() {
        return earnedCredits;
    }

    public void setEarnedCredits(String earnedCredits) {
        this.earnedCredits = earnedCredits;
    }

    public String getNeededCredits() {
        return neededCredits;
    }

    public void setNeededCredits(String neededCredits) {
        this.neededCredits = neededCredits;
    }
}
