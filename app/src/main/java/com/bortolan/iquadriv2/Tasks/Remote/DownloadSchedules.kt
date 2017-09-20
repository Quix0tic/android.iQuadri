package com.bortolan.iquadriv2.Tasks.Remote


import android.os.AsyncTask
import com.bortolan.iquadriv2.Interfaces.GitHub.GitHubResponse
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL


class DownloadSchedules(private val execute: (GitHubResponse) -> Unit) : AsyncTask<Void, Void, GitHubResponse?>() {

    override fun doInBackground(vararg voids: Void): GitHubResponse? {
        var response: GitHubResponse? = null
        try {
            val inputStream = URL(SCHEDULES).openConnection().getInputStream()

            if (inputStream != null) {
                val r = BufferedReader(InputStreamReader(inputStream))
                val total = StringBuilder()
                var line: String?
                while (true) {
                    line = r.readLine()
                    if (line == null) break
                    total.append(line).append('\n')
                }
                response = Gson().getAdapter(GitHubResponse::class.java).fromJson(total.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return response
    }

    override fun onPostExecute(response: GitHubResponse?) {
        super.onPostExecute(response)
        if (response != null) execute.invoke(response)
    }

    companion object {
        val SCHEDULES = "https://liceo.quadri.bortolan.ml"
    }
}
