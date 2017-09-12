package com.bortolan.iquadriv2.Interfaces;

import java.io.Serializable;

public class Circolare implements Serializable {

    private String title, content, link;

    public Circolare(String title, String content, String link) {
        this.title = title;
        this.content = content;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getLink() {
        return link;
    }
}
