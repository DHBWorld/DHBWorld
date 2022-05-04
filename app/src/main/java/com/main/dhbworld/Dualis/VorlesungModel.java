package com.main.dhbworld.Dualis;

import org.json.JSONArray;

public class VorlesungModel {
    private final String title;
    private final String credits;
    private final String endnote;
    private final JSONArray pruefungen;

    public VorlesungModel(String title, JSONArray pruefungen, String credits, String endnote) {
        this.title = title;
        this.pruefungen = pruefungen;
        this.credits = credits;
        this.endnote = endnote;
    }
    public String getTitle() {
        return title;
    }

    public String getCredits() {
        return credits;
    }

    public String getEndnote() {
        return endnote;
    }

    public JSONArray getPruefungen() {
        return pruefungen;
    }
}
