package utils;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import model.Movie;
import model.Movies;
import model.Review;
import model.Sentiment;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Maciej on 24.11.15.
 */
///jvisualvm --openjmx localhost:9777
public class MovieDataDownloader {

    public static String PATH_BASE = "";

    public static final String ID_PATTERN = "title\\/(.*)\\/";
    public static final String TITLE_PATTERN = ">(.*\\s*)<i";
    public static final String TITLE_PATTERN_1 = ">(.*\\s*)<";
    public static final String TAGS_PATTERN = "<[^>]*>";
    public static final String VOTE_PATTERN = "^(10|[0-9])";
    public static final String GET_NUMBER_PATTERN = "(\\d+)";

    private static final String BASE_URL = "http://www.imdb.com/title/";
    private static final String REVIEW_ROUTE = "/reviews"; //Without spoilers ?filter=best&spoiler=hide
    private static final String ALL_REVIEWS = "?start=0;count=";

    private static final String SERIALIZE_LOCATION = "/Users/Maciej/Desktop/IMDb-scraper/data/1.ser";

    private static final int TIMEOUT = 60 * 1000;

    private String URL;

    private HashMap<String, String> linkMap = new HashMap<String, String>();

    private ArrayList<Movie> movies;
    private CountDownLatch latch;
    private ExecutorService pool;

    private ReviewSentiment reviewSentiment;

    private static int errorCount = 0;
    private static final Object errorCountLock = new Object();


    public MovieDataDownloader(String url) {
        setPath();
        System.out.println("Saving in: " + PATH_BASE);
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        availableProcessors = processingPower(availableProcessors);
        this.movies = new ArrayList<Movie>();
        this.URL = url;
        this.latch = new CountDownLatch(250);
        this.pool = Executors.newFixedThreadPool(availableProcessors);
        System.out.println("Available Processing Power: " + availableProcessors);
        this.reviewSentiment = new ReviewSentiment();
    }

    private int processingPower(int orginal) {
        if (orginal == 4)
            orginal = 17;
        return orginal;
    }

    public void starDownloading() {
//        checkDese();
//        loadFromJson();
        getTop250MoviesLink();
        downloadMovieData();
        System.err.println("Saving: " + movies.size() + " movies.");
        System.err.println("Errors :" + errorCount);
        CreateJson();
//        reviewSentiment.getReviewSentiment(movies);
//        //Serialize all movies
//        System.err.println("Saving: " + movies.size() + " movies.");
//        SerializeThis();
//        //All movies downloaded, lets create Json file
//        CreateJson();
    }


