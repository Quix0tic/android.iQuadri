package com.bortolan.iquadriv2.Interfaces;

public class Article {
    private String title, image;
    private CharSequence body;

    public Article(String title, String image, CharSequence body) {
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

    public CharSequence getBody() {
        return body;
    }
}
