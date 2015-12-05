import utils.MovieDataDownloader;

import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    public static final String URL = "http://www.imdb.com/chart/top?ref_=nv_mv_250_6";

    public static void main(String[] args) {
//        MovieDataDownloader html = new MovieDataDownloader(URL);
//        html.starDownloading();
//        HtmlDownloaderMT htmlDownloaderMT = new HtmlDownloaderMT(URL);
//        htmlDownloaderMT.StartParsing();
//        Sentiment sentiment = new Sentiment();
//        sentiment.Start();
        Menu();

    }

    private static void Menu() {
        Scanner s = new Scanner(System.in);
        System.out.println("getData (Start downloading big data.)");
        System.out.println("judgeMe (Start review vote prediction.)");
//        String option = s.next();
        String option = "judgeMe";
        if (option != null) {
            switch (option) {
                case "getData":
                    MovieDataDownloader html = new MovieDataDownloader(URL);
                    html.starDownloading();
                    break;
                case "judgeMe":
                    Regression regression = new Regression();
                    regression.start();
                    break;
                default:
                    break;
            }
        }

    }

}
