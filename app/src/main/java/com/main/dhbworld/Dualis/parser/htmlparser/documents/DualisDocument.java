package com.main.dhbworld.Dualis.parser.htmlparser.documents;

import com.main.dhbworld.Dualis.parser.htmlparser.DualisComponent;

import java.util.Date;

import javax.annotation.Nullable;

public class DualisDocument extends DualisComponent{

    private final String url;
    private final String name;
    private final Date date;


    public DualisDocument(String url, String name, Date date) {
        this.url = url;
        this.name = name;
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public Date getDate() {
        return date;
    }
}
