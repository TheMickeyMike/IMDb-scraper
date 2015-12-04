import utils.MovieDataDownloader;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static final String URL = "http://www.imdb.com/chart/top?ref_=nv_mv_250_6";

    public static void main(String[] args) {
        MovieDataDownloader html = new MovieDataDownloader(URL);
        html.starDownloading();
//        HtmlDownloaderMT htmlDownloaderMT = new HtmlDownloaderMT(URL);
//        htmlDownloaderMT.StartParsing();
//        Sentiment sentiment = new Sentiment();
//        sentiment.Start();
    }

}
