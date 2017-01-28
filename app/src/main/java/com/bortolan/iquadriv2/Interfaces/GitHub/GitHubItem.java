package com.bortolan.iquadriv2.Interfaces.GitHub;

public class GitHubItem {
    private String name;
    private String url;

    public GitHubItem(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
