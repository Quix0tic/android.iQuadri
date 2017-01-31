package com.bortolan.iquadriv2.Utils;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.bortolan.iquadriv2.Interfaces.Circolare;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

import static com.bortolan.iquadriv2.Utils.Methods.removeItemNotInCategory;

public class DownloadRSSFeed extends AsyncTask<String, Void, List<Circolare>> {
    public final static String STUDENTI = "http://studenti.liceoquadri.it/";
    public final static String CIRCOLARI = "https://mail.liceoquadri.it/wp_circolari/wordpress/index.php/category/circolari/feed/";

    private String key;
    private SharedPreferences preferences;
    private PostExecute execute;

    public DownloadRSSFeed(String key, SharedPreferences preferences, PostExecute execute) {
        this.key = key;
        this.preferences = preferences;
        this.execute = execute;
    }


    @Override
    protected List<Circolare> doInBackground(String... strings) {
        List<Circolare> result = new LinkedList<>();

        if (strings[0].equals(CIRCOLARI)) {
            InputStream inputStream = null;
            try {
                inputStream = new URL(strings[0]).openConnection().getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            XmlToJson json = null;
            if (inputStream != null) {
                json = new XmlToJson.Builder(inputStream, null).build();
            }

            if (json != null) {
                try {
                    JSONArray items = json.toJson().getJSONObject("rss").getJSONObject("channel").getJSONArray("item");

                    for (int i = 0; i < items.length(); i++) {                              //PER OGNI ITEM
                        JSONObject item = items.getJSONObject(i);
                        JSONArray cat = item.getJSONArray("category");

                        if (!removeItemNotInCategory(cat, preferences)) {
                            result.add(new Circolare(item.getString("title"), item.getString("description"), item.getString("link")));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                Document doc = Jsoup.connect(strings[0]).get();
                Elements posts = doc.select("div[id^=post-]");

                for (Element post : posts) {
                    Elements title = post.select("div.entryblog > h2 > a");
                    result.add(new Circolare(title.text(), post.select("p").text(), title.attr("href")));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(List<Circolare> list) {
        super.onPostExecute(list);
        execute.postExecute(list);
    }

    public interface PostExecute {
        void postExecute(List<Circolare> list);
    }
}
