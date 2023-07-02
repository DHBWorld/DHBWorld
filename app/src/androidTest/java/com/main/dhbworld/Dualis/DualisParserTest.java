package com.main.dhbworld.Dualis;

import static org.junit.Assert.*;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.main.dhbworld.Dualis.parser.DualisParser;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DualisParserTest {

    JSONObject mainJSON;
    @Before
    public void initialize() {
        mainJSON = new JSONObject();
    }

    @Test
    public void parseResponseTest() {
        String response = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<title>Page Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Heading</h1>\n" +
                "<p>Paragraph</p>\n" +
                "</body>\n" +
                "</html>";

        Document expected = Jsoup.parse(response);
        Document doc = DualisParser.parseResponse(response);
        assertEquals(expected.toString(), doc.toString());
    }
}
