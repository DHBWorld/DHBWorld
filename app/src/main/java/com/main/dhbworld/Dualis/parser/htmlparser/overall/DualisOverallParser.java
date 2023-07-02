package com.main.dhbworld.Dualis.parser.htmlparser.overall;

import com.main.dhbworld.Dualis.parser.htmlparser.DualisComponentParser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DualisOverallParser extends DualisComponentParser {

    public DualisOverallParser(Document doc) {
        super(doc);
    }

    public DualisOverallData parse() {
        DualisOverallData dualisOverallData = new DualisOverallData();
        Elements resultTables = doc.select(".nb.list.students_results");
        if (resultTables.size() == 0) {
            return dualisOverallData;
        }

        Elements resultModules = getModules(resultTables);

        parseData(dualisOverallData, resultModules);
        return dualisOverallData;
    }

    private Elements getModules(Elements resultTables) {
        Element resultModulesTable = resultTables.get(0);
        return resultModulesTable.select("tr:not(.subhead,.tbsubhead)");
    }

    private void parseData(DualisOverallData dualisOverallData, Elements resultModules) {
        for (Element element : resultModules) {
            Elements data = element.select("td");
            if (data.size() < 6 && data.get(0).select(".level00").size() == 0) {
                continue;
            }

            if (data.get(0).select(".level00").size() > 0) {
                parseCredits(dualisOverallData, data);
                continue;
            }

            parseModules(dualisOverallData, data);
        }
    }

    private void parseCredits(DualisOverallData dualisOverallData, Elements data) {
        if (data.size() == 5) {
            String totalSum = data.get(2).text().trim();
            dualisOverallData.setEarnedCredits(totalSum);
        } else if (data.size() == 1) {
            String totalSumNeeded = data.get(0).text().replace("Erforderliche Credits für Abschluss: ", "").trim();
            dualisOverallData.setNeededCredits(totalSumNeeded);
        }
    }

    private void parseModules(DualisOverallData dualisOverallData, Elements data) {
        String moduleID = data.get(0).text();

        String moduleName;
        if (data.get(1).childrenSize() > 0) {
            moduleName = data.get(1).child(0).text();
        } else {
            moduleName = data.get(1).text();
        }

        String credits = data.get(3).text();
        String grade = data.get(4).text();
        String passedString = data.get(5).child(0).attr("title");
        boolean passed = passedString.equalsIgnoreCase("Bestanden");

        dualisOverallData.addCourse(new DualisOverallCourse(moduleID, moduleName, credits, grade, passed));
    }
}
