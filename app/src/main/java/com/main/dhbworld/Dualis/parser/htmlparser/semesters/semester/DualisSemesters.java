package com.main.dhbworld.Dualis.parser.htmlparser.semesters.semester;

import com.main.dhbworld.Dualis.parser.htmlparser.DualisComponent;

import java.util.ArrayList;

public class DualisSemesters extends DualisComponent {
    private final ArrayList<DualisSemester> dualisSemesters;

    public DualisSemesters() {
        this.dualisSemesters = new ArrayList<>();
    }

    public void addDualisSemester(DualisSemester dualisSemester) {
        dualisSemesters.add(dualisSemester);
    }

    public ArrayList<DualisSemester> getDualisSemesters() {
        return dualisSemesters;
    }
}
