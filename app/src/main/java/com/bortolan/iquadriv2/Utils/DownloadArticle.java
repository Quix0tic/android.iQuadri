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
                if (!el.is("h1") && !el.is("img") && !el.is("hr") && !el.is("div") && !el.is("span") && el.text().length() != 0) {
                    if (el.is("p")) {
                        b += "\n" + el.text();

                        for (Element child : el.children()) {
                            if (child.is("a") && child.select("img").size() == 0) {
                                b = b.replace(child.text(), child.attr("href"));
                            }
                        }
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
                if (!el.is("h1") && !el.is("img") && !el.is("hr") && !el.is("div") && !el.is("span") && el.text().length() != 0) {
                    if (el.is("p")) {
                        c += "\n" + el.text();

                        for (Element child : el.children()) {
                            if (child.is("strong") || child.is("b")) {
                                spannable.setSpan(new StyleSpan(Typeface.BOLD), c.indexOf(child.text()), c.indexOf(child.text()) + child.text().length() + 1, 0);
                            } else if (child.is("a") && child.select("img").size() == 0) {
                                c = c.replace(child.text(), child.attr("href"));
                            }
                        }

                    } else if (el.is("ul")) {   //LISTA
                        for (Element child : el.children()) {
                            c += child.text();
                            spannable.setSpan(new BulletSpan(20), c.indexOf(child.text()), c.indexOf(child.text()) + child.text().length(), 0);
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
