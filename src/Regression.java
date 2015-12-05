import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import model.*;
import model.Sentiment;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import utils.ReviewSentiment;

import org.apache.commons.lang3.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Maciej on 2015-12-05.
 */
public class Regression {
    private String review;
    private SimpleRegression regression;
    private ReviewSentiment reviewSentiment;
    private ArrayList<Movie> moviesFromJSON;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public Regression() {
        this.reviewSentiment = new ReviewSentiment();
        this.review = readReview();
        this.regression = new SimpleRegression();
    }

    public void start() {
        System.out.println("\n" + review + "\n");
        System.out.println("Loading data....");
        //Loading data to calculate regression
        loadFromJson();

        //Parse user review to get sentiment
        Sentiment sentiment = reviewSentiment.getReviewSentiment(review);
        double rev_X = getReview_X(sentiment);
        System.err.println("User review param X: " + rev_X);

        System.out.println("Calculate regression...");
        calculateRegression(rev_X);

        //What Y should be?
        int orginalVote = askGod(review);
        System.out.println("Orginal Vote: " + orginalVote);

    }

    private String readReview() {
        String fileAsString = null;
        try {
            InputStream is = new FileInputStream("C:\\Users\\Maciej\\Desktop\\IMDb-scraper\\data\\review.txt");
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));

            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line).append("\n");
                line = buf.readLine();
            }
            fileAsString = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileAsString;
    }

    private void loadFromJson() {
        moviesFromJSON = new ArrayList<>();
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(
                    new InputStreamReader(new FileInputStream("C:\\Users\\Maciej\\Desktop\\IMDb-scraper\\data\\file.json")));
            JsonParser jsonParser = new JsonParser();
            JsonArray userarray = jsonParser.parse(reader).getAsJsonArray();
            ArrayList<Movie> moviess = new ArrayList<>();
            for (JsonElement aUser : userarray) {
                Movie aTwitterUser = gson.fromJson(aUser, Movie.class);
                moviess.add(aTwitterUser);
            }

            int error = 0;
            int add = 0;
            for (Movie mov : moviess) {
                moviesFromJSON.add(mov);
                for (Review review : mov.getReviews()) {
                    int y = review.getVote();
                    Sentiment sentiment = review.getSentiments();
                    if (sentiment.getVeryNegative() + sentiment.getNegative() +
                            sentiment.getPositive() + sentiment.getVeryPositive() == 0) {
                        error++;
                    } else {
                        double x = getCoeff(sentiment.getVeryNegative(), sentiment.getNegative(),
                                sentiment.getNeutral(), sentiment.getPositive(), sentiment.getVeryPositive());
                        //Add data to regression model
                        regression.addData(x,y);
                    }
                }
            }
            System.err.println("Skipped reviews: " + error);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double getCoeff(int veryNegative, int negative, int neutral, int positive, int veryPositive) {
        double voteCount = (double)veryNegative + (double)negative + (double)positive + (double)veryPositive;
        double coeff = (((double)veryNegative * -2) + ((double)negative * -1) + ((double)positive) + ((double)veryPositive * 2)) / voteCount;
        return coeff;
    }

    private void calculateRegression(double rev_X) {

        System.out.println("All data accepted.");
        System.out.println("Intercept of regression line: " + regression.getIntercept());
        // displays intercept of regression line

        System.out.println("Slope of regression line: " + regression.getSlope());
        // displays slope of regression line

        System.out.println("Slope standard error: " + regression.getSlopeStdErr());
        // displays slope standard error

        System.out.println("Number of observations in the model :" + regression.getN());
        //displays the number of observations that have been added to the model.

        System.out.println(ANSI_GREEN + "Predicted Y for review: " + regression.predict(rev_X) + ANSI_RESET);
        // displays predicted y value for x = 1.5

    }

    private double getReview_X(Sentiment sentiment) {
        double x = getCoeff(sentiment.getVeryNegative(), sentiment.getNegative(),
                sentiment.getNeutral(), sentiment.getPositive(), sentiment.getVeryPositive());
        return x;
    }

    private int askGod(String review) {
        int orginalVote = 0;
        for (Movie movie : moviesFromJSON) {
            for (Review rev : movie.getReviews()) {
                if (rev.getText().contains(review)) {
                    orginalVote = rev.getVote();
                }
            }
        }
        return orginalVote;
    }
}
