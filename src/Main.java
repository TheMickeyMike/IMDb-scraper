import utils.HtmlDownloader;
import utils.HtmlDownloaderMT;

public class Main {

    public static final String URL = "http://www.imdb.com/chart/top?ref_=nv_mv_250_6";

    public static void main(String[] args) {
        HtmlDownloader html = new HtmlDownloader(URL);
        html.StartParsing();
//        HtmlDownloaderMT htmlDownloaderMT = new HtmlDownloaderMT(URL);
//        htmlDownloaderMT.StartParsing();
//        Sentiment sentiment = new Sentiment();
//        sentiment.Start();
    }
}
