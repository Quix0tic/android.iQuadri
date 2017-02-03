package com.bortolan.iquadriv2.Interfaces.GitHub;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class GitHubResponse implements Serializable{
    private List<GitHubItem> prof;
    private List<GitHubItem> classi;
    private List<GitHubItem> aule;

    public GitHubResponse(JSONArray prof, JSONArray classi, JSONArray aule) throws IOException {
        this.prof = new Gson().getAdapter(new TypeToken<List<GitHubItem>>() {
        }).fromJson(prof.toString());
        this.classi = new Gson().getAdapter(new TypeToken<List<GitHubItem>>() {
        }).fromJson(classi.toString());
        this.aule = new Gson().getAdapter(new TypeToken<List<GitHubItem>>() {
        }).fromJson(aule.toString());

    }

    public List<GitHubItem> getProf() {
        return prof;
    }

    public List<GitHubItem> getClassi() {
        return classi;
    }


    public List<GitHubItem> getAule() {
        return aule;
    }
}
