package com.main.dhbworld.Dualis.parser.htmlparser.documents;

import com.main.dhbworld.Dualis.parser.htmlparser.DualisComponent;

import java.util.ArrayList;

public class DualisDocuments extends DualisComponent {

    private final ArrayList<DualisDocument> dualisDocuments;

    public DualisDocuments() {
        this.dualisDocuments = new ArrayList<>();
    }

    public void addDualisDocument(DualisDocument dualisDocument) {
        this.dualisDocuments.add(dualisDocument);
    }

    public ArrayList<DualisDocument> getDualisDocuments() {
        return dualisDocuments;
    }
}
