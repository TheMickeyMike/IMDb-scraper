package utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Maciej on 24.11.15.
 */

public class HtmlDownloader {

    private  String URL;
    private String title;

    public HtmlDownloader(String url) {
        this.URL = url;
    }

    public void StartParsing(){
        Document doc = null;
        try {
            doc = Jsoup.connect(URL).get();
            title = doc.title();

            for (Element table : doc.select("lister-list")) {
                for (Element row : table.select("tr")) {
                    Elements tds = row.select("td");

                        System.out.println(tds.get(0).text() + ":" + tds.get(1).text());

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }



        System.out.println(title);
    }
}
