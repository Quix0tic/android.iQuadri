package com.bortolan.iquadriv2.Utils

import android.content.SharedPreferences
import android.os.AsyncTask
import com.bortolan.iquadriv2.Interfaces.Circolare
import com.bortolan.iquadriv2.Utils.Methods.removeItemNotInCategory
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import org.jsoup.Jsoup
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.*

class DownloadRSSFeed(private val key: String, private val preferences: SharedPreferences, private val execute: (List<Circolare>) -> Unit) : AsyncTask<String, Void, List<Circolare>>() {


    override fun doInBackground(vararg strings: String): List<Circolare> {
        val result = LinkedList<Circolare>()

        if (strings[0] == CIRCOLARI) {
            var inputStream: InputStream? = null
            try {
                inputStream = URL(strings[0]).openConnection().getInputStream()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            var json: XmlToJson? = null
            if (inputStream != null) {
                json = XmlToJson.Builder(inputStream, null).build()
            }

            if (json != null) {
                try {
                    val items = json.toJson()?.getJSONObject("rss")?.getJSONObject("channel")?.getJSONArray("item") ?: return emptyList()

                    for (i in 0..items.length() - 1) {                              //PER OGNI ITEM
                        val item = items.getJSONObject(i)
                        val cat = item.getJSONArray("category")

                        if (!removeItemNotInCategory(cat, preferences)) {
                            result.add(Circolare(item.getString("title"), item.getString("description"), item.getString("link")))
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        } else {
            try {
                val doc = Jsoup.connect(strings[0]).get()
                val posts = doc.select("div[id^=post-]")

                for (post in posts) {
                    val title = post.select("div.entryblog > h2 > a")
                    result.add(Circolare(title.text(), post.select("p").text(), title.attr("href")))
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        return result
    }

    override fun onPostExecute(list: List<Circolare>) {
        super.onPostExecute(list)
        execute.invoke(list)
    }


    companion object {
        val STUDENTI = "http://studenti.liceoquadri.it/"
        val CIRCOLARI = "https://mail.liceoquadri.it/wp_circolari/wordpress/index.php/category/circolari/feed/"
    }
}
