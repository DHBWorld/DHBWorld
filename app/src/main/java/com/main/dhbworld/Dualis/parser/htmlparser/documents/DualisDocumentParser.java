package com.main.dhbworld.Dualis.parser.htmlparser.documents;

import com.main.dhbworld.Dualis.parser.htmlparser.DualisComponentParser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DualisDocumentParser extends DualisComponentParser {
    public DualisDocumentParser(Document doc) {
        super(doc);
    }

    public DualisDocuments parse() throws Exception {
        DualisDocuments dualisDocuments = new DualisDocuments();
        for (Element row : getTableRows()) {
            Elements documentTableData = row.select(".tbdata");
            if (documentTableData.size() == 0) {
                continue;
            }

            String downloadUrl = getDocumentDownloadUrl(documentTableData);
            if (downloadUrl != null) {
                dualisDocuments.addDualisDocument(
                        new DualisDocument(downloadUrl, getDocumentName(documentTableData), getDocumentDate(documentTableData))
                );
            }
        }
        return dualisDocuments;
    }

    private Elements getTableRows() {
        Element table = doc.select("#form1").get(0);
        return table.select("tr");
    }

    private String getDocumentName(Elements documentTableData) {
        return documentTableData.get(0).text();
    }

    private Date getDocumentDate(Elements documentTableData) {
        String date = "";
        if (documentTableData.size() >= 2) {
            date = documentTableData.get(1).text();
        }
        String time = "";
        if (documentTableData.size() >= 3) {
            time = documentTableData.get(2).text();
        }
        try {
            return new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).parse(date + " " + time);
        } catch (ParseException e) {
            return null;
        }
    }

    private String getDocumentDownloadUrl(Elements documentTableData) {
        if (documentTableData.size() >= 5 && documentTableData.get(4).select("a").size() > 0) {
            Element downloadElement = documentTableData.get(4).select("a").get(0);
            return downloadElement.attr("href");
        }
        return null;
    }
}
