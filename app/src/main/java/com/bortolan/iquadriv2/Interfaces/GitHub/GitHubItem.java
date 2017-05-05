package com.bortolan.iquadriv2.Interfaces.GitHub;

import java.io.Serializable;

public class GitHubItem implements Serializable {
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GitHubItem) {
            return name.equals(((GitHubItem) obj).getName()) && url.equals(((GitHubItem) obj).getUrl());
        }
        return super.equals(obj);
    }
}
