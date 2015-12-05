package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Movie;
import model.Review;
import model.Sentiment;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by Maciej on 2015-12-05.
 */
public class MachineLearning {
    public static String PATH_BASE = "";

    private String review;
    private Sentiment sentiment;
    private ArrayList<Movie> movies;

    public MachineLearning(String review, Sentiment sentiment, ArrayList<Movie> movies) {
        this.review = review;
        this.sentiment = sentiment;
        this.movies = movies;
        setPath();
        getInfoFromUser();
    }

    private void getInfoFromUser() {
        String movieTittle = null;
        String reviewTittle = null;
        int vote = 0;
        Scanner scanIn;
        do {
            System.out.println("\nEnter movie tittle : ");
            scanIn = new Scanner(System.in);
            movieTittle = scanIn.nextLine();
        } while (movieTittle.equals(""));
        do {
            System.out.println("\nEnter review tittle : ");
            scanIn = new Scanner(System.in);
            reviewTittle = scanIn.nextLine();
        } while (reviewTittle.equals(""));
        do {
            System.out.println("\nEnter vote : ");
            scanIn = new Scanner(System.in);
            vote = scanIn.nextInt();
        } while (vote < 1 && vote > 10);

        scanIn.close();
        System.out.println(movieTittle);
        int index = -1;
        if ((index = isMovieinDb(movieTittle)) != -1) {
            System.out.println("This movie is in Database\nPlease wait, we are enter your review...");
            ArrayList<Review> reviews = movies.get(index).getReviews();
            reviews.add();

        } else {
            //TODO add new movie to DB
        }
        getDate();
    }

    private int isMovieinDb(String tittle) {
        for (Movie movie : movies) {
            if (movie.getTittle().contains(tittle.trim()))
                return movies.indexOf(movie);
        }
        return -1;
    }

    private String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        return dateFormat.format(date);
    }

    private static void setPath() {
        Path currentRelativePath = Paths.get("");
        PATH_BASE = currentRelativePath.toAbsolutePath().toString() + "/";
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

}
