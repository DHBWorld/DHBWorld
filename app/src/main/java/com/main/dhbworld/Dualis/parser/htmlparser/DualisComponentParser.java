package com.main.dhbworld.Dualis.parser.htmlparser;

import org.jsoup.nodes.Document;

public abstract class DualisComponentParser {
    public Document doc;

    public DualisComponentParser(Document doc) {
        this.doc = doc;
    }

    public abstract DualisComponent parse() throws Exception;
}
