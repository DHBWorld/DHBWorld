package com.main.dhbworld.Dualis.parser.htmlparser.semesters.exam;

import com.main.dhbworld.Dualis.parser.htmlparser.DualisComponentParser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class DualisExamParser extends DualisComponentParser {

    public DualisExamParser(Document doc) {
        super(doc);
    }

    public DualisExams parse() {
        DualisExams dualisExams = new DualisExams();

        Element table = doc.select("table").get(0);
        for (int i=0; i<table.select("tr").size(); i++) {
            try {
                String thema = table.select("tr").get(i).select("td").get(1).text();
                String note = table.select("tr").get(i).select("td").get(3).text();
                if (!note.isEmpty() && table.select("tr").get(i).select("td").get(1).hasClass("tbdata")) {
                    DualisExam dualisExam = new DualisExam(thema, note);
                    dualisExams.addDualisExam(dualisExam);
                }
            } catch (IndexOutOfBoundsException ignored) { }

        }
        return dualisExams;
    }
}
