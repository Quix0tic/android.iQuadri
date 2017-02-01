package com.bortolan.iquadriv2.Interfaces;

import android.text.SpannableString;

public class Article {
    private String title, image;
    private SpannableString body;

    public Article(String title, String image, SpannableString body) {
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

    public SpannableString getBody() {
        return body;
    }
}
