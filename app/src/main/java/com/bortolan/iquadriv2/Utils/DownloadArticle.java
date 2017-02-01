package com.bortolan.iquadriv2.Utils;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.style.BulletSpan;
import android.text.style.StyleSpan;
import android.util.Log;

import com.bortolan.iquadriv2.Interfaces.Article;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class DownloadArticle extends AsyncTask<String, Void, Article> {
    private DownloadListener downloadListener;

    public DownloadArticle(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    @Override
    protected Article doInBackground(String... strings) {
        try {
            Document doc = Jsoup.connect(strings[0]).get();

            String b = "";
            Element body = doc.select("#entry").get(0);
            Log.d("DOWNLOAD", String.valueOf(body.children().size()));
            for (Element el : body.children()) {
                if (el.is("h1") || el.is("img") || el.is("hr") || el.is("div") || el.is("span") || el.text().length() == 0) {

                } else {

                    if (el.select("strong").size() > 0) {
                        b += "\n" + el.text();
                    } else if (el.is("ul")) {
                        for (Element child : el.children()) {
                            b += child.text();
                            if (!b.isEmpty()) b += "\n";
                        }
                    } else {
                        b += el.text();
                    }
                    if (!b.isEmpty()) b += "\n";

                }
            }
            SpannableString spannable = new SpannableString(b);

            String c = "";
            for (Element el : body.children()) {
                if (el.is("h1") || el.is("img") || el.is("hr") || el.is("div") || el.is("span") || el.text().length() == 0) {

                } else {
                    if (el.select("strong").size() > 0) {
                        spannable.setSpan(new StyleSpan(Typeface.BOLD), c.length(), c.length() + el.text().length() + 1, 0);
                        c += "\n" + el.text();

                    } else if (el.is("ul")) {
                        for (Element child : el.children()) {
                            spannable.setSpan(new BulletSpan(10), c.length(), c.length() + child.text().length(), 0);
                            c += child.text();
                            if (!c.isEmpty()) c += "\n";
                        }
                    } else {
                        c += el.text();
                    }
                    if (!c.isEmpty()) c += "\n";

                }
            }


            return new Article(doc.select("meta[property=og:title]").attr("content"), doc.select("meta[property=og:image]").attr("content"), spannable);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Article article) {
        super.onPostExecute(article);
        downloadListener.onPost(article);
    }

    public interface DownloadListener {
        void onPost(Article article);
    }
}
