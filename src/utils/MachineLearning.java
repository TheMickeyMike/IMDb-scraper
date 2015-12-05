package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Movie;
import model.Movies;
import model.Review;
import model.Sentiment;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

        int index = -1;

        if ((index = isMovieInDb(movieTittle)) != -1) {
            System.out.println("This movie is in Database\nPlease wait, we are enter your review...");
            String currentDate = getDate();
            Review newReview = new Review(reviewTittle, currentDate, vote, review, sentiment); //Remamber add sentiment!!!
            ArrayList<Review> reviews = movies.get(index).getReviews();
            int beforeTransaction = reviews.size();
            reviews.add(newReview);
            Movie movie = new Movie(movieTittle, reviews);
            movies.set(index, movie);
            //Validate transaction
            if ((movie.getReviews().size() > beforeTransaction) && newReview.getSentiments() != null) {
                System.out.println("Success!");
                System.out.println("Saving new file...");
                CreateJson();
                SerializeThis();
            } else {
                System.err.println("Error! Please try again");
                //TODO back to the beginning
                return;
            }
        } else {
            System.out.println("Wooow, we don't have this movie! Thanks for new one.\nPlease wait, we are enter your review...");
            String currentDate = getDate();
            Review newReview = new Review(reviewTittle, currentDate, vote, review, sentiment); //Remamber add sentiment!!!
            ArrayList<Review> reviews = new ArrayList<>();
            int beforeTransaction = reviews.size();
            int moviesBeforeTransaction = movies.size();
            reviews.add(newReview);
            Movie movie = new Movie(movieTittle, reviews);
            movies.add(movie);
            //Validate transaction
            if ((movie.getReviews().size() > beforeTransaction) && newReview.getSentiments() != null
                    && movies.size() > moviesBeforeTransaction) {
                System.out.println("Success!");
                System.out.println("Saving new file...");
                CreateJson();
                SerializeThis();
            } else {
                System.err.println("Error! Please try again");
                //TODO back to the beginning
                return;
            }
            //TODO add new movie to DB
        }
    }

    private int isMovieInDb(String tittle) {
        for (Movie movie : movies) {
            if (movie.getTittle().contains(tittle.trim()))
                return movies.indexOf(movie);
        }
        return -1;
    }

    private String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = new Date();
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
            FileWriter writer = new FileWriter(PATH_BASE + Serialize.DATA_PATH + "file.json");
            writer.write(data);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("File saved!");
    }

    private void SerializeThis() {
        Movies movieSer = new Movies();
        movieSer.setMovies(movies);
        new Serialize(movieSer);
    }
}
