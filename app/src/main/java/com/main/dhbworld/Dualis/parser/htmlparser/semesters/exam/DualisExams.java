package com.main.dhbworld.Dualis.parser.htmlparser.semesters.exam;

import com.main.dhbworld.Dualis.parser.htmlparser.DualisComponent;

import java.util.ArrayList;

public class DualisExams extends DualisComponent {

    private final ArrayList<DualisExam> dualisExams;

    public DualisExams() {
        this.dualisExams = new ArrayList<>();
    }

    public void addDualisExam(DualisExam dualisExam) {
        dualisExams.add(dualisExam);
    }

    public ArrayList<DualisExam> getDualisExams() {
        return dualisExams;
    }
}
