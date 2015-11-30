package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Movie;
import model.Review;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Maciej on 24.11.15.
 */

public class HtmlDownloader {

    public static final String ID_PATTERN = "title\\/(.*)\\/";
    public static final String TITLE_PATTERN = ">(.*\\s*)<i";
    public static final String TITLE_PATTERN_1 = ">(.*\\s*)<";
    public static final String TAGS_PATTERN = "<[^>]*>";
    public static final String VOTE_PATTERN = "^(10|[0-9])";

    private static final String BASE_URL = "http://www.imdb.com/title/";
    private static final String BASE_MOBILE_URL = "http://m.imdb.com/title/";
    private static final String REVIEW_ROUTE = "/reviews"; //Without spoilers ?filter=best&spoiler=hide

    private String URL;

    private Vector<String> linkVector = new Vector<String>();
    private HashMap<String, String> linkMap = new HashMap<String, String>();

    private ArrayList<Movie> movies;


    public HtmlDownloader(String url) {
        this.movies = new ArrayList<Movie>();
        this.URL = url;
    }

    public void StartParsing() {
        Pattern pattern = Pattern.compile(ID_PATTERN);
        Document doc = null;

        try {
            doc = Jsoup.connect(URL).get();
            Elements table = doc.getElementsByClass("titleColumn");
            Elements row = table.select("a[href]");
            for (Element el : row) {
                String absHref = el.attr("abs:href");
                Matcher m = pattern.matcher(absHref);
                if (m.find()) {
                    String movieId = m.group(1);
                    linkMap.put(movieId, absHref);
                    linkVector.add(movieId);
                } else {
                    System.out.println("ERROR: Movie ID NOT FOUND in url!\n" + absHref);
                }
                System.out.println(absHref);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        DownloadMovie();
    }

    public void DownloadMovie() {
        for (Map.Entry<String,String> entry : linkMap.entrySet()) {
            String movieId = entry.getKey();
            String movieUrl = entry.getValue();
            GetMovieReview(movieId);
        }

        //All movies downloaded, lets create Json file
        CreateJson();
    }

    private void GetMovieReview(String id) {
        ArrayList<Review> reviews = new ArrayList<Review>();
        String url = BASE_URL + id + REVIEW_ROUTE;
        String movieTitle = null;
        Movie movie = null;

        Document doc = null;
        try {
            doc = Jsoup.connect(url).userAgent("Mozilla").get();
            Element titleBlock = doc.getElementById("tn15title");

            //Fetch movie title
            Elements orginalTitle = titleBlock.getElementsByClass("title-extra");
            Pattern pattern = Pattern.compile(TITLE_PATTERN);
            Matcher matcher = pattern.matcher(orginalTitle.toString());
            if (matcher.find()) {
                movieTitle = matcher.group(1);
                System.out.println(movieTitle);
            } else {
                //If movie don't have translated title this will find original title
                Elements title = titleBlock.getElementsByClass("main");
                Pattern pattern1 = Pattern.compile(TITLE_PATTERN_1);
                Matcher matcher1 = pattern1.matcher(title.toString());
                if (matcher1.find()) {
                    movieTitle = matcher1.group(1);
                    System.out.println(movieTitle);
                } else {
                    System.out.println("ERROR: No title found");
                }
            }

            //Fetch reviews
            doc.select("div.yn").remove();
            Elements div = doc.select("div#tn15content > div");
            Elements p = doc.select("div#tn15content > p");

            for (int i = 0; i < div.size(); i++) {
                Element mDiv = div.get(i);
                String reviewTittle = mDiv.select("h2").toString();
                String vote = mDiv.getElementsByTag("img").attr("alt");
                Elements small = mDiv.getElementsByTag("small");
                String date = small.last().toString();

                date = DateConverter(RemoveHtmlTags(date)).toString();
                reviewTittle = RemoveHtmlTags(reviewTittle);
                int voteInt  = ConvertVote(vote);
                String text = RemoveHtmlTags(p.get(i).toString());

                //Add review to reviewsList
                reviews.add(new Review(reviewTittle,date,voteInt,text));

                System.out.println(reviewTittle);
//                System.out.println(voteInt);
//                System.out.println(date);
//                System.out.println(text);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        //Add movie to list
        movies.add(new Movie(movieTitle,reviews));

        //Create Json
//        movie = new Movie(movieTitle,reviews);
//        Gson gson = new GsonBuilder()
//                .setPrettyPrinting()
//                .disableHtmlEscaping()
//                .create();
//
//        String result = gson.toJson(movie);
//        SaveToFile(result);
//        System.out.println(result);
    }

    private void CreateJson() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        String result = gson.toJson(movies);
        SaveToFile(result);
    }

    public String RemoveHtmlTags(String rawText) {
       String result = rawText.replaceAll(TAGS_PATTERN, "");
        return result;
    }

    public int ConvertVote(String rawText) {
        int result = 0;
        Pattern pattern = Pattern.compile(VOTE_PATTERN);
        Matcher matcher = pattern.matcher(rawText);
        if (matcher.find()) {
            result = Integer.parseInt(matcher.group(0));
            return result;
        }
        return result;
    }

    public LocalDate DateConverter(String rawDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyy", Locale.ENGLISH);
        LocalDate date = LocalDate.parse(rawDate, formatter);
        return date;
    }

    // convert from internal Java String format -> UTF-8
    public static String convertToUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }

    private void SaveToFile(String data) {
        try {
            //write converted json data to a file named "file.json"
            FileWriter writer = new FileWriter("/Users/Maciej/Desktop/IMDb-scraper/data/file.json");
            writer.write(data);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
