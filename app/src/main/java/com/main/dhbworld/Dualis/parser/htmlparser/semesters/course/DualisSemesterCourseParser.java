package com.main.dhbworld.Dualis.parser.htmlparser.semesters.course;

import com.main.dhbworld.Dualis.parser.htmlparser.DualisComponentParser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DualisSemesterCourseParser extends DualisComponentParser {

    public DualisSemesterCourseParser(Document doc) {
        super(doc);
    }

    public DualisSemesterCourses parse() {
        DualisSemesterCourses dualisSemesterCourses = new DualisSemesterCourses();

        Elements rows = getRows();
        for (int i=0; i<rows.size(); i++) {
            Elements tabledatas = getTabledatas(rows, i);
            if (tabledatas.size() == 0) {
                continue;
            }

            String examsLink = findExamLink(tabledatas);
            if (examsLink != null) {
                DualisSemesterCourse dualisSemesterCourse = new DualisSemesterCourse(
                        tabledatas.get(0).text(),
                        tabledatas.get(1).text(),
                        tabledatas.get(2).text(),
                        tabledatas.get(3).text());
                dualisSemesterCourse.setExamLink(examsLink);
                dualisSemesterCourses.addDualisSemesterCourse(dualisSemesterCourse);
            }
        }
        return dualisSemesterCourses;
    }

    private Elements getTabledatas(Elements rows, int i) {
        Element row = rows.get(i);
        return row.select("td");
    }

    private Elements getRows() {
        Elements table = doc.select("table.nb.list");
        return table.get(0).select("tbody tr");
    }

    private String findExamLink(Elements tabledatas) {
        Element script = tabledatas.get(5).selectFirst("script");
        if (script == null) {
            return null;
        }
        String scriptText = script.html();

        Pattern pattern = Pattern.compile("dl_popUp\\(\"(.+?)\",\"Resultdetails\"", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(scriptText);
        boolean found = matcher.find();
        if (found) {
            return matcher.group(1);
        }
        return null;
    }
}
