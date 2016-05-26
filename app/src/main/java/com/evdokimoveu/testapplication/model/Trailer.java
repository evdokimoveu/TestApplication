package com.evdokimoveu.testapplication.model;


public class Trailer {
    private String key;
    private String name;
    private String id;

    public Trailer(String key, String name, String id) {
        this.key = key;
        this.name = name;
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
