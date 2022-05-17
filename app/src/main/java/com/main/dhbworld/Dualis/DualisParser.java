package com.main.dhbworld.Dualis;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DualisParser {

    static Document parseResponse(String response) {
        response = new String(response.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        return Jsoup.parse(response);
    }

    static JSONArray parseDocuments(Document doc) {
        Element table = doc.select("#form1").get(0);
        Elements tableRows = table.select("tr");

        JSONArray documentsArray = new JSONArray();
        for (Element row : tableRows) {
            Elements data = row.select(".tbdata");
            if (data.size() == 0) {
                continue;
            }

            String name = data.get(0).text();
            String date = data.get(1).text();

            String download = null;
            if (data.get(4).select("a").size() > 0) {
                Element downloadElement = data.get(4).select("a").get(0);
                download = downloadElement.attr("href");
            }

            if (download != null && !download.isEmpty()) {
                try {
                    JSONObject documentObject = new JSONObject();
                    documentObject.put("name", name);
                    documentObject.put("date", date);
                    documentObject.put("url", download);
                    documentsArray.put(documentObject);
                } catch (JSONException ignored) { }
            }

        }
        return documentsArray;
    }

    public static void parseGPA(Document doc, JSONObject mainJson) {
        Elements resultTables = doc.select(".nb.list.students_results");
        Element resultSum = resultTables.get(1);

        resultSum.select("tr").forEach(element -> {
            Elements columns = element.select("th");
            String name = columns.get(0).text();
            String value = columns.get(1).text();

            try {
                if (name.equalsIgnoreCase("Gesamt-GPA")) {
                    mainJson.put("totalGPA", value);
                } else if (name.equalsIgnoreCase("Hauptfach-GPA")) {
                    mainJson.put("majorCourseGPA", value);
                }
            } catch (JSONException ignored) { }
        });
    }

    public static JSONArray parseCoursesOverall(JSONObject mainJson, Document doc) {
        Elements resultTables = doc.select(".nb.list.students_results");
        Element resultModulesTable = resultTables.get(0);
        Elements resultModules = resultModulesTable.select("tr:not(.subhead,.tbsubhead)");

        JSONArray coursesJsonArray = new JSONArray();
        for (Element element : resultModules) {
            Elements data = element.select("td");

            if (data.size() < 6 && data.get(0).select(".level00").size() == 0) {
                continue;
            }

            if (data.get(0).select(".level00").size() > 0) {
                try {
                    if (data.size() == 5) {
                        String totalSum = data.get(2).text().trim();
                        mainJson.put("totalSum", totalSum);
                    } else if (data.size() == 1) {
                        String totalSumNeeded = data.get(0).text().replace("Erforderliche Credits für Abschluss: ", "").trim();
                        mainJson.put("totalSumNeeded", totalSumNeeded);
                    }
                } catch (JSONException ignored) { }

                continue;
            }

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

            try {
                JSONObject courseObject = new JSONObject();
                courseObject.put("moduleID", moduleID);
                courseObject.put("moduleName", moduleName);
                courseObject.put("credits", credits);
                courseObject.put("grade", grade);
                courseObject.put("passed", passed);

                coursesJsonArray.put(courseObject);
            } catch (JSONException ignored) { }
        }
        return coursesJsonArray;
    }

    public static JSONArray parseClasses(Document doc) {
        Elements selectDiv = doc.select("select#semester");
        Elements options = selectDiv.select("option");
        JSONArray semesterOptionen = new JSONArray();
        try {
            for (Element option : options) {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("name", option.text());
                jsonObject.put("value", option.val());

                semesterOptionen.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  semesterOptionen;
    }

    public static JSONArray parseVorlesungen(Document doc) {
        Elements table = doc.select("table.nb.list");
        Elements rows = table.get(0).select("tbody tr");
        JSONArray vorlesungen = new JSONArray();
        for (int i=0; i<rows.size(); i++) {
            Element row = rows.get(i);
            Elements tabledatas = row.select("td");
            if (tabledatas.size() > 1) {
                try {
                    JSONObject vorlesung = new JSONObject();
                    vorlesung.put("nummer", tabledatas.get(0).text());
                    vorlesung.put("name", tabledatas.get(1).text());
                    vorlesung.put("note", tabledatas.get(2).text());
                    vorlesung.put("credits", tabledatas.get(3).text());

                    Element script = tabledatas.get(5).selectFirst("script");
                    String scriptText = Objects.requireNonNull(script).html();

                    Pattern pattern = Pattern.compile("dl_popUp\\(\"(.+?)\",\"Resultdetails\"", Pattern.DOTALL);
                    Matcher matcher = pattern.matcher(scriptText);
                    boolean found = matcher.find();
                    if (found) {
                        String link = matcher.group(1);

                        vorlesung.put("link", link);
                        vorlesungen.put(vorlesung);
                    }
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
        return vorlesungen;
    }

    public static JSONArray parsePruefungen(Document doc) {
        JSONArray pruefungen = new JSONArray();

        Element table = doc.select("table").get(0);
        for (int k=0; k<table.select("tr").size(); k++) {
            try {
                String thema = table.select("tr").get(k).select("td").get(1).text();
                String note = table.select("tr").get(k).select("td").get(3).text();
                if (!note.isEmpty() && table.select("tr").get(k).select("td").get(1).hasClass("tbdata")) {
                    JSONObject pruefung = new JSONObject();
                    pruefung.put("thema", thema);
                    pruefung.put("note", note);
                    pruefungen.put(pruefung);
                }
            } catch (IndexOutOfBoundsException | JSONException ignored) { }

        }
        return pruefungen;
    }



    public static String getSavedFileContent(Context context) {
        File file = new File(context.getFilesDir() + "/data.json");
        String fileContent = null;
        if (file.exists()) {
            try {
                BufferedReader fin = new BufferedReader(new FileReader(context.getFilesDir() + "/data.json"));
                StringBuilder stringBuilder = new StringBuilder();
                while (fin.ready()) {
                    stringBuilder.append(fin.readLine());
                }
                fileContent = stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileContent;
    }

    public static JSONObject parseSavedFile(String fileContent) {
        JSONObject savedJson = new JSONObject();
        try {
            savedJson = new JSONObject(fileContent);
            for (int i=0; i<savedJson.getJSONArray("semester").length(); i++) {
                for (int j=0; j<savedJson.getJSONArray("semester").getJSONObject(i).getJSONArray("Vorlesungen").length(); j++) {
                    savedJson.getJSONArray("semester").getJSONObject(i).getJSONArray("Vorlesungen").getJSONObject(j).remove("link");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return savedJson;
    }

    public static void parseMainJsonForCompareison(JSONObject mainJson) {
        try {
            for (int i=0; i<mainJson.getJSONArray("semester").length(); i++) {
                for (int j=0; j<mainJson.getJSONArray("semester").getJSONObject(i).getJSONArray("Vorlesungen").length(); j++) {
                    mainJson.getJSONArray("semester").getJSONObject(i).getJSONArray("Vorlesungen").getJSONObject(j).remove("link");
                }
            }
        } catch (JSONException ignored) {

        }
    }

    public static void saveFileContent(Context context, JSONObject mainJson) {
        try {
            FileOutputStream fOut = context.openFileOutput("data.json", Context.MODE_PRIVATE);
            fOut.write(mainJson.toString().getBytes());
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
