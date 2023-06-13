package com.main.dhbworld.Dualis.parser.htmlparser.gpa;

import com.main.dhbworld.Dualis.parser.htmlparser.DualisComponentParser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DualisGPAParser extends DualisComponentParser {

    public DualisGPAParser(Document doc) {
        super(doc);
    }

    public DualisGPA parse() {
        Elements resultTables = doc.select(".nb.list.students_results");
        if (resultTables.size() == 0) {
            return new DualisGPA();
        }
        Element resultSum = resultTables.get(1);

        DualisGPA dualisGPA = new DualisGPA();

        resultSum.select("tr").forEach(element -> {
            Elements columns = element.select("th");
            String name = columns.get(0).text();
            String value = columns.get(1).text();

                if (name.equalsIgnoreCase("Gesamt-GPA")) {
                    dualisGPA.setTotalGPA(value);
                } else if (name.equalsIgnoreCase("Hauptfach-GPA")) {
                    dualisGPA.setMajorCourseGPA(value);
                }
        });
        return dualisGPA;
    }
}