    private void loadFromJson() {
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(
                    new InputStreamReader(new FileInputStream("C:\\Users\\Maciej\\Desktop\\IMDb-scraper\\data\\file.json")));
            JsonParser jsonParser = new JsonParser();
            JsonArray userarray= jsonParser.parse(reader).getAsJsonArray();
            ArrayList<Movie> moviess = new ArrayList<>();
            for ( JsonElement aUser : userarray ) {
                Movie aTwitterUser = gson.fromJson(aUser, Movie.class);
                moviess.add(aTwitterUser);
            }
            int error = 0;
            for (Movie mov : moviess) {
                mov.getTittle();
                for (Review review : mov.getReviews()) {
                    Sentiment sentiment = review.getSentiments();
                    if (sentiment.getNegative() + sentiment.getNeutral() + sentiment.getPositive() +
                            sentiment.getVeryNegative() + sentiment.getVeryPositive() == 0) {
                        error++;
                        System.err.println(mov.getTittle() + " " + review.getTittle());
                    }

                }
            }
            System.out.println("IM OUT");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void checkDese() {
        ArrayList<Movie> movieee = DeserializeThis("1");
        System.err.println(movieee.size());
        int all = 0;
        for (Movie movie: movieee){
            for (Review review : movie.getReviews()){
                Sentiment sentiment = review.getSentiments();

            }
            all++;
        }
        System.err.println(all);
    }

    public void getTop250MoviesLink() {
        Pattern pattern = Pattern.compile(ID_PATTERN);
        Document doc = null;
        try {
            doc = Jsoup.connect(URL).userAgent("Mozilla").timeout(TIMEOUT).get();
            Elements table = doc.getElementsByClass("titleColumn");
            Elements row = table.select("a[href]");
            for (Element el : row) {
                String absHref = el.attr("abs:href");
                Matcher m = pattern.matcher(absHref);
                if (m.find()) {
                    String movieId = m.group(1);
                    linkMap.put(movieId, absHref);
                } else {
                    System.out.println("ERROR: Movie ID NOT FOUND in url!\n" + absHref);
                }
                System.out.println(absHref);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadMovieData() {
        for (Map.Entry<String, String> entry : linkMap.entrySet()) {
            String movieId = entry.getKey();
            String movieUrl = entry.getValue();
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    GetMovieReview(movieId);
                    System.err.println(latch.getCount());
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException E) {
            E.printStackTrace();
        }
        shutdownAndAwaitTermination(pool);
    }



    private void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    private void GetMovieReview(String id) {
        ArrayList<Review> reviews = new ArrayList<Review>();
        String url = BASE_URL + id + REVIEW_ROUTE;
        String movieTitle = null;
        Document doc = null;
        try {
            doc = Jsoup.connect(url).userAgent("Mozilla").timeout(TIMEOUT).get();
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
            Connection.Response response = Jsoup.connect(url)
                    .userAgent("Mozilla")
                    .timeout(TIMEOUT)
                    .ignoreHttpErrors(true)
                    .maxBodySize(0)
                    .execute();
            //doc = Jsoup.connect(url).userAgent("Mozilla").timeout(TIMEOUT).maxBodySize(0).get();
            int statusCode = response.statusCode();
            if (statusCode == 200) {
                doc = response.parse();
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
                    date = DateConverter(RemoveHtmlTags(date)).toString();
                    reviewTittle = RemoveHtmlTags(reviewTittle);
                    int voteInt = ConvertVote(vote);
                    String text = RemoveHtmlTags(p.get(i).toString());

                    //Add review to reviewsList
                    reviews.add(new Review(reviewTittle, date, voteInt, text)); //Na poczatku tablicy sa najlepsze recenzje (najwyzej ocenione przez uzytkownikow)
                }
            } else if (statusCode == 503) {
                synchronized (errorCountLock) {
                    errorCount++;
                }
                System.out.print("HTTP error fetching URL. Status=503");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //One review per vote 1-10
        reviews = giveMeBestForRegresiion(reviews);

        //Add movie to list
        addMovie(new Movie(movieTitle, reviews));
    }

    private synchronized void addMovie (Movie movie){
        movies.add(movie);
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
        for (int i = 1; i <= 10; i++) {
            if (!output.containsKey(i)) {
                int n = 0;
                boolean draw = true;
                Random rand = new Random();
                do {
                    //TODO USPRAWNIC SYSTEM LOSOWANIA!
                    n = rand.nextInt(inputSize) + 1;
                    Review rev  = input.get(n);
                    if (rev.getVote() != 0) {
                        if (!output.containsValue(rev)) {
                            output.put(i, input.get(n));
                            draw = false;
                            list = new ArrayList<Review>(output.values());
                        }
                    }
                } while (draw);
            }
        }
//        for (Map.Entry<Integer, Review> entry : output.entrySet()) {
//            if (entry.getValue() == null) {
//                System.err.println("NO TO KURWA MAMY PROBLEM1");
//                int n = 0;
//                boolean draw = true;
//                Random rand = new Random();
//                do {
//                    System.err.println("NO TO KURWA MAMY PROBLEM WHILE...");
//                    n = rand.nextInt(inputSize) + 1;
//                    if (!output.containsValue(input.get(n))) {
//                        draw = false;
//                        list = new ArrayList<Review>(output.values());
//                    }
//                } while (draw);
//            }
//        }
        return list;
    }


    private Sentiment GetSentimentsCoeff(String review) {
        Sentiment sentimentCoef = null;
        int veryPositive = 0;
        int positive = 0;
        int neutral = 0;
        int negative = 0;
        int veryNegative = 0;

        Properties props = new Properties();
        props.put("sentiment.model", "/Users/Maciej/Desktop/IMDb-scraper/lib/stanford-corenlp-full-2015-04-20/models/model-0009-79,68.ser.gz");
        props.put("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation annotation = pipeline.process(review);
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            switch (sentiment) {
                case "Very positive":
                    veryPositive++;
                    break;
                case "Positive":
                    positive++;
                    break;
                case "Neutral":
                    neutral++;
                    break;
                case "Negative":
                    negative++;
                    break;
                case "Very negative":
                    veryNegative++;
                    break;
                default:
                    break;
            }
        }
        sentimentCoef = new Sentiment(veryPositive, positive, neutral, negative, veryNegative);
        System.out.println(veryPositive + " " + positive + " " + neutral + " " + negative + " " + veryNegative);
        return sentimentCoef;
    }

    private synchronized ArrayList<Movie> getMovies() {
        System.err.println(movies.size());
        return movies;
    }

    private static void setPath() {
        Path currentRelativePath = Paths.get("");
        PATH_BASE = currentRelativePath.toAbsolutePath().toString() + "/";
        System.out.println(PATH_BASE);
    }

    private void CreateJson() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        String result = gson.toJson(movies);
        SaveToFile(result);
    }

    private void SaveToFile(String data) {
        try {
            //write converted json data to a file named "file.json"
            FileWriter writer = new FileWriter(PATH_BASE + Serialize.DATA_PATH +  "file.json");
            writer.write(data);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void SerializeThis() {
        Movies movieSer = new Movies();
        movieSer.setMovies(movies);
        new Serialize(movieSer);
    }

    private ArrayList<Movie> DeserializeThis(String name) {
        Serialize serialize = new Serialize(name);
        return serialize.getMovieArrayList();
    }
}
