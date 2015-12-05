import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import model.*;
import model.Sentiment;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Maciej on 2015-12-05.
 */
public class Regression {
    private String review;
    private HashMap<Integer, Integer> regData;
    private SimpleRegression regression;

    public Regression() {
        this.review = readReview();
        this.regData = new HashMap<>();
    }

    public void start() {
        System.out.println("Loading data....");
        //Loading data to calculate regression
        loadFromJson();
        System.out.println("Calculate regression...");
        calculateRegression(2);

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

            for (Movie mov : moviess) {
                for (Review review : mov.getReviews()) {
                    int y = review.getVote();
                    Sentiment sentiment = review.getSentiments();
                    int x = getCoeff(sentiment.getVeryNegative(), sentiment.getNegative(),
                            sentiment.getNeutral(), sentiment.getPositive(), sentiment.getVeryPositive());
                    regData.put(x, y);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getCoeff(int veryNegative, int negative, int neutral, int positive, int veryPositive) {
        int coeff = (veryNegative * 2) + (negative) + (positive) + (veryPositive * 2);
        return coeff;
    }

    private void calculateRegression(int rev_X) {
        regression = new SimpleRegression();

        //Set up pool data
        for (Map.Entry<Integer, Integer> entry : regData.entrySet()) {
            int x = entry.getKey();
            int y = entry.getValue();
            regression.addData(x, y);
        }
        System.out.println("All data accepted.");
        System.out.println("Intercept of regression line: " + regression.getIntercept());
        // displays intercept of regression line

        System.out.println("Slope of regression line: " + regression.getSlope());
        // displays slope of regression line

        System.out.println("Slope standard error: " + regression.getSlopeStdErr());
        // displays slope standard error

        System.out.println("Predicted Y for review: " + regression.predict(1.5d));
        // displays predicted y value for x = 1.5

    }

}
