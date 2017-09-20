package com.bortolan.iquadriv2.Tasks.Remote

import android.os.AsyncTask
import android.text.Html
import android.util.Log
import com.bortolan.iquadriv2.Interfaces.Article
import org.jsoup.Jsoup
import java.io.IOException

class DownloadArticle(private val post: (Article) -> Unit) : AsyncTask<String, Void, Article>() {

    override fun doInBackground(vararg strings: String): Article? {
        try {
            val doc = Jsoup.connect(strings[0]).get()
            doc.select("img").remove()
            Log.d("HTML", "-> " + doc.select("div[id=entry]").html())
            doc.select("iframe").remove()
            doc.select("div[class^=social]").remove()
            val body = doc.select("#entry").first()
            return Article(doc.select("meta[property=og:title]").attr("content"), doc.select("meta[property=og:image]").attr("content"), Html.fromHtml(body.html()))
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    override fun onPostExecute(article: Article) {
        super.onPostExecute(article)
        post.invoke(article)
    }
}
