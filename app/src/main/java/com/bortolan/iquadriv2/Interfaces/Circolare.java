package com.bortolan.iquadriv2.Interfaces;

import java.io.Serializable;
import java.util.Date;

public class Circolare implements Serializable {

    private String title, description, link;

    public Circolare(String title, String description, String link) {
        this.title = title;
        this.description = description;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }
}
