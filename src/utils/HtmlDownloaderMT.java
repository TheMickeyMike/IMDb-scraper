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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Maciej on 24.11.15.
 */
///jvisualvm --openjmx localhost:9777
public class HtmlDownloaderMT {

    public static final String ID_PATTERN = "title\\/(.*)\\/";
    public static final String TITLE_PATTERN = ">(.*\\s*)<i";
    public static final String TITLE_PATTERN_1 = ">(.*\\s*)<";
    public static final String TAGS_PATTERN = "<[^>]*>";
    public static final String VOTE_PATTERN = "^(10|[0-9])";
    public static final String GET_NUMBER_PATTERN = "(\\d+)";

    private static final String BASE_URL = "http://www.imdb.com/title/";
    private static final String BASE_MOBILE_URL = "http://m.imdb.com/title/";
    private static final String REVIEW_ROUTE = "/reviews"; //Without spoilers ?filter=best&spoiler=hide
    private static final String LOVED_IT = "?filter=love";
    private static final String HATED_IT = "?filter=hate";
    private static final String ALL_REVIEWS = "?start=0;count=";

    private String URL;

    private Vector<String> linkVector = new Vector<String>();
    private HashMap<String, String> linkMap = new HashMap<String, String>();

    private ArrayList<Movie> movies;

    private ExecutorService pool;


    public HtmlDownloaderMT(String url) {
        this.movies = new ArrayList<Movie>();
        this.URL = url;
        this.pool = Executors.newFixedThreadPool(5);
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
        for (Map.Entry<String, String> entry : linkMap.entrySet()) {
            String movieId = entry.getKey();
            String movieUrl = entry.getValue();
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    GetMovieReview(movieId);
                }
            });
//            GetMovieReview(movieId);
        }

        pool.shutdown();
        try {


            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        }catch (Exception e ) {
            e.printStackTrace();
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

            //Fetch numbers of reviews
            Elements total = doc.select("div#tn15content > table");
            String revCount = GetNumber(total.get(1).select("td").get(1).toString());
            url = url + ALL_REVIEWS + revCount;
            doc = Jsoup.connect(url).userAgent("Mozilla").timeout(30 * 1000).maxBodySize(0).get();
            //System.out.println(doc);


            //Fetch ALL reviews
            doc.select("div.yn").remove();
            doc.select("p:has(b)").remove();
            Elements div = doc.select("div#tn15content > div");
            Elements p = doc.select("div#tn15content > p");
            for (int i = 0; i < div.size(); i++) {
                Element mDiv = div.get(i);
                String reviewTittle = mDiv.select("h2").toString();
                String vote = mDiv.getElementsByTag("img").attr("alt");
                Elements small = mDiv.getElementsByTag("small");
                String date = small.last().toString();
//                String date = null;

                date = DateConverter(RemoveHtmlTags(date)).toString();
                reviewTittle = RemoveHtmlTags(reviewTittle);
                int voteInt = ConvertVote(vote);
                String text = RemoveHtmlTags(p.get(i).toString());

                //Add review to reviewsList
                reviews.add(new Review(reviewTittle, date, voteInt, text)); //Na poczatku tablicy sa najlepsze recenzje (najwyzej ocenione przez uzytkownikow)

//                System.out.println(reviewTittle);

//                System.out.println(voteInt);
//                System.out.println(date);
//                System.out.println(text);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //One review per vote 1-10
        reviews = giveMeBestForRegresiion(reviews);

        //Add movie to list
        movies.add(new Movie(movieTitle, reviews));
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

    private String GetNumber(String input) {
        String output = null;
        Pattern pattern = Pattern.compile(GET_NUMBER_PATTERN);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            output = matcher.group(0);
            return output;

        }
        return output;
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

    private ArrayList<Review> giveMeBestForRegresiion(ArrayList<Review> input) {
        Map<Integer, Review> output = new HashMap<Integer, Review>();
        ArrayList<Review> list = null;
        int vote = 0;
        for (Review review : input) {
            vote = review.getVote();
            if (vote != 0) {
                if (!output.containsKey(vote)) {
                    output.put(vote, review);
                    System.out.println(review.getTittle());
                    System.out.println("Vote: " + review.getVote() + "\n");
                }
            }
            if (output.size() == 10) {
                list = new ArrayList<Review>(output.values());
                return list;
            }
        }
        //When there is no review for vote, draw one from all review
        int inputSize = input.size();
        for (Map.Entry<Integer, Review> entry : output.entrySet()) {
            if (entry.getValue() == null) {
                int n = 0;
                boolean draw = true;
                Random rand = new Random();
                do {
                    n = rand.nextInt(inputSize) + 1;
                    if (!output.containsValue(input.get(n))) {
                        draw = false;
                        list = new ArrayList<Review>(output.values());
                    }
                } while (draw);
            }
        }
        return list;
    }

    private class DownloadTask implements Runnable {
        private String movieId;

        public DownloadTask(String movieId) {
            this.movieId = movieId;
        }

        @Override
        public void run() {
            GetMovieReview(movieId);
        }
    }
}

