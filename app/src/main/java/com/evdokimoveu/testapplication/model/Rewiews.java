package com.evdokimoveu.testapplication.model;


public class Rewiews {
    private String id;
    private String author;
    private String content;

    public Rewiews(String author, String content, String id) {
        this.author = author;
        this.content = content;
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
