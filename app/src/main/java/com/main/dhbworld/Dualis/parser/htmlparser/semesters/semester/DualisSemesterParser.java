package com.main.dhbworld.Dualis.parser.htmlparser.semesters.semester;

import com.main.dhbworld.Dualis.parser.htmlparser.DualisComponentParser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DualisSemesterParser extends DualisComponentParser {

    public DualisSemesterParser(Document doc) {
        super(doc);
    }

    public DualisSemesters parse() {
        DualisSemesters dualisSemesters = new DualisSemesters();

        Elements selectDiv = doc.select("select#semester");
        Elements options = selectDiv.select("option");
        for (Element option : options) {
            dualisSemesters.addDualisSemester(new DualisSemester(option.text(), option.val()));
        }
        return dualisSemesters;
    }
}
