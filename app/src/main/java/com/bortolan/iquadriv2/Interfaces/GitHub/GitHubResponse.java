package com.bortolan.iquadriv2.Interfaces.GitHub;

import java.io.Serializable;
import java.util.List;

public class GitHubResponse implements Serializable {
    private List<GitHubItem> prof;
    private List<GitHubItem> classi;
    private List<GitHubItem> aule;

    public GitHubResponse() {
    }

    public List<GitHubItem> getProf() {
        return prof;
    }

    public void setProf(List<GitHubItem> prof) {
        this.prof = prof;
    }

    public List<GitHubItem> getClassi() {
        return classi;
    }

    public void setClassi(List<GitHubItem> classi) {
        this.classi = classi;
    }

    public List<GitHubItem> getAule() {
        return aule;
    }

    public void setAule(List<GitHubItem> aule) {
        this.aule = aule;
    }
}
