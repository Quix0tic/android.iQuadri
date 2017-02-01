package com.bortolan.iquadriv2.Interfaces;

import android.text.Spanned;

public class Article {
    private String title, image;
    private Spanned body;

    public Article(String title, String image, Spanned body) {
        this.title = title;
        this.image = image;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public Spanned getBody() {
        return body;
    }
}
