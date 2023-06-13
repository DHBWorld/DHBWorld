package com.main.dhbworld.Dualis.parser.htmlparser.semesters.exam;

import com.main.dhbworld.Dualis.parser.htmlparser.DualisComponent;

public class DualisExam extends DualisComponent {

    private final String topic;
    private final String grade;

    public DualisExam(String topic, String grade) {
        this.topic = topic;

        this.grade = grade;
    }

    public String getTopic() {
        return topic;
    }

    public String getGrade() {
        return grade;
    }
}
