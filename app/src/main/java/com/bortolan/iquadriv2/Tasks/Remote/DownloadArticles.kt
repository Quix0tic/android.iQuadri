package com.bortolan.iquadriv2.Tasks.Remote

import android.os.AsyncTask
import com.bortolan.iquadriv2.Interfaces.Circolare
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class DownloadArticles(val post: (List<Circolare>) -> Unit) : AsyncTask<String, Void, List<Circolare>>() {
    val list = ArrayList<Circolare>()

    override fun doInBackground(vararg p0: String?): List<Circolare>? {
        try {


            val doc: Document = Jsoup.connect(STUDENTI).get()
            val posts = doc.select("div.post")

            posts.forEachIndexed { index, element ->
                list.add(index, Circolare(element.select("h2").text(), element.select("p").text(), element.select("a.continua").attr("href")))
            }
            return list
        } catch (err: Exception) {
            err.printStackTrace()
            return null
        }
    }

    override fun onPostExecute(result: List<Circolare>?) {
        super.onPostExecute(result)
        if (result != null) post.invoke(result)
    }

    companion object {
        val STUDENTI = "http://studenti.liceoquadri.it"
    }

}