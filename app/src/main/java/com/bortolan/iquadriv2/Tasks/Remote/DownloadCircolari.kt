package com.bortolan.iquadriv2.Tasks.Remote

import android.content.SharedPreferences
import android.os.AsyncTask
import android.util.Log
import com.bortolan.iquadriv2.Interfaces.Circolare
import com.bortolan.iquadriv2.Utils.Methods
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.parser.Parser

class DownloadCircolari(pref: SharedPreferences, val post: (List<Circolare>?) -> Unit) : AsyncTask<String, Void, List<Circolare>?>() {
    val list = ArrayList<Circolare>()
    val myCategories = Methods.getCategoriesSettings(pref)


    override fun doInBackground(vararg p0: String?): List<Circolare>? {
        Log.d("DownloadCircolari", "execute()")
        try {
            val doc = Jsoup.connect(CIRCOLARI).ignoreContentType(true).parser(Parser.xmlParser()).get()

            val items = doc.select("item")
            items.forEach { element: Element ->
                val itemCategories = element.select("category").text().split(' ')

                for (myCat in myCategories) {
                    if (itemCategories.contains(myCat)) {
                        list.add(Circolare(element.select("title").text(), element.select("description").text(), element.select("link").text()))
                        break
                    }
                }
            }
            return list
        } catch (err: Exception) {
            err.printStackTrace()
            return null
        }
    }

    override fun onPostExecute(result: List<Circolare>?) {
        super.onPostExecute(result)
        post.invoke(result)
    }

    companion object {
        val CIRCOLARI = "https://mail.liceoquadri.it/wp_circolari/wordpress/index.php/category/circolari/feed/"
    }
}