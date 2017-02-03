package com.bortolan.iquadriv2.Utils;


import android.os.AsyncTask;

import com.bortolan.iquadriv2.Interfaces.GitHub.GitHubResponse;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;


public class DownloadSchedules extends AsyncTask<Void, Void, GitHubResponse> {
    private Execute execute;

    public DownloadSchedules(Execute execute) {
        this.execute = execute;
    }

    @Override
    protected GitHubResponse doInBackground(Void... voids) {
        GitHubResponse response = null;
        try {
            InputStream inputStream = new URL("https://raw.githubusercontent.com/Quix0tic/iQuadri/master/orari.json").openConnection().getInputStream();

            if (inputStream != null) {
                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line).append('\n');
                }
                response = new Gson().getAdapter(GitHubResponse.class).fromJson(total.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    protected void onPostExecute(GitHubResponse response) {
        super.onPostExecute(response);
        execute.onPost(response);
    }

    public interface Execute {
        void onPost(GitHubResponse response);
    }
}
