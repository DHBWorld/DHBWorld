package com.main.dhbworld.Feedback.data;

import java.util.Date;

public class Comment {
    private String author;
    private Date time;
    private String body;

    public Comment(String author, Date time, String body) {
        this.author = author;
        this.time = time;
        this.body = body;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isByMe() {
        return author.equals("DHBWorld");
    }
}
